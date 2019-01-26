package com.paxos.messages;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class MessageHandler extends SimpleChannelInboundHandler<HttpObject> {
    static final String MESSAGE_URI = "/message";
    static final String MESSAGES_URI = "/messages/";

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) httpObject;
        String uri = httpRequest.uri();
        HttpMethod method = httpRequest.method();
        if (MESSAGE_URI.equalsIgnoreCase(uri) && method == HttpMethod.POST) {
            if (httpObject instanceof HttpContent) {
                ByteBuf byteBuf = ((HttpContent) httpObject).content();
                String requestBody = byteBuf.toString(CharsetUtil.UTF_8);
                Gson gson = new Gson();
                RequestMessage requestMessage = gson.fromJson(requestBody, RequestMessage.class);
                System.out.println("Received message: " + requestMessage.message);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedHash = digest.digest(requestMessage.message.getBytes());

                String hexString = bytesToHex(encodedHash);
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("{\"digest\": \"").append(hexString).append("\"}");
                System.out.println(jsonResponse.toString());

                cache.put(hexString, requestMessage.message);

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                        Unpooled.copiedBuffer(jsonResponse.toString(), CharsetUtil.UTF_8));
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
                channelHandlerContext.writeAndFlush(response).addListener(f -> {
                    if (!f.isSuccess()) {
                        System.out.println("Failed to send response to client");
                    }
                    channelHandlerContext.close();
                });
            }
        } else if (uri.startsWith(MESSAGES_URI) && method == HttpMethod.GET) {
            String[] paths = uri.split("/");
            String digest = paths.length > 2 ? paths[2] : "";
            FullHttpResponse response = Optional.ofNullable(cache.get(digest))
                    .map(m -> {
                        StringBuilder jsonResponse = new StringBuilder();
                        jsonResponse.append("{\"message\": \"").append(m).append("\"}");
                        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(jsonResponse.toString(), CharsetUtil.UTF_8));
                    })
                    .orElseGet(() -> {
                        StringBuilder jsonResponse = new StringBuilder();
                        jsonResponse.append("{\"err_msg\": \"Message not found\"}");
                        return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.copiedBuffer(jsonResponse.toString(), CharsetUtil.UTF_8));
                    });
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            channelHandlerContext.writeAndFlush(response).addListener(f -> {
                if (!f.isSuccess()) {
                    System.out.println("Failed to send response to client");
                }
                channelHandlerContext.close();
            });
        } else {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            channelHandlerContext.writeAndFlush(response).addListener(f ->
                channelHandlerContext.close()
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        if (cause instanceof JsonSyntaxException) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            ctx.writeAndFlush(response);
        }
        cause.printStackTrace();
        ctx.close();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
