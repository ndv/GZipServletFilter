package com.sneakypack;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
