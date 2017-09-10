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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

class GZipServletResponseWrapper extends HttpServletResponseWrapper {

    private GZipServletOutputStream gzipOutputStream = null;
    private PrintWriter printWriter      = null;

    public GZipServletResponseWrapper(HttpServletResponse response)
            throws IOException {
        super(response);
    }

    public void close() throws IOException {

        //PrintWriter.close does not throw exceptions.
        //Hence no try-catch block.
        if (this.printWriter != null) {
            this.printWriter.close();
        }

        if (this.gzipOutputStream != null) {
            this.gzipOutputStream.close();
        }
    }


    /**
     * Flush OutputStream or PrintWriter
     *
     * @throws IOException
     */

    @Override
    public void flushBuffer() throws IOException {

        //PrintWriter.flush() does not throw exception
        if(this.printWriter != null) {
            this.printWriter.flush();
        }

        IOException exception1 = null;
        try{
            if(this.gzipOutputStream != null) {
                this.gzipOutputStream.flush();
            }
        } catch(IOException e) {
            exception1 = e;
        }

        IOException exception2 = null;
        try {
            super.flushBuffer();
        } catch(IOException e){
            exception2 = e;
        }

        if(exception1 != null) throw exception1;
        if(exception2 != null) throw exception2;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.printWriter != null) {
            throw new IllegalStateException(
                    "PrintWriter obtained already - cannot get OutputStream");
        }
        if (this.gzipOutputStream == null) {
            this.gzipOutputStream = new GZipServletOutputStream(
                    getResponse().getOutputStream());
        }
        return this.gzipOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.printWriter == null && this.gzipOutputStream != null) {
            throw new IllegalStateException(
                    "OutputStream obtained already - cannot get PrintWriter");
        }
        if (this.printWriter == null) {
            this.gzipOutputStream = new GZipServletOutputStream(
                    getResponse().getOutputStream());
            this.printWriter      = new PrintWriter(new OutputStreamWriter(
                    this.gzipOutputStream, getResponse().getCharacterEncoding()));
        }
        return this.printWriter;
    }


    @Override
    public void setContentLength(int len) {
        //ignore, since content length of zipped content
        //does not match content length of unzipped content.
    }
}
