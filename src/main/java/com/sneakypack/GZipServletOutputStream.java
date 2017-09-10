/* Copyright 2017 by SneakyPack.Com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sneakypack;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

class GZipServletOutputStream extends ServletOutputStream {
    private ByteArrayOutputStream buf = null;
    private OutputStream output;

    public GZipServletOutputStream(OutputStream output)
            throws IOException {
        super();
        this.output = output;
        this.buf = new ByteArrayOutputStream();
    }

    @Override
    public void close() throws IOException {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(buf.toByteArray()));
        byte buf1[] = new byte[1024];
        int bytesRead;
        while ((bytesRead=gis.read(buf1)) != -1) {
            output.write(buf1, 0, bytesRead);
        }
        output.close();
        buf = null;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void write(byte b[]) throws IOException {
        buf.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        buf.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        buf.write(b);
    }
}
