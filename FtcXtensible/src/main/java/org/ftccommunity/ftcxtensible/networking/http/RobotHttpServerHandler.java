///*
// * Copyright © 2016 David Sargent
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// * and associated documentation files (the "Software"), to deal in the Software without restriction,
// * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
// * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
// * the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or
// * substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
// * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */
//package org.ftccommunity.ftcxtensible.networking.http;
//
//import com.google.common.base.Charsets;
//import com.google.common.io.Files;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import android.util.Log;
//import android.webkit.MimeTypeMap;
//
//import org.ftccommunity.ftcxtensible.networking.ServerSettings;
//import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
//import org.ftccommunity.ftcxtensible.robot.RobotContext;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.LinkedList;
//
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.codec.http.DefaultFullHttpResponse;
//import io.netty.handler.codec.http.FullHttpResponse;
//import io.netty.handler.codec.http.HttpHeaders;
//import io.netty.handler.codec.http.HttpMethod;
//import io.netty.handler.codec.http.HttpRequest;
//import io.netty.handler.codec.http.HttpResponseStatus;
//import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
//import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
//import io.netty.handler.codec.http.multipart.InterfaceHttpData;
//
//import static com.google.common.base.Preconditions.checkNotNull;
//import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
//import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
//import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
//import static io.netty.handler.codec.http.HttpHeaders.Values;
//import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
//import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
//import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
//import static io.netty.handler.codec.http.HttpResponseStatus.OK;
//import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
//
///**
// * The handler for maintaining a correct HTTP request form and providing the page sends and data
// * requests
// *
// * @author David Sargent
// * @since 0.1.0
// */
//public class RobotHttpServerHandler extends ChannelInboundHandlerAdapter {
//    private HashMap<String, String> cache;
//    private HashMap<String, String> mimeTypes;
//    private ServerSettings serverSettings;
//    private RobotContext context;
//
//    public RobotHttpServerHandler(RobotContext ctx) {
//        checkNotNull(ctx);
//        serverSettings = ctx.serverSettings();
//        context = ctx;
//        cache = new HashMap<>();
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        if (msg instanceof HttpRequest) {
//            HttpRequest req = (HttpRequest) msg;
//            String page;
//
//            if (req.getMethod() == HttpMethod.POST) {
//                LinkedList<InterfaceHttpData> postData = new LinkedList<>();
//                HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
//                postRequestDecoder.getBodyHttpDatas();
//                for (InterfaceHttpData data : postRequestDecoder.getBodyHttpDatas()) {
//                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                        postData.add(data);
//                    }
//                }
//                context.addPostData(postData);
//            }
//
//            if (HttpHeaders.is100ContinueExpected(req)) {
//                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
//            }
//
//            HttpResponseStatus responseStatus = OK;
//            String uri = (req.getUri().equals("/") ? context.serverSettings().getIndex() : req.getUri());
//            if (uri.equals(context.serverSettings().getHardwareMapJsonPage())) {
//                GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
//                Gson gson = gsonBuilder.create();
//                ExtensibleHardwareMap hardwareMap = context.hardwareMap();
//                page = gson.toJson(hardwareMap.dcMotors());
//            } else if (uri.equals(context.serverSettings().getLogPage())) {
//                page = context.status().getLog();
//            } else {
//                if (cache.containsKey(uri)) {
//                    page = cache.get(uri);
//                    responseStatus = NOT_MODIFIED;
//                } else {
//                    try {
//                        page = Files.toString(new File(serverSettings.getWebDirectory() + uri), Charsets.UTF_8);
//                    } catch (FileNotFoundException e) {
//                        Log.e("NET_OP_MODE::", "Cannot find main: + " + serverSettings.getWebDirectory() + uri);
//                        page = "File Not Found!";
//                        responseStatus = NOT_FOUND;
//                    } catch (IOException e) {
//                        page = "An Error Occurred!\n" +
//                                e.toString();
//                        responseStatus = NOT_FOUND;
//                        Log.e("NET_OP_MODE::", e.toString(), e);
//                    }
//                    cache.put(uri, page);
//                }
//            }
//
//            boolean keepAlive = HttpHeaders.isKeepAlive(req);
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus,
//                    Unpooled.wrappedBuffer(page.getBytes(Charsets.UTF_8)));
//
//            String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
//            String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//
//            response.headers().set(CONTENT_TYPE, (MIME != null ? MIME : MimeForExtension(extension)) + (MIME != null && MIME.equals("application/octet-stream") ? "" : "; charset=utf-8"));
//            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//
//            if (!keepAlive) {
//                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//            } else {
//                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
//                ctx.write(response);
//            }
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        Log.e("HTTP_SERVER::", cause.toString(), cause);
//        ctx.close();
//    }
//
//    private String MimeForExtension(final String ext) {
//        if (mimeTypes == null) {
//            HashMap<String, String> mimes = new HashMap<>();
//            mimes.put("json", "application/json");
//            mimes.put("xml", "application/xml");
//            mimes.put("js", "application/javascript");
//            mimes.put("jpg", "image/jpeg");
//            mimes.put("png", "image/png");
//            mimes.put("css", "text/css");
//            mimes.put("html", "text/html");
//            mimeTypes = mimes;
//        }
//
//        if (mimeTypes.containsKey(ext)) {
//            return mimeTypes.get(ext);
//        } else {
//            return "text/plain";
//        }
//    }
//}
