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
package com.betanzos.cinemafamily.controller;

import com.betanzos.cinemafamily.service.FileSystemService;
import com.betanzos.cinemafamily.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
@Controller
public class MainController {

    @Value("${root.dir}")
    private String rootDirectory;

    private FileSystemService fileSystemService;

    @Autowired
    public MainController(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    @GetMapping("/")
    private String index(Model model) {
        //model.addAttribute("header_text", "Wellcome to Cinema Family");
        model.addAttribute("elements", fileSystemService.getDirContent(new File(rootDirectory)));

        return "folder_view";
    }

    @GetMapping("/folder/{id}")
    private String folder(Model model, @PathVariable("id") String folderId) {
        model.addAttribute("header_text", Util.idToPath(folderId).getName());
        model.addAttribute("elements", fileSystemService.getDirContent(folderId));

        return "folder_view";
    }

    @GetMapping("/video/{id}")
    private String video(Model model, @PathVariable("id") String videoId) {
        model.addAttribute("video_title", Util.idToPath(videoId).getName());
        model.addAttribute("video_id", videoId);
        // Load subtitles
        model.addAttribute("subtitles", fileSystemService.loadVideoFileSubtitules(videoId));

        return "video_player";
    }

    @GetMapping("file/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") String fileId) {
        Resource resource = fileSystemService.loadFileAsResource(fileId);

        String contentType = Util.detectMimeType(resource)
                .orElseGet(() -> "application/octet-stream");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("subtitle/{id}")
    public ResponseEntity<Resource> downloadSubtitle(@PathVariable("id") String subFileId) {
        Resource resource = fileSystemService.loadSubtitleFileAsResource(subFileId);

        String contentType = Util.detectMimeType(resource)
                .orElseGet(() -> "text/plain");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
