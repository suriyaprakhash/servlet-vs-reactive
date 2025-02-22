package com.suriyaprakhash.servlet.hugefile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequestMapping("/hugefile")
@RestController
public class HugeFileController {


    String filePath = "../ignore-test-files/";
    String fileName = "150MB";
    String fileExtension = "csv";
    int bufferByteSize = 8192; // Adjust buffer size as needed

    // private String filePath = "\"/home/suriya/sample-test-files/\"";
    // private String fileName = "150MB";
    // private String fileType = "csv";

    /**
     *
     * Tomcat - writes the actual file
     * Netty - writes the actual file
     *
     * @return
     * @throws IOException
     */
    @GetMapping(value="bio/resource", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getHugeFileBio2() throws IOException {

        try {
            Path filePath = Paths.get(this.filePath).resolve(fileName + "." + fileExtension).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Even though this is web servlet - it uses async task executor to work on the rest of the stream closing the main thread.
     * So spring.mvc.async.request-timeout - matters by default its set to 30 sec
     * Transfer-Encoding: chunked - response header is set so content length doesn't matter
     *
     * Here, HTTP response ends after the first pass from the buffer - the aync is handled from the 2nd pass
     *
     * Tomcat - writes the actual file
     * Netty - needs handler and can't process StreamingBodyResponse so spits out an empty object in csv file
     *
     * @return
     * @throws IOException
     */
    @GetMapping(value="bio/stream")
    public ResponseEntity<StreamingResponseBody> getHugeFileBioStream()  {
//       DeferredResult<ResponseEntity<StreamingResponseBody>> deferredResult = new DeferredResult<>(10000L);


        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("stream.csv").build());
        headers.setContentLength(new File(filePath + fileName + "." + fileExtension).length());
        StreamingResponseBody stream = outputStream -> {

            int byteRead;
            try (var bis = new BufferedInputStream(new FileInputStream(filePath + fileName + "." + fileExtension),
                    bufferByteSize)) {
                // gets only one byte so does more read from the stream
                while ((byteRead = bis.read()) != -1) {
                    outputStream.write(byteRead);
                }
            }
            outputStream.flush();
            outputStream.close();

//            int bytesRead; // keeps track of no.of byte filled in the buffer
//            byte[] buffer = new byte[bufferByteSize];
//            try (var bis = new BufferedInputStream(new FileInputStream("/home/suriya/sample-test-files/150MB.csv"), bufferByteSize)) {
//                // reads the buffer into the array
//                while ((bytesRead = bis.read(buffer)) != -1) {
//                    // Process the bytes in the buffer
//                    for (int i = 0; i < bytesRead; i++) {
//                        outputStream.write(buffer[i]);
//                    }
//                }
 //           outputStream.flush();
//            }
        };
        return ResponseEntity.ok().headers(headers).body(stream);
    }

    // TODO: Check in this context if DefferedResult & StreamingResponseBody are one and the same - ie) aync
    // TODO: Sine the for loop is blocking - it does seem to be processed in async thread - meaning expect http response and a separate aync for downloading the rest of data

    @GetMapping("bio/stream/deferred")
    public ResponseEntity<Set<Future<Pair<String, Integer>>>> stream() throws ExecutionException, InterruptedException {
        DeferredResult<ResponseEntity<StreamingResponseBody>> deferredResult = new DeferredResult<>(5000L); // 5 seconds timeout

//        CompletableFuture.runAsync(() -> {
//            try {
//                StreamingResponseBody responseBody = outputStream -> {
//                    // Simulate streaming data
//                    for (int i = 0; i < 20; i++) {
//                        outputStream.write(("Data " + i + "\n").getBytes());
//                        outputStream.flush();
////                        try {
////                            Thread.sleep(500); // Simulate some processing delay
////                        } catch (InterruptedException e) {
////                            throw new RuntimeException(e);
////                        }
//                    }
//                };
//
//                deferredResult.setResult(ResponseEntity.ok(responseBody));
//
//            } catch (Exception e) {
//                deferredResult.setErrorResult(ResponseEntity.internalServerError().build()); // Handle errors
//            }
//        });
//
//        deferredResult.onTimeout(() -> {
//            deferredResult.setErrorResult(ResponseEntity.status(408).build()); // Request Timeout
//        });

//        StreamingResponseBody responseBody = outputStream -> {
////                    // Simulate streaming data
//                    for (int i = 0; i < 20; i++) {
//                        outputStream.write(("Data " + i + "\n").getBytes());
//                        outputStream.flush();
//                    }
//                };


//        CompletableFuture.runAsync(() -> {
//            OutputStream outputStream = null;
//
//            int bufferByteSize = 8192; // Adjust buffer size as needed - default 8192
//            int byteRead;
//            try (var bis = new BufferedInputStream(new FileInputStream("/home/suriya/sample-test-files/150MB.csv"), bufferByteSize)) {
//                // gets only one byte so does more read from the stream
//                while ((byteRead = bis.read()) != -1) {
//                    outputStream.write(byteRead);
//                }
//            }  catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        Callable<byte[]> callable = () -> {
//            int bufferByteSize = 8192; // Adjust buffer size as needed - default 8192
//            int byteRead;
//            byte[] buffer = new byte[bufferByteSize];
//            try (var bis = new BufferedInputStream(new FileInputStream("/home/suriya/sample-test-files/150MB.csv"), bufferByteSize)) {
//                // gets only one byte so does more read from the stream
//                while ((byteRead = bis.read(buffer)) != -1) {
//                    return buffer;
//                }
//            }  catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        };

        AtomicInteger globalCounter = new AtomicInteger();
//        int globalCounter = 0;
        Set<Future<Pair<String, Integer>>> futureList = new HashSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            Future<Pair<String, Integer>> future = executor.submit(new NamedTask(i, globalCounter));
//            globalCounter++;
            log.info(String.valueOf(future.get()));
            futureList.add(future);
        }

        return ResponseEntity.ok(futureList);
    }

    private static class NamedTask implements Callable<Pair<String,Integer>> {  //Inner class
        private final String name;
        private final AtomicInteger threadCounter;

        public NamedTask(int threadId, AtomicInteger threadCounter) {
            this.name = "t-"+ threadId;
            this.threadCounter = threadCounter;
        }

        @Override
        public Pair<String,Integer> call() throws Exception {
            return new Pair<String,Integer>(name, threadCounter.addAndGet(1));
        }
    }

}