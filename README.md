# HSTS Breaks Payara / Grizzly

## Start Server

```sh
docker run -d -p 4848:4848 -p 8181:8181 payara/server-full:6.2023.1
```

# Build, Deploy, Test

Build the application with

```bash
mvn clean package
```

Deploy the application and open the 'HstsTestcase'-Resource at https://<your-ip>:8181/payara-hsts-1.0.0/
You should now see a long sequence of random alphanumeric characters. This is the expected behaviour.

CURL will also work and close with 'connection left intact'.
```bash
curl -k https://<your-ip>:8181/payara-hsts-1.0.0/
```

## Enable HSTS
```bash
set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.hsts-enabled=true
```

Restart the server.

Reload your browser window or rerun CURL. The response will now be cut off after a certian amount of characters and the page won't stop loading. CURL will eventually terminate with 'unexpected eof while reading, errno 0'. Grizzly will produce a bunch of exceptions in your server logs.

```java
[#|2023-01-31T07:44:07.901+0000|WARNING|Payara 6.2023.3-SNAPSHOT|org.glassfish.grizzly.filterchain.DefaultFilterChain|_ThreadID=85;_ThreadName=http-thread-pool::http-listener-2(3);_TimeMillis=1675151047901;_LevelValue=900;|
  GRIZZLY0013: Exception during FilterChain execution
org.glassfish.grizzly.http.util.MimeHeaders$MaxHeaderCountExceededException: Illegal attempt to exceed the configured maximum number of headers: 100
        at org.glassfish.grizzly.http.util.MimeHeaders.createHeader(MimeHeaders.java:399)
        at org.glassfish.grizzly.http.util.MimeHeaders.addValue(MimeHeaders.java:428)
        at org.glassfish.grizzly.http.HttpHeader.addHeader(HttpHeader.java:656)
        at org.glassfish.grizzly.config.HSTSFilter.handleWrite(HSTSFilter.java:102)
        at org.glassfish.grizzly.filterchain.ExecutorResolver$8.execute(ExecutorResolver.java:81)
        at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeFilter(DefaultFilterChain.java:246)
        at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeChainPart(DefaultFilterChain.java:178)
        at org.glassfish.grizzly.filterchain.DefaultFilterChain.execute(DefaultFilterChain.java:118)
        at org.glassfish.grizzly.filterchain.DefaultFilterChain.process(DefaultFilterChain.java:96)
        at org.glassfish.grizzly.ProcessorExecutor.execute(ProcessorExecutor.java:51)
        at org.glassfish.grizzly.filterchain.FilterChainContext.write(FilterChainContext.java:751)
        at org.glassfish.grizzly.filterchain.FilterChainContext.write(FilterChainContext.java:729)
        at org.glassfish.grizzly.http.io.OutputBuffer.flushBuffer(OutputBuffer.java:1004)
        at org.glassfish.grizzly.http.io.OutputBuffer.flushBinaryBuffers(OutputBuffer.java:980)
        at org.glassfish.grizzly.http.io.OutputBuffer.flushAllBuffers(OutputBuffer.java:952)
        at org.glassfish.grizzly.http.io.OutputBuffer.flush(OutputBuffer.java:696)
        at org.apache.catalina.connector.OutputBuffer.doFlush(OutputBuffer.java:275)
        at org.apache.catalina.connector.OutputBuffer.flush(OutputBuffer.java:259)
        at org.apache.catalina.connector.CoyoteOutputStream.flush(CoyoteOutputStream.java:173)
        at org.glassfish.jersey.servlet.internal.ResponseWriter$NonCloseableOutputStreamWrapper.flush(ResponseWriter.java:307)
        at org.glassfish.jersey.message.internal.CommittingOutputStream.flush(CommittingOutputStream.java:263)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor$UnCloseableOutputStream.flush(WriterInterceptorExecutor.java:281)
        at java.base/sun.nio.cs.StreamEncoder.implFlush(StreamEncoder.java:318)
        at java.base/sun.nio.cs.StreamEncoder.flush(StreamEncoder.java:153)
        at java.base/java.io.OutputStreamWriter.flush(OutputStreamWriter.java:251)
        at org.glassfish.jersey.message.internal.ReaderWriter.writeToAsString(ReaderWriter.java:170)
        at org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider.writeToAsString(AbstractMessageReaderWriterProvider.java:107)
        at org.glassfish.jersey.message.internal.StringMessageProvider.writeTo(StringMessageProvider.java:76)
        at org.glassfish.jersey.message.internal.StringMessageProvider.writeTo(StringMessageProvider.java:36)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor$TerminalWriterInterceptor.invokeWriteTo(WriterInterceptorExecutor.java:242)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor$TerminalWriterInterceptor.aroundWriteTo(WriterInterceptorExecutor.java:227)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor.proceed(WriterInterceptorExecutor.java:139)
        at org.glassfish.jersey.server.internal.JsonWithPaddingInterceptor.aroundWriteTo(JsonWithPaddingInterceptor.java:85)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor.proceed(WriterInterceptorExecutor.java:139)
        at org.glassfish.jersey.server.internal.MappableExceptionWrapperInterceptor.aroundWriteTo(MappableExceptionWrapperInterceptor.java:61)
        at org.glassfish.jersey.message.internal.WriterInterceptorExecutor.proceed(WriterInterceptorExecutor.java:139)
        at org.glassfish.jersey.message.internal.MessageBodyFactory.writeTo(MessageBodyFactory.java:1116)
        at org.glassfish.jersey.server.ServerRuntime$Responder.writeResponse(ServerRuntime.java:670)
        at org.glassfish.jersey.server.ServerRuntime$Responder.processResponse(ServerRuntime.java:378)
        at org.glassfish.jersey.server.ServerRuntime$Responder.process(ServerRuntime.java:368)
        at org.glassfish.jersey.server.ServerRuntime$1.run(ServerRuntime.java:263)
        at org.glassfish.jersey.internal.Errors$1.call(Errors.java:248)
        at org.glassfish.jersey.internal.Errors$1.call(Errors.java:244)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:292)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:274)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:244)
        at org.glassfish.jersey.process.internal.RequestScope.runInScope(RequestScope.java:265)
        at org.glassfish.jersey.server.ServerRuntime.process(ServerRuntime.java:239)
        at org.glassfish.jersey.server.ApplicationHandler.handle(ApplicationHandler.java:697)
        at org.glassfish.jersey.servlet.WebComponent.serviceImpl(WebComponent.java:394)
        at org.glassfish.jersey.servlet.WebComponent.service(WebComponent.java:346)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:357)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:311)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:205)
        at org.apache.catalina.core.StandardWrapper.service(StandardWrapper.java:1569)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:259)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:167)
        at org.apache.catalina.core.StandardPipeline.doInvoke(StandardPipeline.java:757)
        at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:577)
        at com.sun.enterprise.web.WebPipeline.invoke(WebPipeline.java:99)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:158)
        at org.apache.catalina.connector.CoyoteAdapter.doService(CoyoteAdapter.java:372)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:239)
        at com.sun.enterprise.v3.services.impl.ContainerMapper$HttpHandlerCallable.call(ContainerMapper.java:520)
        at com.sun.enterprise.v3.services.impl.ContainerMapper.service(ContainerMapper.java:217)
        at org.glassfish.grizzly.http.server.HttpHandler$1.run(HttpHandler.java:190)
        at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.doWork(AbstractThreadPool.java:535)
        at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.run(AbstractThreadPool.java:515)
        at java.base/java.lang.Thread.run(Thread.java:829)
|#]
```