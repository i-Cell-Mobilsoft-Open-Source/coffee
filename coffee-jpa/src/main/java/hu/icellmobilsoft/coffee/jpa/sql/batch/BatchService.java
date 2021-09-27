/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.coffee.jpa.sql.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.util.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.Update;
import org.hibernate.type.CustomType;
import org.hibernate.type.EnumType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.jpa.sql.batch.enums.Status;
import hu.icellmobilsoft.coffee.jpa.sql.entity.EntityHelper;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * Batch mentessekkel foglalkozo osztaly
 *
 * @author imre.scheffer
 * @author robert.kaplar
 * @since 1.0.0
 */
public class BatchService {

    /** Constant <code>BATCH_SIZE=100</code> */
    public static final int BATCH_SIZE = 100;

    @Inject
    @ThisLogger
    private AppLogger log;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    private EntityManager entityManager;

    private String sqlPostfix;

    /**
     * input validalas.
     * 
     * @param entities
     *            - validalni kivant collection
     * @throws BaseException
     *             exception
     */
    protected void validateInput(Collection<?> entities) throws BaseException {
        if (entities == null) {
            log.warn("entities is null skipped to save!");
            throw new BaseException("entity is null!");
        }
        if (entities.isEmpty()) {
            log.debug("No entity in list, skip merge.");
        }
    }

    /**
     * Hibernate batch mentes. Ezt akkor erdemes hasznalni, amikor a memoria optimalizalas vegett tul sokat kell menteni, de hibernate-en keresztul.
     * Mentesi sebessegen nem gyorsit, de a memoria igenyeket jocskan lejjebb viszi
     *
     * @param <E>
     *            - entitas tipusa
     * @param entities
     *            - merge-olni kivant collection
     * @return merge-elt entitas id-k {@link List}-je
     * @throws BaseException
     *             exception
     */
    public <E> List<String> batchMerge(Collection<E> entities) throws BaseException {
        validateInput(entities);
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        String entityName = entities.iterator().next().getClass().getSimpleName();
        log.debug(">> batchMerge: [{0}] list of [{1}] elements", entityName, entities.size());
        StatelessSession statelessSession = null;
        try {
            // ha nincs tranzakció nem szabad hogy autoCommit történjen
            entityManager.joinTransaction();

            Session session = entityManager.unwrap(Session.class);
            SessionFactory sessionFactory = session.getSessionFactory();
            statelessSession = sessionFactory.openStatelessSession();

            log.debug(">> batchMerge: start");
            List<String> ids = new ArrayList<>();
            for (E entity : entities) {
                // ezt az entitas ki kell szedni az entityManagerbol,
                // kulonben ugy fogja erzekelni hogy az adat mar valtozott masik tranzakcioban
                entityManager.detach(entity);

                String entityId = getId(entity);
                if (entityId == null) {
                    String id = (String) statelessSession.insert(entity);
                    ids.add(id);
                } else {
                    statelessSession.update(entity);
                    ids.add(entityId);
                }
            }
            log.debug(">> batchMerge: end");

            return ids;
        } catch (Exception e) {
            String msg = MessageFormat.format("Error in batch merge [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_SAVE_FAILED, msg, e);
        } finally {
            log.debug("<< batchMerge: [{0}] list of [{1}] elements", entityName, entities.size());
        }
    }

    /**
     * Szetvalogatja a beerkezo entitasokat az szerint hogy az id ki van-e toltve vagy sem es aszerint kuldi be a megfelelo metodusokba.<br>
     * Klasszikus PreparedStatement alapon mukododo batch mentes. A SQL osszeallitasara a hibernate dolgai vannak felhasznalva, de a futas mar
     * klasszikusan folyik. Nagyon gyors a mentes, kicsi memoria hasznalattal
     *
     * @param <E>
     *            - entitas tipusa
     * @param entities
     *            - merge-olni kivant collection
     * @param clazz
     *            - a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az merge-elt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     * @see #batchInsertNative(Collection, Class)
     * @see #batchUpdateNative(Collection, Class)
     */
    public <E> Map<String, Status> batchMergeNative(Collection<E> entities, Class<E> clazz) throws BaseException {
        validateInput(entities);
        if (entities.isEmpty()) {
            return Collections.emptyMap();
        }
        List<E> insert = entities.stream().filter(e -> getId(e) == null).collect(Collectors.toList());
        List<E> update = entities.stream().filter(e -> getId(e) != null).collect(Collectors.toList());
        Map<String, Status> mergeResult = new HashMap<>();
        mergeResult.putAll(batchInsertNative(insert, clazz));
        mergeResult.putAll(batchUpdateNative(update, clazz));
        return mergeResult;
    }

    /**
     * Klasszikus PreparedStatement alapon mukododo batch update mentes. A SQL osszeallitasara a hibernate dolgai vannak felhasznalva, de a futas mar
     * klasszikusan folyik. Nagyon gyors a mentes, kicsi memoria hasznalattal
     *
     * @param <E>
     *            - entitas tipusa
     * @param entities
     *            - update-elni kivant collection
     * @param clazz
     *            - a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az update-elt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    public <E> Map<String, Status> batchUpdateNative(Collection<E> entities, Class<E> clazz) throws BaseException {
        validateInput(entities);
        if (entities.isEmpty()) {
            return Collections.emptyMap();
        }
        String entityName = entities.iterator().next().getClass().getSimpleName();
        log.debug(">> batchMerge: [{0}] list of [{1}] elements", entityName, entities.size());

        Map<String, Status> result = new HashMap<>();
        try {
            // ha nincs tranzakció nem szabad hogy autoCommit történjen az executeBatch-ben
            entityManager.joinTransaction();

            Session session = entityManager.unwrap(Session.class);
            MetamodelImplementor metamodel = (MetamodelImplementor) entityManager.getMetamodel();
            SingleTableEntityPersister persister = (SingleTableEntityPersister) metamodel.entityPersister(clazz);
            String[] names = persister.getPropertyNames();

            SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
            Update u = new Update(sfi.getJdbcServices().getDialect());
            u.setPrimaryKeyColumnNames(persister.getIdentifierColumnNames());
            u.setTableName(persister.getTableName());
            u.setVersionColumnName(persister.getVersionColumnName());
            for (String name : names) {
                u.addColumns(persister.getPropertyColumnNames(name));
            }
            // where column automatan belekerul
            // u.addWhereColumn(persister.getIdentifierColumnNames()[0]);
            // u.addWhereColumn(persister.getVersionColumnName());

            String sql = u.toStatementString() + StringUtils.defaultString(getSqlPostfix());

            // String sql = update.toString();
            log.debug("Running update:\n[{0}]", sql);

            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int i = 1;
                    log.debug(">> batchMerge: start");

                    // temporalis lista, max {batchSize} elemet tartalmaz
                    List<String> tmpProcessingEntities = new ArrayList<>(batchSize());

                    for (E entity : entities) {
                        handleUpdateAudit(entity);

                        // klasszikus parameter betoltesek: ps.setLong(1, entity.getVersion() + 1);
                        setParametersForUpdate(ps, persister, entity);
                        ps.addBatch();

                        // ezt az entitast ki kell szedni az entityManagerbol,
                        // kulonben ugy fogja erzekelni hogy az adat mar valtozott masik tranzakcioban
                        entityManager.detach(entity);

                        // mivel {batchSize} csomagokban hajtjuk vegre a muveletet, meg kell jelolnunk azokat az entitasokat amiken vegigmegyunk
                        tmpProcessingEntities.add(getId(entity));

                        if (i % batchSize() == 0) {
                            executeBatch(result, ps, tmpProcessingEntities);
                            tmpProcessingEntities.clear();
                        }
                        i++;

                    }

                    if (!CollectionUtils.isEmpty(tmpProcessingEntities)) {
                        executeBatch(result, ps, tmpProcessingEntities);
                    }

                    log.debug(">> batchMerge: end");
                } catch (SQLException e) {
                    String msg = MessageFormat.format("SQLException in batch merge [{0}]: [{1}]", entityName, e.getLocalizedMessage());
                    log.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            });
            return result;
        } catch (Exception e) {
            String msg = MessageFormat.format("Exception in batch merge [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_SAVE_FAILED, msg, e);
        } finally {
            log.debug("<< batchMerge: [{0}] list of [{1}] elements", entityName, entities.size());
        }
    }

    /**
     * Klasszikus PreparedStatement alapon mukododo batch insert mentes. A SQL osszeallitasara a hibernate dolgai vannak felhasznalva, de a futas mar
     * klasszikusan folyik. Nagyon gyors a mentes, kicsi memoria hasznalattal
     *
     * @param <E>
     *            - entitas tipusa
     * @param entities
     *            - insertalni kivant collection
     * @param clazz
     *            - a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az insertalt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    public <E> Map<String, Status> batchInsertNative(Collection<E> entities, Class<E> clazz) throws BaseException {
        validateInput(entities);
        if (entities.isEmpty()) {
            return Collections.emptyMap();
        }
        String entityName = entities.iterator().next().getClass().getSimpleName();
        log.debug(">> batchInsertNative: [{0}] list of [{1}] elements", entityName, entities.size());

        Map<String, Status> result = new HashMap<>();
        try {
            // ha nincs tranzakció nem szabad hogy autoCommit történjen az executeBatch-ben
            entityManager.joinTransaction();

            Session session = entityManager.unwrap(Session.class);
            MetamodelImplementor metamodel = (MetamodelImplementor) entityManager.getMetamodel();
            SingleTableEntityPersister persister = (SingleTableEntityPersister) metamodel.entityPersister(clazz);
            String[] names = persister.getPropertyNames();

            SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
            // IdentifierGenerator ig = persister.getEntityMetamodel().getIdentifierProperty().getIdentifierGenerator();
            Insert insert = new Insert(sfi.getJdbcServices().getDialect());
            insert.setTableName(persister.getTableName());
            insert.addColumn(persister.getRootTableKeyColumnNames()[0], "?");
            // insert.addIdentityColumn(persister.getIdentifierColumnNames()[0]);
            for (String name : names) {
                insert.addColumns(persister.getPropertyColumnNames(name));
            }

            String sql = insert.toStatementString() + StringUtils.defaultString(getSqlPostfix());
            log.debug("Running insert:\n[{0}]", sql);
            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    int i = 1;
                    log.debug(">> batchInsertNative: start");

                    // temporalis lista, max {batchSize} elemet tartalmaz
                    List<String> tmpProcessingEntities = new ArrayList<>(batchSize());

                    for (E entity : entities) {
                        handleInsertAudit(entity);

                        // klasszikus parameter betoltesek: ps.setLong(1, entity.getVersion() + 1);
                        setParametersForInsert(ps, persister, entity);
                        ps.addBatch();

                        // mivel {batchSize} csomagokban hajtjuk vegre a muveletet, meg kell jelolnunk azokat az entitasokat amiken vegigmegyunk
                        tmpProcessingEntities.add(getId(entity));

                        if (i % batchSize() == 0) {
                            executeBatch(result, ps, tmpProcessingEntities);
                            tmpProcessingEntities.clear();
                        }
                        i++;

                    }

                    if (!CollectionUtils.isEmpty(tmpProcessingEntities)) {
                        executeBatch(result, ps, tmpProcessingEntities);
                    }

                    log.debug(">> batchInsertNative: end");
                } catch (SQLException e) {
                    String msg = MessageFormat.format("SQLException in batch insert [{0}]: [{1}]", entityName, e.getLocalizedMessage());
                    log.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            });
            return result;
        } catch (Exception e) {
            String msg = MessageFormat.format("Exception in batch insert [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_SAVE_FAILED, msg, e);
        } finally {
            log.debug("<< batchInsertNative: [{0}] list of [{1}] elements", entityName, entities.size());
        }
    }

    /**
     * Klasszikus PreparedStatement alapon mukododo batch delete. A SQL osszeallitasara a hibernate dolgai vannak felhasznalva, de a futas mar
     * klasszikusan folyik. Nagyon gyors a törlés, kicsi memoria hasznalattal
     *
     * @param <E>
     *            - entitas tipusa
     * @param entities
     *            - torolni kivant collection
     * @param clazz
     *            - a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az torolt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    public <E> Map<String, Status> batchDeleteNative(Collection<E> entities, Class<E> clazz) throws BaseException {
        validateInput(entities);
        if (entities.isEmpty()) {
            return Collections.emptyMap();
        }
        String entityName = entities.iterator().next().getClass().getSimpleName();
        log.debug(">> batchDeleteNative: [{0}] list of [{1}] elements", entityName, entities.size());

        Map<String, Status> result = new HashMap<>();
        try {
            // ha nincs tranzakció nem szabad hogy autoCommit történjen az executeBatch-ben
            entityManager.joinTransaction();

            Session session = entityManager.unwrap(Session.class);
            MetamodelImplementor metamodel = (MetamodelImplementor) entityManager.getMetamodel();
            SingleTableEntityPersister persister = (SingleTableEntityPersister) metamodel.entityPersister(clazz);

            Delete delete = new Delete();
            delete.setTableName(persister.getTableName());
            delete.addPrimaryKeyColumn(persister.getRootTableKeyColumnNames()[0], "?");

            String sql = delete.toStatementString();
            log.debug("Running delete:\n[{0}]", sql);
            session.doWork(connection -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    log.debug(">> batchDeleteNative: start");
                    int i = 1;

                    // temporalis lista, max {batchSize} elemet tartalmaz
                    List<String> tmpProcessingEntities = new ArrayList<>(batchSize());

                    for (E entity : entities) {

                        String entityId = getId(entity);
                        ps.setString(1, entityId);
                        ps.addBatch();

                        // mivel {batchSize} csomagokban hajtjuk vegre a muveletet, meg kell jelolnunk azokat az entitasokat amiken vegigmegyunk
                        tmpProcessingEntities.add(entityId);

                        if (i % batchSize() == 0) {
                            executeBatch(result, ps, tmpProcessingEntities);
                            tmpProcessingEntities.clear();
                        }
                        i++;

                    }

                    if (!CollectionUtils.isEmpty(tmpProcessingEntities)) {
                        executeBatch(result, ps, tmpProcessingEntities);
                    }

                    log.debug(">> batchDeleteNative: end");
                } catch (SQLException e) {
                    String msg = MessageFormat.format("SQLException in batch Delete [{0}]: [{1}]", entityName, e.getLocalizedMessage());
                    log.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            });
            return result;
        } catch (Exception e) {
            String msg = MessageFormat.format("Exception in batch delete [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_DELETE_FAILED, msg, e);
        } finally {
            log.debug("<< batchDeleteNative: [{0}] list of [{1}] elements", entityName, entities.size());
        }
    }

    /**
     * Parameterek beallitasa az update szamara.
     * 
     * @param <E>
     *            - entitas tipusa
     * @param ps
     *            - beallitando preparedStatement
     * @param persister
     *            - persister
     * @param entity
     *            - modositani kivant entitas
     * @throws SQLException
     *             exception
     */
    protected <E> void setParametersForUpdate(PreparedStatement ps, SingleTableEntityPersister persister, E entity) throws SQLException {
        int i = 1;
        Long version = (Long) persister.getVersion(entity);
        int versionIndex = persister.getVersionProperty();
        for (String name : persister.getPropertyNames()) {
            // remeljuk az index es a persister.getPropertyNames() osszhangban van
            int index = persister.getPropertyIndex(name);
            if (versionIndex == index) {
                ps.setLong(i, version + 1);
                i++;
                continue;
            }
            Object value = persister.getPropertyValue(entity, index);
            Type type = persister.getPropertyType(name);
            setPsObject(ps, i, type, value);
            i++;
        }
        // where
        ps.setObject(i++, persister.getIdentifier(entity));
        ps.setObject(i, persister.getVersion(entity));
    }

    /**
     * Parameterek beallitasa az insert szamara.
     * 
     * @param <E>
     *            - entitas tipusa
     * @param ps
     *            - beallitando preparedStatement
     * @param persister
     *            - persister
     * @param entity
     *            - beszurni kivant entitas
     * @throws SQLException
     *             exception
     */
    protected <E> void setParametersForInsert(PreparedStatement ps, SingleTableEntityPersister persister, E entity) throws SQLException {
        int i = 1;
        int versionIndex = persister.getVersionProperty();
        // elso a PK
        String entityId = (String) persister.getIdentifier(entity);
        // String entityId = getId(entity);
        if (entityId == null) {
            entityId = generateId();
            persister.setIdentifier(entity, entityId, (SharedSessionContractImplementor) null);
        }
        setPsObject(ps, i++, StringType.INSTANCE, entityId);
        for (String name : persister.getPropertyNames()) {
            // remeljuk az index es a persister.getPropertyNames() osszhangban van
            int index = persister.getPropertyIndex(name);
            if (versionIndex == index) {
                ps.setLong(i, 0);
                i++;
                continue;
            }
            Object value = persister.getPropertyValue(entity, index);
            Type type = persister.getPropertyType(name);
            setPsObject(ps, i, type, value);
            i++;
        }
    }

    /**
     * Adott objektum {@link PreparedStatement}-be valo behelyettesitese.
     * 
     * @param <E>
     *            - entitas tipusa
     * @param ps
     *            - beallitando preparedStatement
     * @param parameterIndex
     *            - parameter indexe
     * @param type
     *            - parameter tipusa
     * @param value
     *            - parameter erteke
     * @throws SQLException
     *             exception
     */
    protected <E> void setPsObject(PreparedStatement ps, int parameterIndex, Type type, Object value) throws SQLException {
        // enumokat le kell kezelni
        if (type instanceof CustomType) {
            if (((CustomType) type).getUserType() instanceof EnumType) {
                if (value != null) {
                    value = ((Enum<?>) value).name();
                }
            } else {
                log.debug("Unhandled custom type: [{0}]", type.getName());
                // a setOject el fog szalni
            }
            ps.setObject(parameterIndex, value);
        } else if (type instanceof TimestampType) {
            ps.setObject(parameterIndex, value, TimestampType.INSTANCE.sqlType());
        } else if (type instanceof ManyToOneType) {
            E manyToOneEntity = (E) value;
            ps.setObject(parameterIndex, manyToOneEntity != null ? EntityHelper.getLazyId(manyToOneEntity) : null);
        } else {
            ps.setObject(parameterIndex, value);
        }
    }

    /**
     * Audit bejegyzes beszurasanak kezelese
     * 
     * @param <E>
     *            - entitas tipusa
     * @param entity
     *            - entitas
     */
    protected <E> void handleInsertAudit(E entity) {
    }

    /**
     * Audit bejegyzes modositasanak kezelese.
     * 
     * @param <E>
     *            - entitas tipusa
     * @param entity
     *            - entitas
     */
    protected <E> void handleUpdateAudit(E entity) {
    }

    /**
     * ID generalas
     * 
     * @return random id
     */
    protected String generateId() {
        return RandomUtil.generateId();
    }

    /**
     * Batch merete
     * 
     * @return batch meret
     */
    protected int batchSize() {
        return BATCH_SIZE;
    }

    /**
     * Parameterkent kapott <code>java.util.Date</code> visszadasa <code>java.sql.Timestamp</code>-kent
     * 
     * @param date
     *            - datum
     * @return {@link Timestamp}
     */
    public static Timestamp getTimestamp(Date date) {
        return date == null ? null : new java.sql.Timestamp(date.getTime());
    }

    /**
     * Getter for the field <code>sqlPostfix</code>.
     * 
     * @return SQL postfix
     */
    public String getSqlPostfix() {
        return sqlPostfix;
    }

    /**
     * Setter for the field <code>sqlPostfix</code>.
     * 
     * @param sqlPostfix
     *            - postfix
     */
    public void setSqlPostfix(String sqlPostfix) {
        this.sqlPostfix = sqlPostfix;
    }

    /**
     * Entity id visszadasa.
     * 
     * @param entity
     *            - entitas
     * @return entity id
     */
    protected String getId(Object entity) {
        return (String) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    private <E> void executeBatch(Map<String, Status> result, PreparedStatement ps, List<String> tmpProcessedEntities) throws SQLException {
        int[] executeBatchResult = ps.executeBatch();
        addBatchResult(result, tmpProcessedEntities, executeBatchResult);
    }

    private void addBatchResult(Map<String, Status> result, List<String> entityIds, int[] batchResult) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return;
        } else if (entityIds.size() < batchResult.length) {
            throw new IllegalArgumentException("Each batchResult must have an associated entityId!");
        }

        for (int i = 0; i < entityIds.size(); i++) {
            Status status;
            if (i < batchResult.length) {
                int resultCode = batchResult[i];
                status = getStatus(resultCode);
            } else {
                // kevesebb batchResult jött vissza, mint entityId (pl Oracle hajlamos erre, ha a batch utolsó eleme failel el, és log error reject
                // limittel fut), nem tudjuk mi lett a hiányzó rekorddal, UNKNOWN státuszt jelölünk
                status = Status.UNKNOWN;
            }
            result.put(entityIds.get(i), status);
        }
    }

    private Status getStatus(int resultCode) {
        if (resultCode > 0) {
            return Status.SUCCESS.setRowsAffected(resultCode);
        } else {
            switch (resultCode) {
            case 0:
                return Status.SUCCESS_NO_UPDATE;
            case Statement.SUCCESS_NO_INFO:
                return Status.SUCCESS_NO_INFO;
            case Statement.EXECUTE_FAILED:
                return Status.EXECUTE_FAILED;
            default:
                return Status.UNKNOWN;
            }
        }
    }
}
