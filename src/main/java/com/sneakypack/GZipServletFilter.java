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
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GZipServletFilter implements Filter {
    Map<String, String> mimeTypes = new HashMap<String, String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("svg", "image/svg");
    }

    @Override
    public void destroy() {
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!"get".equals(httpRequest.getMethod().toLowerCase())) {
            chain.doFilter(request, response);
            return;
        }

        if (!httpResponse.containsHeader("Content-Type")) {
            String uri = httpRequest.getRequestURI();
            if (uri.endsWith(".gz")) {
                int pos = uri.lastIndexOf('.', uri.length() - 4);
                if (pos != -1) {
                    String ext = uri.substring(pos + 1, uri.length() - 3);
                    String mime = mimeTypes.get(ext);
                    if (mime != null) {
                        httpResponse.setContentType(mime);
                    }
                }
            }
        }
        httpResponse.setHeader("Vary","Accept-Encoding");

        if ( !acceptsGZipEncoding(httpRequest) ) {
            GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        } else {
            httpResponse.setHeader("Content-Encoding", "gzip");
            final boolean allowContentType = httpResponse.getContentType() == null;
            chain.doFilter(request, new HttpServletResponseWrapper(httpResponse) {
                @Override
                public void setContentType(String type) {
                    if (allowContentType)
                        super.setContentType(type);
                }

                @Override
                public void setHeader(String name, String value) {
                    if ("accept-ranges".equals(name.toLowerCase()))
                        return;
                    super.setHeader(name, value);
                }

                @Override
                public void addHeader(String name, String value) {
                    if ("accept-ranges".equals(name.toLowerCase()))
                        return;
                    super.addHeader(name, value);
                }
            });
        }
    }

    private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");

        return acceptEncoding != null &&
               acceptEncoding.indexOf("gzip") != -1;
    }
}
