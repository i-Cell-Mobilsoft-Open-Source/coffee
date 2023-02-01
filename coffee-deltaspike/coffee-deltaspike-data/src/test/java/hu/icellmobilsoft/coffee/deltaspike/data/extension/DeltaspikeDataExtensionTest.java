/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.deltaspike.data.extension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.apache.deltaspike.data.impl.meta.RepositoryMetadata;
import org.apache.deltaspike.data.impl.meta.RepositoryMetadataHandler;
import org.apache.deltaspike.data.impl.meta.RepositoryMethodMetadata;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test {@link RepositoryExtension} and dynamic proxy creation with method delegate handlers
 * 
 * @author czenczl
 * @since 2.0.0
 *
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Deltaspike Data extension tests")
class DeltaspikeDataExtensionTest {

    @Inject
    RepositoryExtension repositoryExtension;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld().enableDiscovery().addExtensions(RepositoryExtension.class))
            .addBeans(MockBean.of(mock(EntityManager.class), EntityManager.class)).build();

    @Test
    @DisplayName("repository extension test")
    void repositoryExtension() {
        Assertions.assertEquals(SampleEntityRepository.class, repositoryExtension.getRepositoryClasses().get(0));
    }

    @Test
    @DisplayName("Check repository injects")
    void repositoryInject() {
        SampleEntityRepository weldSampleRepositroy = weld.select(SampleEntityRepository.class).get();
        Assertions.assertNotNull(weldSampleRepositroy);

        SampleEntityRepository sampleRepository = CDI.current().select(SampleEntityRepository.class).get();
        Assertions.assertNotNull(sampleRepository);
    }

    @Test
    @DisplayName("Check repository dynamic proxy call")
    void repositoryDynamicProxyCall() {
        SampleEntityRepository sampleRepository = CDI.current().select(SampleEntityRepository.class).get();
        Assertions.assertNotNull(sampleRepository);

        sampleRepository.findBy("mock");

        Query mockedQuery = mock(Query.class);
        when(mockedQuery.getSingleResult()).thenReturn(new SampleEntity());
        EntityManager em = CDI.current().select(EntityManager.class).get();

        when(em.createNamedQuery("select i from SampleEntity i")).thenReturn(mockedQuery);
        when(em.createQuery("select i from SampleEntity i")).thenReturn(mockedQuery);

        sampleRepository.findByCustom();
        sampleRepository.findAllByCustom();
    }

    @Test
    @DisplayName("Check repostory metadata")
    void repositoryMetadata() {

        RepositoryMetadataHandler repositoryMetadataHandler = CDI.current().select(RepositoryMetadataHandler.class).get();
        RepositoryMetadata repositoryMetadata = repositoryMetadataHandler.lookupMetadata(List.of(SampleEntityRepository.class));

        Assertions.assertEquals(SampleEntity.class, repositoryMetadata.getEntityMetadata().getEntityClass());
        Assertions.assertEquals(SampleEntity.class.getSimpleName(), repositoryMetadata.getEntityMetadata().getEntityName());

        boolean foundCustomMethod = false;
        for (Map.Entry<Method, RepositoryMethodMetadata> entry : repositoryMetadata.getMethodsMetadata().entrySet()) {
            if (entry.getKey().getName().equals("findByCustom")) {
                foundCustomMethod = true;
                break;
            }
        }
        Assertions.assertTrue(foundCustomMethod);
    }

}
