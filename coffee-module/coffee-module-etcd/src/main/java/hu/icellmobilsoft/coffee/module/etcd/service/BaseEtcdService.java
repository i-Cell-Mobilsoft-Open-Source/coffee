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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.etcd.repository.EtcdRepository;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;
import hu.icellmobilsoft.coffee.tool.utils.string.StringUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Response;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;

/**
 * https://github.com/coreos/jetcd
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class BaseEtcdService<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private EtcdRepository etcdRepository;

    @Inject
    private StringHelper stringHelper;

    /**
     * <p>getEtcdData.</p>
     */
    @SuppressWarnings("unchecked")
    public T getEtcdData(String key, Class<T> c) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new BaseException("key is empty!");
        }

        try {
            ByteSequence bsKey = ByteSequence.from(key, StandardCharsets.UTF_8);
            GetResponse response = etcdRepository.get(bsKey).get();
            if (response.getCount() < 1) {
                log.tracev("etcd: getting key [{0}], value NOT FOUND, response: [{1}]", key, response);
                throw new BONotFoundException("Etcd data not found for key [" + key + "]!");
            }
            String stringData = response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
            String responseStr = replaceSensitiveDataInReponseString(response);
            log.tracev("etcd: getting key [{0}], value [{1}], response: [{2}]", key, stringHelper.maskPropertyValue(key, stringData), responseStr);
            if (c == String.class) {
                return (T) stringData;
            } else {
                throw new TechnicalException("Type [" + c.getClass() + "] is not implemented yet!");
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>setEtcdData.</p>
     */
    public void setEtcdData(String key, String value) throws BaseException {
        // IS: a value lehetne generikum is, ha szukseg lessz ra, csak akkor meg kell allapodni mikent fogjuk tarolni (pl json)
        if (StringUtils.isAnyBlank(key, value)) {
            throw new BaseException("key or value is empty!");
        }

        try {
            ByteSequence bsKey = ByteSequence.from(key, StandardCharsets.UTF_8);
            ByteSequence bsValue = ByteSequence.from(value, StandardCharsets.UTF_8);
            PutResponse response = etcdRepository.put(bsKey, bsValue).get();
            String stringData = replaceSensitiveDataInReponseString(response);
            log.tracev("etcd: putting key [{0}], value [{1}] response: [{2}]", key, stringHelper.maskPropertyValue(key, value), stringData);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>getEtcdDataList.</p>
     */
    @SuppressWarnings("unchecked")
    public Map<String, T> getEtcdDataList(String startKeyStr, String endKeyStr) throws BaseException {
        log.debugv(">> ExtEtcdService.getEtcdDataList()");
        Map<String, T> etcdDataList = new HashMap<>();
        try {
            ByteSequence startKey = ByteSequence.from(startKeyStr, StandardCharsets.UTF_8);
            ByteSequence endKey = ByteSequence.from(endKeyStr, StandardCharsets.UTF_8);
            log.debugv("etcd search: startKey : [{0}], endKey : [{1}]", startKeyStr, endKeyStr);
            GetResponse response = etcdRepository.getList(startKey, endKey).get();

            long kvsCount = response.getCount();
            for (int i = 0; i < kvsCount; i++) {
                String stringKey = response.getKvs().get(i).getKey().toString(StandardCharsets.UTF_8);
                String stringValue = response.getKvs().get(i).getValue().toString(StandardCharsets.UTF_8);
                log.debugv("etcd: [{0}]. key : [{1}], value : [{2}]", i, stringKey, stringHelper.maskPropertyValue(stringKey, stringValue));
                etcdDataList.put(stringKey, (T) stringValue);
            }
            String responseStr = replaceSensitiveDataInReponseString(response);
            log.debugv("etcd: found [{0}] entry, response: [{1}]", kvsCount, responseStr);
            return etcdDataList;

        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        } finally {
            log.debugv("<< ExtEtcdService.getEtcdDataList()");
        }
    }

    /**
     * <p>deleteEtcdData.</p>
     */
    public void deleteEtcdData(String key, Class<T> c) throws BaseException {
        log.debugv(">> ExtEtcdService.deleteEtcdData(key : [{0}])", key);
        try {
            ByteSequence keyToDelete = ByteSequence.from(key, StandardCharsets.UTF_8);
            DeleteResponse deleteResponse = etcdRepository.delete(keyToDelete).get();

            long deletedCount = deleteResponse.getDeleted();
            if (deletedCount < 1) {
                log.debugv("etcd: delete key [{0}], keyNotFound NOT FOUND, response: [{1}]", key, deleteResponse);
                throw new BONotFoundException("Etcd data not found for key [" + key + "]!");
            }
            String responseStr = replaceSensitiveDataInReponseString(deleteResponse);

            log.debugv("etcd: found [{0}] entry deleted, response: [{1}]", deletedCount, responseStr);

        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
        log.debugv("<< ExtEtcdService.deleteEtcdData()");
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
        String replacementRegex = "(kvs[\\S\\s]*?key:[\\s]*?\"(" + stringHelper.getSensitiveKeyPattern() + ")\"[\\S\\s]*?value:[\\s]*?)\"(.*?)\"";
        return StringUtil.replaceAllIgnoreCase(String.valueOf(response), replacementRegex, "$1\"*\"");
    }
}
