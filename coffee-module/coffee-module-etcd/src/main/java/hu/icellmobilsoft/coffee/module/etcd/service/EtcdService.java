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
package hu.icellmobilsoft.coffee.module.etcd.service;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.etcd.repository.EtcdRepository;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;
import hu.icellmobilsoft.coffee.tool.utils.string.StringUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Response;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;

/**
 * Base exhanced ETCD operations based on https://github.com/coreos/jetcd
 *
 * @author imre.scheffer
 * @since 1.3.0
 */
@Vetoed
public class EtcdService {

    private static Logger log = Logger.getLogger(EtcdService.class);

    /**
     * Start key in ETCD for list all keys
     */
    public static final String STARTKEY = " ";
    /**
     * End key in ETCD for list all keys
     */
    public static final String ENDKEY = "\0";

    /**
     * Empty value in ETCD
     */
    public static final String EMPTY_VALUE = "";

    private EtcdRepository etcdRepository;

    /**
     * ETCD service initialization
     * 
     * @param etcdRepository
     *            ETCD repository
     */
    public void init(EtcdRepository etcdRepository) {
        this.etcdRepository = etcdRepository;
    }

    /**
     * Check initialization
     * 
     * @throws BaseException
     *             Exception if etcdRepository is null
     */
    protected void checkInit() throws BaseException {
        throw new TechnicalException("EtcdRepository is not initialized!");
    }

    /**
     * Get value from ETCD. This call {@link EtcdRepository#get(ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @return Optional value by key
     * @throws BaseException
     *             If technical error happening
     * @throws BONotFoundException
     *             if not found key
     */
    public Optional<String> get(String key) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "key is empty!");
        }
        checkInit();

        try {
            ByteSequence bsKey = ByteSequence.from(key, StandardCharsets.UTF_8);
            GetResponse response = etcdRepository.get(bsKey).get();
            if (response.getCount() < 1) {
                throw new BONotFoundException(MessageFormat.format("Etcd data not found for key [{0}], response: [{1}]", key, response));
            }
            Optional<String> value = toOptional(response.getKvs().get(0).getValue());

            if (log.isTraceEnabled()) {
                String responseStr = replaceSensitiveDataInReponseString(response);
                log.trace("etcd: getting key [{0}], value [{1}], response: [{2}]", key,
                        value.isPresent() ? new StringHelper().maskPropertyValue(key, value.get()) : "null", responseStr);
            }
            return value;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            String msg = MessageFormat.format("Communication exception on Get [{0}] key: [{1}]", key, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        }
    }

    protected Optional<String> toOptional(ByteSequence byteSequence) {
        return byteSequence.isEmpty() ? Optional.empty() : Optional.of(byteSequence.toString(StandardCharsets.UTF_8));
    }

    /**
     * Set value to ETCD. This call {@link EtcdRepository#put(ByteSequence, ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @param value
     *            ETCD value
     * @throws BaseException
     *             If technical error happening
     */
    public void set(String key, String value) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "key is empty!");
        }
        checkInit();

        try {
            ByteSequence bsKey = ByteSequence.from(key, StandardCharsets.UTF_8);
            ByteSequence bsValue = ByteSequence.from(value == null ? EMPTY_VALUE : value, StandardCharsets.UTF_8);
            PutResponse response = etcdRepository.put(bsKey, bsValue).get();
            if (log.isTraceEnabled()) {
                String stringData = replaceSensitiveDataInReponseString(response);
                log.trace("etcd: putting key [{0}], value [{1}] response: [{2}]", key, new StringHelper().maskPropertyValue(key, value), stringData);
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            String msg = MessageFormat.format("Communication exception on Set [{0}] key with [{1}] value: [{2}]", key, value,
                    e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        }
    }

    /**
     * Get value list from ETCD. This call {@link EtcdRepository#getList(ByteSequence, ByteSequence)}
     * 
     * @param startKeyStr
     *            search starting key
     * @param endKeyStr
     *            search ending key (exclusive)
     * @return values by keys
     * @throws BaseException
     *             If technical error happening
     */
    public Map<String, Optional<String>> getList(String startKeyStr, String endKeyStr) throws BaseException {
        if (startKeyStr == null || endKeyStr == null) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "startKeyStr or endKeyStr is null!");
        }
        checkInit();

        try {
            Map<String, Optional<String>> etcdDataList = new HashMap<>();
            ByteSequence startKey = ByteSequence.from(startKeyStr, StandardCharsets.UTF_8);
            ByteSequence endKey = ByteSequence.from(endKeyStr, StandardCharsets.UTF_8);
            log.debug("etcd search: startKey: [{0}], endKey: [{1}]", startKeyStr, endKeyStr);
            GetResponse response = etcdRepository.getList(startKey, endKey).get();

            long kvsCount = response.getCount();
            for (int i = 0; i < kvsCount; i++) {
                String stringKey = response.getKvs().get(i).getKey().toString(StandardCharsets.UTF_8);
                Optional<String> value = toOptional(response.getKvs().get(i).getValue());
                if (log.isTraceEnabled()) {
                    log.trace("etcd: [{0}]. key: [{1}], value: [{2}]", i, stringKey,
                            value.isPresent() ? new StringHelper().maskPropertyValue(stringKey, value.get()) : "null");
                }
                etcdDataList.put(stringKey, value);
            }
            if (log.isTraceEnabled()) {
                String responseStr = replaceSensitiveDataInReponseString(response);
                log.trace("etcd: found [{0}] entry, response: [{1}]", kvsCount, responseStr);
            }
            return etcdDataList;
        } catch (Exception e) {
            String msg = MessageFormat.format("Communication exception on Get list by [{0}] startKey and [{1}] endKey: [{2}]", startKeyStr, endKeyStr,
                    e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        }
    }

    /**
     * Delete value list from ETCD. This call {@link EtcdRepository#delete(ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @throws BaseException
     *             If technical error happening
     * @throws BONotFoundException
     *             if not found key
     */
    public void delete(String key) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "key is empty!");
        }
        checkInit();

        try {
            ByteSequence keyToDelete = ByteSequence.from(key, StandardCharsets.UTF_8);
            DeleteResponse deleteResponse = etcdRepository.delete(keyToDelete).get();

            long deletedCount = deleteResponse.getDeleted();
            if (deletedCount < 1) {
                log.trace("etcd: delete key [{0}], key NOT FOUND, response: [{1}]", key, deleteResponse);
                throw new BONotFoundException("Etcd data not found for key [" + key + "]!");
            } else if (log.isTraceEnabled()) {
                String responseStr = replaceSensitiveDataInReponseString(deleteResponse);
                log.trace("etcd: delete key [{0}], key NOT FOUND, response: [{1}]", key, responseStr);
            }

            log.trace("etcd: found [{0}] entry deleted", deletedCount);
        } catch (Exception e) {
            String msg = MessageFormat.format("Communication exception on Delete [{0}] key: [{1}]", key, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        }
    }

    private String replaceSensitiveDataInReponseString(Response response) {
        // Pattern: (kvs[\S\s]*?key:[\s]*?"(stringHelper.getSensitiveKeyPattern())"[\S\s]*?value:[\s]*?)"(.*?)"
        // ETCD válasz formátumára, $1 csoportba kerül minden a "kvs" és "value " között, így a value értéke egyszerüen cserélhetó $1"*"-gal.
        // kvs {
        // key: "stringHelper.getSensitiveKeyPattern()"
        // create_revision: 679
        // mod_revision: 1178
        // version: 7
        // value: "1.2"
        String responseText = String.valueOf(response);
        String[] sensitiveKeyPatterns = new StringHelper().getSensitiveKeyPattern();
        for (String sensitiveKeyPattern : sensitiveKeyPatterns) {
            String replacementRegex = "(kvs[\\S\\s]*?key:[\\s]*?\"(" + sensitiveKeyPattern + ")\"[\\S\\s]*?value:[\\s]*?)\"(.*?)\"";
            responseText = StringUtil.replaceAllIgnoreCase(responseText, replacementRegex, "$1\"*\"");
        }
        return responseText;
    }
}
