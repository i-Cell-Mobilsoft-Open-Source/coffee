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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;

/**
 * Helper class to GZIP compress/decompress string contents <br>
 * <br>
 * Example use of compress/decompress
 *
 * <pre>
 * public static void main(String[] args) {
 *     String source = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;xmlRoot&gt;&lt;xmlChild xmlField=\"xmlValue\"&gt;anotherXmlValue&lt;/xmlChild&gt;&lt;/xmlRoot&gt;";
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
public class GZIPUtil {

    /**
     * Default constructor, constructs a new object.
     */
    public GZIPUtil() {
        super();
    }

    /**
     * Compress the source byte array content
     *
     * @param data
     *            input byte array
     * @return compressed byte array
     * @throws BaseException
     *             exception
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
     *            input byte array
     * @return decompressed byte array
     * @throws BaseException
     *             exception
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
     * Compress JSON DTO object to binary. Input object is parsed with ({@link JsonUtil#toJsonGson(Object, Appendable)}
     *
     * @param <T>
     *            Input DTO class type
     * @param jsonDto
     *            dto object, expected to json serialize
     * @return GZIP compressed binary
     * @throws BaseException
     *             json parse or GZIP compile errors
     */
    public static <T> byte[] compressJson(T jsonDto) throws BaseException {
        if (jsonDto == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                OutputStreamWriter writer = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8)) {
            JsonUtil.toJsonGson(jsonDto, writer);
            writer.flush();
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        } catch (IOException ioe) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error at compressing", ioe);
        }
    }

    /**
     * Unzip and convert result from byte[].
     *
     * @param <T>
     *            destination type
     * @param data
     *            input byte array
     * @param clazz
     *            destination class
     * @return unzipped and converted object
     * @throws BaseException
     *             exception
     */
    public static <T> T decompress(byte[] data, Class<T> clazz) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        byte[] jsonByte = GZIPUtil.decompress(data);
        String jsonString = new String(jsonByte, StandardCharsets.UTF_8);
        return JsonUtil.toObjectGson(jsonString, clazz);
    }

    /**
     * Decompresses and parses the passed byte array as a JSON.<br>
     * Throws exception on JSON parse error.
     *
     * @param <T>
     *            the type parameter
     * @param data
     *            byte array to decompress and parse
     * @param clazz
     *            the type of the resulting class
     * @return the t
     * @throws BaseException
     *             on GZIP or JSON error
     */
    public static <T> T decompressEx(byte[] data, Class<T> clazz) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                InputStreamReader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
            return JsonUtil.toObjectGson(reader, clazz);
        } catch (IOException ioe) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error at decompressing", ioe);
        }
    }

    /**
     * Creates an InputStream with the decompressed content from a compressed byte array. <br>
     * The logic is the same as in {@link GZIPUtil#decompress(byte[])}, but this approach avoids loading the entire decompressed byte array into
     * memory. <br>
     * Make sure to close the stream after use!
     *
     * @param data
     *            input byte array
     * @return decompressed inputStream
     * @throws BaseException
     *             exception
     */
    public static InputStream decompressToInputStream(byte[] data) throws BaseException {
        if (data == null || data.length == 0) {
            return null;
        }
        if (!isCompressed(data)) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "Input data is not GZIP (does not have GZIP header)");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            return new GZIPInputStream(inputStream);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "IOException at decompressing: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.GZIP_DECOMPRESSION_ERROR, "Exception at decompressing: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Returns the original size of a GZIP file. In reality, it's not always possible to calculate accurately, but there's something to work with.
     * Sample:
     *
     * @param data
     *            input adat
     * @return eredeti meret
     * @see <a href=
     *      "https://stackoverflow.com/questions/7317243/gets-the-uncompressed-size-of-this-gzipinputstream">https://stackoverflow.com/questions/7317243/gets-the-uncompressed-size-of-this-gzipinputstream</a>
     */
    public static int decompressedSize(byte[] data) {
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
     * Determines if a byte array is compressed. The {@link java.util.zip} GZip implementaiton does not expose the GZip header so it is difficult to
     * determine if a string is compressed.
     *
     * @param bytes
     *            an array of bytes
     * @return true if the array is compressed or false otherwise
     * @see <a href=
     *      "https://stackoverflow.com/questions/4818468/how-to-check-if-inputstream-is-gzipped">https://stackoverflow.com/questions/4818468/how-to-check-if-inputstream-is-gzipped</a>
     */
    public static boolean isCompressed(byte[] bytes) {
        if (bytes == null || bytes.length < 2) {
            return false;
        }
        return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
    }
}
