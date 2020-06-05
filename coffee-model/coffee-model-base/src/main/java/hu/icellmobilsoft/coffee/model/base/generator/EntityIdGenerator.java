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
package hu.icellmobilsoft.coffee.model.base.generator;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Random;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * Entity identifier generator.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class EntityIdGenerator implements IdentifierGenerator {

    /** {@inheritDoc} */
    @Override
    public Serializable generate(SharedSessionContractImplementor arg0, Object arg1) throws HibernateException {
        if (arg1 instanceof AbstractIdentifiedAuditEntity) {
            AbstractIdentifiedAuditEntity entity = (AbstractIdentifiedAuditEntity) arg1;
            if (entity.getId() == null) {
                return generateId();
            }
            return entity.getId();
        } else
        // java8 dátumos AbstractIdentifiedAuditEntity
        if (arg1 instanceof hu.icellmobilsoft.coffee.model.base.javatime.AbstractIdentifiedAuditEntity) {
            hu.icellmobilsoft.coffee.model.base.javatime.AbstractIdentifiedAuditEntity entity = (hu.icellmobilsoft.coffee.model.base.javatime.AbstractIdentifiedAuditEntity) arg1;
            if (entity.getId() == null) {
                return generateId();
            }
            return entity.getId();
        }
        return generateId();
    }

    private static final long DATE_2013_01_01 = 1356998400000l;

    // [0-9a-zA-Z]
    private static final int MAX_NUM_SYS = 62;
    // [a-z]
    private static final char[] LOWERCASE;
    // [A-Z]
    private static final char[] UPPERCASE;
    // [0-9a-zA-Z]
    private static final char[] ALL_LETTER;
    private static final String ALL_LETTER_STRING;
    private static int generatedIndex = 0;
    private static final int PID;
    private static final String PID62;
    private static final String PID36;

    /* init */
    static {
        UPPERCASE = new char[26];
        for (int i = 65; i < 65 + 26; i++) {
            UPPERCASE[i - 65] = (char) i;
        }
        LOWERCASE = new char[26];
        for (int i = 97; i < 97 + 26; i++) {
            LOWERCASE[i - 97] = (char) i;
        }
        ALL_LETTER = new char[MAX_NUM_SYS];
        for (int i = 48; i < 48 + 10; i++) {
            ALL_LETTER[i - 48] = (char) i;
        }
        for (int i = 10; i < 10 + 26; i++) {
            ALL_LETTER[i] = UPPERCASE[i - 10];
        }
        for (int i = 10 + 26; i < 10 + 26 + 26; i++) {
            ALL_LETTER[i] = LOWERCASE[i - 10 - 26];
        }
        ALL_LETTER_STRING = new String(ALL_LETTER);
        PID = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        /* pid */
        // maxpid = 238327
        PID62 = paddL(convertToRadix(PID, MAX_NUM_SYS), 3, '0').substring(0, 3);
        // maxpid
        PID36 = paddL(convertToRadix(PID, 36), 3, '0').substring(0, 3);
    }

    private static final Random RANDOM = new Random();

    /**
     * generate fix 19 length id!
     */
    public static String generateId() {
        int xInd = getNextIndex();
        Date xDate = new Date();
        xDate.setTime(xDate.getTime() - DATE_2013_01_01);
        /* time based */
        String xRes = convertToRadix(xDate.getTime(), 36);
        // 8888 -ig eleg a 8 karakter :) 7 karakter 2081 -ig jo.
        xRes = paddL(xRes, 8, '0');

        StringBuilder builder = new StringBuilder();
        builder.append(xRes);

        // nano, utolso 4 karaktert levagjuk mert csak az változik millisecen belül
        long nano = System.nanoTime();
        String xNano = convertToRadix(nano, 36);
        builder.append(xNano.substring(xNano.length() - 4, xNano.length()));

        /* random */
        // 2
        builder.append(paddL(convertToRadix(RANDOM.nextInt(1296), 36), 2, '0'));
        /* generation index */
        builder.append(paddL(convertToRadix(xInd, 36), 2, '0'));

        return builder.toString();
    }

    private static synchronized int getNextIndex() {
        generatedIndex++;
        // MAX a ZZ
        if (generatedIndex > 1296) {
            generatedIndex = 0;
        }
        return generatedIndex;
    }

    private static String paddL(String str, int length, char padd) {
        return StringUtils.leftPad(str, length, padd);
    }

    /*
     * 
     * Egy szám tetszőleges számrendszerbe való váltása. 62-es szamrendszer a max
     */
    private static String convertToRadix(long inNum, long radix) {
        long dig;
        long numDivRadix;
        long num = inNum;
        String result = "";
        do {
            numDivRadix = num / radix;
            dig = ((num % radix) + radix) % radix;
            result = ALL_LETTER[(int) dig] + result;
            num = numDivRadix;
        } while (num != 0);
        return result;
    }
}
