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
package hu.icellmobilsoft.coffee.module.etcd.repository;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;

/**
 * https://github.com/coreos/jetcd
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class EtcdRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Client etcdClient;

    /**
     * <p>put.</p>
     */
    public CompletableFuture<PutResponse> put(ByteSequence key, ByteSequence value) throws BaseException {
        try {
            return etcdClient.getKVClient().put(key, value);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in put key [" + key + "] and value [" + value + "] into etcd: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>get.</p>
     */
    public CompletableFuture<GetResponse> get(ByteSequence key) throws BaseException {
        try {
            return etcdClient.getKVClient().get(key);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Exception in get key [" + key + "] into etcd: " + e.getLocalizedMessage(),
                    e);
        }
    }

    /**
     * <p>delete.</p>
     */
    public CompletableFuture<DeleteResponse> delete(ByteSequence key) throws BaseException {
        try {
            return etcdClient.getKVClient().delete(key);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Exception in deleting key [" + key + "] in etcd: " + e.getLocalizedMessage(),
                    e);
        }
    }

    /**
     * <p>getList.</p>
     */
    public CompletableFuture<GetResponse> getList(ByteSequence startKey, ByteSequence endKey) throws BaseException {
        try {
            return etcdClient.getKVClient().get(startKey, GetOption.newBuilder().withRange(endKey).build());
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in getList startKey [" + startKey + "] into etcd: " + e.getLocalizedMessage(), e);
        }
    }
}
