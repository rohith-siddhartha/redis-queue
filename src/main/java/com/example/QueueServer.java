package com.example;

import com.example.utility.HTTPRequestParser;
import com.example.utility.JSONMapper;
import com.example.utility.RequestBody;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

public class QueueServer {

    static RedisQueueService queue;

    public static void main(String[] args) throws Exception {
        queue = new RedisQueueService();
        HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .registerHandler("/push", new PushHandler())
                .registerHandler("/pull", new PullHandler())
                .registerHandler("/delete", new DeleteHandler())
                .create();
        server.start();
        System.out.println(" server started");
    }

    static class PushHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            RequestBody reqBody = HTTPRequestParser.parseRequestBody(((HttpEntityEnclosingRequest) request).getEntity().getContent());

            try {
                queue.push(reqBody.getQueueUrl(), reqBody.getMessageBody());
                response.setStatusCode(HttpStatus.SC_OK);
                StringEntity entity = new StringEntity("Message Pushed Successfully", ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                StringEntity entity = new StringEntity("Internal Server Error", ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            }
        }
    }

    static class PullHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            try {
                RequestBody reqBody = HTTPRequestParser.parseRequestBody(((HttpEntityEnclosingRequest) request).getEntity().getContent());
                Message message = queue.pull(reqBody.getQueueUrl());

                response.setStatusCode(HttpStatus.SC_OK);
                StringEntity entity = new StringEntity(JSONMapper.objectToString(message), ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                StringEntity entity = new StringEntity("Internal Server Error", ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            }
        }
    }

    static class DeleteHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            try {
                RequestBody reqBody = HTTPRequestParser.parseRequestBody(((HttpEntityEnclosingRequest) request).getEntity().getContent());
                queue.delete(reqBody.getQueueUrl(), reqBody.getReceiptId());

                response.setStatusCode(HttpStatus.SC_OK);
                StringEntity entity = new StringEntity("deleted message with receipt id "+reqBody.getReceiptId(), ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                StringEntity entity = new StringEntity("Internal Server Error", ContentType.TEXT_PLAIN);
                response.setEntity(entity);
            }
        }
    }

}
