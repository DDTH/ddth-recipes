package com.github.ddth.recipes.apiservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * API Service utility class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.2.0
 */
public class ApiServiceUtils {
    /**
     * Compress data using Gzip.
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] toGzip(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOS = new GZIPOutputStream(baos)) {
                gzipOS.write(data);
                gzipOS.finish();
                return baos.toByteArray();
            }
        }
    }

    /**
     * Decode data from a JSON string.
     *
     * @param data
     * @return
     */
    public static JsonNode fromJsonString(byte[] data) {
        return SerializationUtils.readJson(data);
    }

    /**
     * Decode data from a Gzipped-JSON string.
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static JsonNode fromJsonGzip(byte[] data) throws IOException {
        try (GZIPInputStream gzipIS = new GZIPInputStream(new ByteArrayInputStream(data))) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipIS.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return fromJsonString(baos.toByteArray());
            }
        }
    }
}
