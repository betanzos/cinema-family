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
package com.betanzos.cinemafamily.service;

import com.betanzos.cinemafamily.domain.FileSystemElement;
import com.betanzos.cinemafamily.domain.Folder;
import com.betanzos.cinemafamily.domain.SubtitleFile;
import com.betanzos.cinemafamily.domain.VideoFile;
import com.betanzos.cinemafamily.exception.FileNotFoundException;
import com.betanzos.cinemafamily.utils.AlphanumericStringComparator;
import com.betanzos.cinemafamily.utils.Util;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
@Service
public class FileSystemService {

    /**
     * Permite obtener el contenido del directorio cuyo identificador es {@code dirId}.<br>
     * <br>
     * El contenido será ordenado alfabéticamente ocupando las primeras posiciones las carpetas y posteriorente
     * los archivos.
     *
     * @param dirId Identificador del directorio
     *
     * @return Instancia de {@link List}{@code <}{@link FileSystemElement}{@code >}. Si el directorio está
     *         vacío la lista que se devuelve igualmente lo estará.
     */
    public List<FileSystemElement> getDirContent(String dirId) {
        return getDirContent(Util.idToPath(dirId));
    }

    /**
     * Permite obtener el contenido del directorio denotado por {@code dirPath}.<br>
     * <br>
     * El contenido será ordenado alfabéticamente ocupando las primeras posiciones las carpetas y posteriorente
     * los archivos.
     *
     * @param dirPath Directorio el que se devolverá el contenido
     *
     * @return Instancia de {@link List}{@code <}{@link FileSystemElement}{@code >}. Si el directorio está
     *         vacío la lista que se devuelve igualmente lo estará.
     */
    public List<FileSystemElement> getDirContent(File dirPath) {
        var filesArr = dirPath.listFiles();

        if (filesArr.length > 0) {
            var fsFolders = new ArrayList<FileSystemElement>();
            var fsFiles = new ArrayList<VideoFile>();

            for (var file : filesArr) {
                if (file.isDirectory()) {
                    fsFolders.add(new Folder(Util.pathToId(file), file.getName()));
                } else {
                    Util.detectMimeType(file)
                            .ifPresent(mime -> {
                                if (mime.startsWith("video")) {
                                    fsFiles.add(new VideoFile(Util.pathToId(file), file.getName()));
                                }
                            });
                }
            }

            // Ordenar por nombre
            fsFolders.sort(Comparator.comparing(FileSystemElement::getName, new AlphanumericStringComparator()));
            fsFiles.sort(Comparator.comparing(FileSystemElement::getName, new AlphanumericStringComparator()));

            fsFolders.addAll(fsFiles);

            return fsFolders;
        }

        return List.of();
    }

    /**
     * Carga los archivos que posiblemente sean subtítulos pertenecientes al video cuyo identificador es
     * {@code videoId}.<br>
     * <br>
     * Para determinar si un archivo podría contener subtítulos se valora que:<br>
     * - se trate de un archivo de texto (si su MIME type es text/plain), y<br>
     * - su nombre es igual al nombre del archivo de video excluyendo las extensiones.
     *
     * @param videoId Identificador del video
     *
     * @return Listado de subtítulos en caso de encontrarse alguno. Si no se encuentran se devolverá
     *         una lista vacía
     */
    public List<SubtitleFile> loadVideoFileSubtitules(String videoId) {
        File file = Util.idToPath(videoId);
        File[] filesArr = file.getParentFile().listFiles();

        var subs = new ArrayList<SubtitleFile>(5);

        for (var f : filesArr) {
            if (f.isFile() && Util.areEqualsWithoutExtension(f.getName(), file.getName())) {
                Util.detectMimeType(f)
                        .ifPresent(mime -> {
                            if (mime.equals("text/plain")) {
                                var langId = "lang" + (subs.size() + 1);
                                var langName = "Language " + (subs.size() + 1);
                                subs.add(new SubtitleFile(Util.pathToId(f), f.getName(), langId, langName));
                            }
                        });
            }
        }

        return subs;
    }

    /**
     * Permite cargar un archivo en forma de {@link Resource}. Esto es necesario para poder enviar archivos que
     * se encuentran fuera del contexto de la aplicación (o incluso fuera del contexto del servidor de aplicaciones)
     * hacia el cleinte.
     *
     * @param fileId Identificador del archivo
     *
     * @return El archivo como {@link Resource}
     */
    public Resource loadFileAsResource(String fileId) {
        try {
            Resource resource = new UrlResource(Util.idToPath(fileId).toURI());

            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileId);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileId, e);
        }
    }

    public Resource loadSubtitleFileAsResource(String subFileId) {
        var subFile = Util.idToPath(subFileId);
        if (!subFile.exists()) {
            throw new FileNotFoundException("File not found " + subFileId);
        }

        return Util.subtitleSrtToVtt(subFile)
                    .stream()
                    .map(data -> (Resource) new ByteArrayResource(data))
                    .findFirst()
                    .orElseGet(() -> loadFileAsResource(subFileId));
    }
}
