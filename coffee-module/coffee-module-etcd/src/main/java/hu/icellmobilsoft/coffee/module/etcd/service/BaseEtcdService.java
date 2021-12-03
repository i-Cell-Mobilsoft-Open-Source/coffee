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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
 * https://github.com/coreos/jetcd
 *
 * @author imre.scheffer
 * @since 1.0.0
 * @deprecated Use {@link EtcdService}
 */
@Dependent
@Deprecated(since = "1.3.0", forRemoval = true)
public class BaseEtcdService<T> {

    private static Logger log = Logger.getLogger(BaseEtcdService.class);

    @Inject
    private EtcdRepository etcdRepository;

    /**
     * Returns value from ETCD.
     *
     * @param key
     *            key of value to return
     * @param c
     *            class of value to return
     * @return ETCD value
     * @throws BaseException
     *             if given key param is empty, value is not found, value type is unsupported or technical error occurs
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
                throw new BONotFoundException(MessageFormat.format("Etcd data not found for key [{0}], response: [{1}]", key, response));
            }
            String stringData = response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
            String responseStr = replaceSensitiveDataInReponseString(response);
            if (log.isTraceEnabled()) {
                log.trace("etcd: getting key [{0}], value [{1}], response: [{2}]", key, StringHelper.maskPropertyValue(key, stringData), responseStr);
            }
            if (c == String.class) {
                return (T) stringData;
            } else {
                throw new TechnicalException("Type [" + c + "] is not implemented yet!");
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Sets value of given key in ETCD.
     *
     * @param key
     *            key to set value of
     * @param value
     *            value to set
     * @throws BaseException
     *             if given key or value param is empty, or technical error occurs
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
            if (log.isTraceEnabled()) {
                log.trace("etcd: putting key [{0}], value [{1}] response: [{2}]", key, StringHelper.maskPropertyValue(key, value), stringData);
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Returns values of keys between the two given keys from ETCD.
     *
     * @param startKeyStr
     *            start key
     * @param endKeyStr
     *            end key (exclusive)
     * @return response key-value pairs in a {@link Map}
     * @throws BaseException
     *             if technical error occurs
     */
    @SuppressWarnings("unchecked")
    public Map<String, T> getEtcdDataList(String startKeyStr, String endKeyStr) throws BaseException {
        log.debug(">> ExtEtcdService.getEtcdDataList()");
        Map<String, T> etcdDataList = new HashMap<>();
        try {
            ByteSequence startKey = ByteSequence.from(startKeyStr, StandardCharsets.UTF_8);
            ByteSequence endKey = ByteSequence.from(endKeyStr, StandardCharsets.UTF_8);
            log.debug("etcd search: startKey : [{0}], endKey : [{1}]", startKeyStr, endKeyStr);
            GetResponse response = etcdRepository.getList(startKey, endKey).get();

            long kvsCount = response.getCount();
            for (int i = 0; i < kvsCount; i++) {
                String stringKey = response.getKvs().get(i).getKey().toString(StandardCharsets.UTF_8);
                String stringValue = response.getKvs().get(i).getValue().toString(StandardCharsets.UTF_8);
                if (log.isDebugEnabled()) {
                    log.debug("etcd: [{0}]. key : [{1}], value : [{2}]", i, stringKey, StringHelper.maskPropertyValue(stringKey, stringValue));
                }
                etcdDataList.put(stringKey, (T) stringValue);
            }
            String responseStr = replaceSensitiveDataInReponseString(response);
            log.debug("etcd: found [{0}] entry, response: [{1}]", kvsCount, responseStr);
            return etcdDataList;

        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        } finally {
            log.debug("<< ExtEtcdService.getEtcdDataList()");
        }
    }

    /**
     * Deletes value from ETCD.
     *
     * @param key
     *            key of value to delete
     * @param c
     *            class of value to delete
     * @throws BaseException
     *             if value is not found or technical error occurs
     */
    public void deleteEtcdData(String key, Class<T> c) throws BaseException {
        log.debug(">> ExtEtcdService.deleteEtcdData(key : [{0}])", key);
        try {
            ByteSequence keyToDelete = ByteSequence.from(key, StandardCharsets.UTF_8);
            DeleteResponse deleteResponse = etcdRepository.delete(keyToDelete).get();

            long deletedCount = deleteResponse.getDeleted();
            if (deletedCount < 1) {
                log.debug("etcd: delete key [{0}], keyNotFound NOT FOUND, response: [{1}]", key, deleteResponse);
                throw new BONotFoundException("Etcd data not found for key [" + key + "]!");
            }
            String responseStr = replaceSensitiveDataInReponseString(deleteResponse);

            log.debug("etcd: found [{0}] entry deleted, response: [{1}]", deletedCount, responseStr);

        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Communication exception: " + e.getLocalizedMessage(), e);
        }
        log.debug("<< ExtEtcdService.deleteEtcdData()");
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
        String[] sensitiveKeyPatterns = StringHelper.getSensitiveKeyPattern();
        for (String sensitiveKeyPattern : sensitiveKeyPatterns) {
            String replacementRegex = "(kvs[\\S\\s]*?key:[\\s]*?\"(" + sensitiveKeyPattern + ")\"[\\S\\s]*?value:[\\s]*?)\"(.*?)\"";
            responseText = StringUtil.replaceAllIgnoreCase(responseText, replacementRegex, "$1\"*\"");
        }
        return responseText;
    }
}
