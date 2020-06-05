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
package hu.icellmobilsoft.coffee.tool.utils.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;

/**
 * Helper class to GZIP compress/decompress string contents <br>
 * <br>
 * Example use of compress/decompress
 *
 * <pre>
 * public static void main(String[] args) {
 *     String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlRoot><xmlChild xmlField=\"xmlValue\">anotherXmlValue</xmlChild></xmlRoot>";
 *     try {
 *         System.out.println("source size: " + source.getBytes(StandardCharsets.UTF_8).length);
 *         long start = System.currentTimeMillis();
 *         byte[] compressed = compress(source.getBytes(StandardCharsets.UTF_8));
 *         long end = System.currentTimeMillis();
 *         System.out.println("compressed size: " + compressed.length + ", time: " + (end - start) + "ms");
 *         int decompressedSize = decompressedSize(compressed);
 *         System.out.println("original size: " + source.length() + ", decompressed size: " + decompressedSize);
 *         start = System.currentTimeMillis();
 *         byte[] decompressed = decompress(compressed);
 *         end = System.currentTimeMillis();
 *         System.out.println("decompressed: " + new String(decompressed, StandardCharsets.UTF_8));
 *         System.out.println("decompress time: " + (end - start) + "ms");
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }
 * </pre>
 *
 * @author robert.kaplar
 * @since 1.0.0
 */
@Vetoed
public class GZIPUtil {

    /**
     * Compress the source byte array content
     *
     * @param data
     */
    public static byte[] compress(byte[] data) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(outputStream);
            IOUtils.copy(inputStream, gzipOutputStream);
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        } catch (IOException ioe) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error at compressing", ioe);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(gzipOutputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Decompress the compressed byte array content
     *
     * @param data
     * @throws BaseException
     */
    public static byte[] decompress(byte[] data) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        if (!isCompressed(data)) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "Input data is not GZIP (does not have GZIP header)");
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(inputStream);
            IOUtils.copy(gzipInputStream, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "IOException at decompressing: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "Exception at decompressing: " + e.getLocalizedMessage(), e);
        } finally {
            IOUtils.closeQuietly(gzipInputStream);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * unzip and convert result from byte[]
     *
     * @param data
     * @param clazz
     * @throws BaseException
     */
    public static <T> T decompress(byte[] data, Class<T> clazz) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        byte[] jsonByte = GZIPUtil.decompress(data);
        String jsonString = new String(jsonByte, StandardCharsets.UTF_8);
        return JsonUtil.toObject(jsonString, clazz);
    }

    /**
     * Vissza adja a GZIP eredeti meretet. Valojaban nem mindig lehet szamolni vele, de megis van valami. Minta:
     *
     * @param data
     * @throws BaseException
     * @see <a href=
     * "https://stackoverflow.com/questions/7317243/gets-the-uncompressed-size-of-this-gzipinputstream">https://stackoverflow.com/questions/7317243/gets-the-uncompressed-size-of-this-gzipinputstream</a>
     */
    public static int decompressedSize(byte[] data) throws BaseException {
        if (data == null || data.length < 4) {
            return 0;
        }
        if (!isCompressed(data)) {
            return data.length;
        }
        byte[] bytes = ArrayUtils.subarray(data, data.length - 4, data.length);
        int fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (fileSize < 0) {
            fileSize += (1L << 32);
        }
        return fileSize;
    }

    /**
     * Determines if a byte array is compressed. The java.util.zip GZip implementaiton does not expose the GZip header so it is difficult to determine
     * if a string is compressed.
     *
     * @param bytes an array of bytes
     * @return true if the array is compressed or false otherwise
     * @see <a href=
     * "https://stackoverflow.com/questions/4818468/how-to-check-if-inputstream-is-gzipped">https://stackoverflow.com/questions/4818468/how-to-check-if-inputstream-is-gzipped</a>
     */
    public static boolean isCompressed(byte[] bytes) {
        if (bytes == null || bytes.length < 2) {
            return false;
        }
        return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
    }
}
