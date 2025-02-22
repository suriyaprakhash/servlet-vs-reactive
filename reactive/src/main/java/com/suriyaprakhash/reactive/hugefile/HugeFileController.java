package com.suriyaprakhash.reactive.hugefile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

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
     * This reads the file in the nio fashion - however, writes the file in bio
     *
     * Tomcat - writes the actual file
     * Netty - needs handler and can't process StreamingBodyResponse so spits out an empty object in csv file
     *
     * @return
     */
    @GetMapping(value="nio/write-block")
    public ResponseEntity<StreamingResponseBody> getHugeFileNioTomcat() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("nio.csv").build());

        StreamingResponseBody stream = outputStream -> {
            Path nioPath = Paths.get(filePath + fileName + "." + fileExtension);
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferByteSize);
            // represents connection to the network
            try (FileChannel fileChannel = FileChannel.open(nioPath, StandardOpenOption.READ)) {
                int bytesRead;
                while ((bytesRead = fileChannel.read(byteBuffer)) != -1) {
                    if (bytesRead > 0) { // Important check!
                        byteBuffer.flip(); // Prepare buffer for reading

                        byte[] bytes = new byte[bytesRead]; // Create byte array with actual size read
                        byteBuffer.get(bytes); // Read from buffer
                        // here even though we read in nio fashion we write in bio fashion
                        // so this negates the benefit of non blocking io
                        outputStream.write(bytes); // Write to output stream
                        byteBuffer.clear();      // Prepare buffer for next read
                    }
                }
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        return ResponseEntity.ok().headers(headers).body(stream);

    }

    /**
     * Tomcat - returns the nativeBuffer in the data.nativeBuffer in byte chunks into the csv file
     * Netty - returns the csv file with right info
     *
     * @return
     */
    @GetMapping(value="nio", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<DataBuffer>> getHugeFileNio() {

        try {
            // represents connection to the network
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    Paths.get(filePath + fileName + "." + fileExtension), StandardOpenOption.READ); // Use AsynchronousFileChannel
            DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

            Flux<DataBuffer> dataBufferFlux =  Flux.create(sink -> {
                ByteBuffer byteBuffer = ByteBuffer.allocate(bufferByteSize); // Adjust byteBuffer size
                AtomicLong position = new AtomicLong(0);
                // processes incoming and outgoing data
                java.nio.channels.CompletionHandler<Integer, Void> handler = new java.nio.channels.CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        if (result == -1) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                sink.error(e);
                            }
                            sink.complete();
                            return;
                        }

                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        DataBuffer dataBuffer = dataBufferFactory.wrap(ByteBuffer.wrap(bytes));
                        sink.next(dataBuffer);
                        byteBuffer.clear();
                        position.addAndGet(result);
                        channel.read(byteBuffer, position.get(), null, this);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            exc.addSuppressed(e);
                        }
                        sink.error(exc);
                    }
                };

                channel.read(byteBuffer, 0, null, handler);
            });

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"nio.csv\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(dataBufferFlux); // Return Flux<DataBuffer>

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build(); // Or handle the error in a reactive way
        }

    }


}