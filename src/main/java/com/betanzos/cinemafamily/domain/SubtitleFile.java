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
package com.betanzos.cinemafamily.domain;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
public class SubtitleFile extends FileSystemElement {
    private final String languageId;
    private final String languageName;

    public SubtitleFile(String id, String name, String languageId, String languageName) {
        super(id, name);
        this.languageId = languageId;
        this.languageName = languageName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public String getLanguageName() {
        return languageName;
    }
}
