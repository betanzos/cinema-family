/**
 * Copyright 2019 Eduardo E. Betanzos Morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.betanzos.cinemafamily.utils;

import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.vtt.VttWriter;
import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
public final class Util {
    private Util() {}

    private static final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final Random r = new Random();

    private static final Tika TIKA = new Tika();

    public static String pathToId(File path) {
        var base64 = toBase64(path.getAbsolutePath());
        int halfIndex = base64.length() / 2;

        var preId = new StringBuilder()
                .append(chars[r.nextInt(62)])
                .append(base64.substring(0, halfIndex))
                .append(chars[r.nextInt(62)])
                .append(base64.substring(halfIndex))
                .toString();

        return toBase64(preId);
    }

    public static File idToPath(String id) {
        var preId = fromBase64(id);

        int halfIndex = (preId.length() - 2) / 2;

        var step1 = preId.substring(1);
        var base64 = step1.substring(0, halfIndex) + step1.substring(halfIndex + 1);

        return new File(fromBase64(base64));
    }

    public static String toBase64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String fromBase64(String s) {
        byte[] decode = Base64.getDecoder().decode(s.getBytes());
        return new String(decode);
    }

    public static Optional<String> detectMimeType(File file) {
        try (var is = new FileInputStream(file)) {
            return Optional.ofNullable(TIKA.detect(is));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<String> detectMimeType(byte[] data) {
        try (var is = new ByteArrayInputStream(data)) {
            return Optional.ofNullable(TIKA.detect(is));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<String> detectMimeType(Resource resource) {
        try {
            if (resource instanceof ByteArrayResource) {
                var bar = (ByteArrayResource) resource;
                return detectMimeType(bar.getByteArray());
            } else {
                return detectMimeType(resource.getFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static boolean areEqualsWithoutExtension(String name1, String name2) {
        int dotElement = name1.lastIndexOf('.');
        int dotFile = name2.lastIndexOf('.');

        return name1.substring(0, dotElement).equals(name2.substring(0, dotFile));
    }

    public static Optional<byte[]> subtitleSrtToVtt(File subFile) {
        try (
                var srtInputStream = new FileInputStream(subFile);
                var forEncodingInputStream = new FileInputStream(subFile)
        ) {
            // Detect original encoding
            String encoding = detectEncoding(forEncodingInputStream.readAllBytes())
                    .orElseGet(() -> "utf-8");

            var srtParser = new CustomStrParser(encoding);
            var srtSubObject = srtParser.parse(srtInputStream);

            var vttWriter = new VttWriter("utf-8");
            var baos = new ByteArrayOutputStream();
            vttWriter.write(srtSubObject, baos);

            return Optional.ofNullable(baos.toByteArray());
        } catch (IOException | SubtitleParsingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private static Optional<String> detectEncoding(byte[] data) {
        return List.of("UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16", "windows-1252")
                .stream()
                .filter(cs -> {
                    var decoder = Charset.forName(cs).newDecoder();
                    decoder.onMalformedInput(CodingErrorAction.REPORT);
                    try {
                        decoder.decode(ByteBuffer.wrap(data));
                        return true;
                    } catch (CharacterCodingException e) {
                        // Do nothing, just wrong encoding. Try again
                    }

                    return false;
                })
                .findFirst();
    }
}
