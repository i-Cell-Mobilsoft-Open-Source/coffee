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
package hu.icellmobilsoft.coffee.tool.utils.string;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

/**
 * Util class for generating random Strings
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class RandomUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(RandomUtil.class);

    /** Constant <code>DATE_2013_01_01=1356998400000l</code> */
    public static long DATE_2013_01_01 = 1356998400000l;

    // [0-9a-zA-Z]
    /** Constant <code>MAX_NUM_SYS=62</code> */
    public static final int MAX_NUM_SYS = 62;
    // [a-z]
    /** Constant <code>LOWERCASE</code> */
    public static final char[] LOWERCASE;
    // [A-Z]
    /** Constant <code>UPPERCASE</code> */
    public static final char[] UPPERCASE;
    // [0-9a-zA-Z]
    /** Constant <code>ALL_LETTER</code> */
    public static final char[] ALL_LETTER;
    /** Constant <code>ALL_LETTER_STRING=""</code> */
    public static final String ALL_LETTER_STRING;
    /** Constant <code>generatedIndex=0</code> */
    public static int generatedIndex = 0;
    /** Constant <code>PID=</code> */
    public static final int PID;
    /** Constant <code>PID62=""</code> */
    protected static final String PID62;
    /** Constant <code>PID36=""</code> */
    protected static final String PID36;

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
     * generate fix 16 length id!
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
        Long nano = System.nanoTime();
        String xNano = convertToRadix(nano, 36);
        builder.append(xNano.substring(xNano.length() - 4, xNano.length()));

        /* random */
        // 2
        builder.append(paddL(convertToRadix(RANDOM.nextInt(1296), 36), 2, '0'));
        /* generation index */
        builder.append(paddL(convertToRadix(xInd, 36), 2, '0'));

        return builder.toString();
    }

    /**
     * <p>getNextIndex.</p>
     */
    protected static synchronized int getNextIndex() {
        generatedIndex++;
        // MAX a ZZ
        if (generatedIndex > 1296) {
            generatedIndex = 0;
        }
        return generatedIndex;
    }

    /**
     * <p>paddL.</p>
     */
    protected static String paddL(String str, int length, char padd) {
        /*
         * String result = str; while (result.length() < length) { result = padd + result; } return result;
         */
        return StringUtils.leftPad(str, length, padd);
    }

    /**
     * Egy szám tetszőleges számrendszerbe való váltása. 62-es szamrendszer a max
     */
    protected static String convertToRadix(long inNum, long radix) {
        if (radix == 0) {
            return null;
        }
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

    /**
     * Generating random String token
     */
    protected static String generateToken() {
        String token = StringUtils.left(UUID.randomUUID() + generateId(), 48);
        LOGGER.infov("Generated token: [{0}]", token);
        return token;
    }
}
