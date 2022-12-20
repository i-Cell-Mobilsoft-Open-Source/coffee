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

import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.inject.Vetoed;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;

/**
 * Base ETCD operations based on https://github.com/coreos/jetcd
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class EtcdRepository {

    private Client etcdClient;

    /**
     * ETCD repository initialization
     * 
     * @param etcdClient
     *            ETCD client
     */
    public void init(Client etcdClient) {
        this.etcdClient = etcdClient;
    }

    /**
     * Check initialization
     * 
     * @throws BaseException
     *             Exception if etcdClient is null
     */
    protected void checkInit() throws BaseException {
        if (etcdClient == null) {
            throw new TechnicalException("Etcd client is not initialized!");
        }
    }

    /**
     * Put value into ETCD. This call {@link KV#put(ByteSequence, ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @param value
     *            value
     * @return ETCD response
     * @throws BaseException
     *             If technical error happening
     */
    public CompletableFuture<PutResponse> put(ByteSequence key, ByteSequence value) throws BaseException {
        checkInit();
        try {
            return etcdClient.getKVClient().put(key, value);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in put key [" + key + "] and value [" + value + "] into etcd: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Get value from ETCD. This call {@link KV#get(ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @return ETCD response
     * @throws BaseException
     *             If technical error happening
     */
    public CompletableFuture<GetResponse> get(ByteSequence key) throws BaseException {
        checkInit();
        try {
            return etcdClient.getKVClient().get(key);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in get key [" + key + "] into etcd: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Delete value in ETCD. This call {@link KV#delete(ByteSequence)}
     * 
     * @param key
     *            ETCD key
     * @return ETCD response
     * @throws BaseException
     *             If technical error happening
     */
    public CompletableFuture<DeleteResponse> delete(ByteSequence key) throws BaseException {
        checkInit();
        try {
            return etcdClient.getKVClient().delete(key);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in deleting key [" + key + "] in etcd: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Get values from ETCD. This call {@link KV#get(ByteSequence, GetOption)} wit list setting of {@link GetOption.Builder#withRange(ByteSequence)}
     * 
     * @param startKey
     *            start key
     * @param endKey
     *            end key (exclusive)
     * @return ETCD response
     * @throws BaseException
     *             If technical error happening
     */
    public CompletableFuture<GetResponse> getList(ByteSequence startKey, ByteSequence endKey) throws BaseException {
        checkInit();
        try {
            return etcdClient.getKVClient().get(startKey, GetOption.newBuilder().withRange(endKey).build());
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                    "Exception in getList startKey [" + startKey + "] into etcd: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Returns the ETCD client
     * 
     * @return the ETCD client
     */
    public Client getEtcdClient() {
        return etcdClient;
    }
}
