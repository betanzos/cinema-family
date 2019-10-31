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
public final class VideoFile extends FileSystemElement {
    private byte[] poster;

    public VideoFile(String id, String name) {
        super(id, name);
        this.poster = null;
    }

    public byte[] getPoster() {
        return poster;
    }

    public VideoFile setPoster(byte[] poster) {
        this.poster = poster;
        return this;
    }
}
