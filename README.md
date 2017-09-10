# GZipServletFilter

Allows web server to serve gzip-compressed files (e.g. script.js.gz). The filter analyses the 'Accept-Encoding' request header,
and, if contains 'gzip', serves the corresponding compressed file. The Content-Type header is guessed from file extension, e.g. "*.html.gz" maps to "text/html".

On a rare occasion when user-agent does not support gzip, the file is decompressed and served as-is. You have to preserve '.gz' in the URL.

## Build

```
git clone https://github.com/ndv/GZipServletFilter.git
mvn package
```

## Tomcat setup

You can either install servlet filter to the webserver or include it in your .war file. In the former case,
first copy the servlet filter:

```
cp target/GzipJPEG-1.0-SNAPSHOT.jar $CATALINA_HOME/lib
```

Then add the following into your $CATALINA_HOME/web.xml:

```xml
	<filter>
	  <filter-name>GZipServletFilter</filter-name>
	  <filter-class>com.sneakypack.GZipServletFilter</filter-class>
	</filter>

	<filter-mapping>
	  <filter-name>GZipServletFilter</filter-name>
	  <url-pattern>*.gz</url-pattern>
	</filter-mapping>
```
