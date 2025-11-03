/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import jakarta.persistence.QueryHint;

import org.apache.deltaspike.data.api.Query;
import org.hibernate.jpa.HibernateHints;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.docgen.common.processor.AbstractDocGenProcessor;
import hu.icellmobilsoft.coffee.module.docgen.common.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.config.SqlCatalogConfig;
import hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.data.SqlCatalogData;

/**
 * Annotation processor for {@link Query}
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
@AutoService(Processor.class)
public class QueryProcessor extends AbstractDocGenProcessor<SqlCatalogConfig, SqlCatalogData> {

    /**
     * Default constructor, constructs a new object.
     */
    public QueryProcessor() {
        super();
    }

    @Override
    protected SqlCatalogConfig getConfig() {
        return new SqlCatalogConfig(processingEnv.getOptions());
    }

    @Override
    protected List<SqlCatalogData> collectDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return annotations.stream().map(roundEnv::getElementsAnnotatedWith).flatMap(Set::stream).map(this::createSqlCatalogData).toList();
    }

    private SqlCatalogData createSqlCatalogData(Element element) {
        String className = String.valueOf(element.getEnclosingElement().getSimpleName());
        String methodName = String.valueOf(element.getSimpleName());
        String jpql = element.getAnnotation(Query.class).value();
        String comment = Arrays.stream(element.getAnnotation(Query.class).hints())
                .filter(hint -> hint.name().equals(HibernateHints.HINT_COMMENT))
                .map(QueryHint::value)
                .findFirst()
                .orElse("");
        return new SqlCatalogData(className, methodName, jpql, comment);
    }

    @Override
    protected List<SqlCatalogData> sortData(List<SqlCatalogData> dataList) {
        return dataList.stream().sorted(Comparator.comparing(SqlCatalogData::className).thenComparing(SqlCatalogData::methodName)).toList();
    }

    @Override
    protected IDocWriter<SqlCatalogData> getDocWriter(SqlCatalogConfig config) {
        return new SqlCatalogAsciiDocWriter();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Query.class.getCanonicalName());
    }
}
