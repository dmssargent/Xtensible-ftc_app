/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ftccommunity.ftcxtensible.networking.http;

import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.robot.RobotContext;

/**
 * An HTTP server that sends back the content of the received HTTP request in stopMode pretty
 * plaintext form.
 *
 * @author David Sargent
 * @since 0.1.0
 */
public final class RobotHttpServer implements Runnable {
    static final int PORT = 8080;
    private RobotContext context;

    /**
     * Creates a Robot HTTP Server, based off the core values within the Robot Context
     *
     * @param ctx Robot Context
     */
    public RobotHttpServer(RobotContext ctx) {
        context = ctx;
    }

    /**
     * The server thread implementation. This loads the Netty server and runs as the Netty server.
     */
    public void run() {
        // Configure the server.

        // TODO: 12/19/2016 update info to newer version
        RobotLog.e("Networking is disabled, since 0.8.0");
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            try {
//                ServerBootstrap SimulatedUsbDevice = new ServerBootstrap();
//                SimulatedUsbDevice.option(ChannelOption.SO_BACKLOG, 1024);
//                SimulatedUsbDevice.group(bossGroup, workerGroup)
//                        .channel(NioServerSocketChannel.class)
//                        .handler(new LoggingHandler(LogLevel.INFO))
//                                //.childHandler(new HttpHelloWorldServerInitializer(sslCtx, main));
//                        .childHandler(new ChannelInitializer<SocketChannel>() {
//                            /**
//                             * This method will be called once the {@link Channel} was registered. After the method returns this instance
//                             * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
//                             *
//                             * @param ch the {@link Channel} which was registered.
//                             * @throws Exception is thrown if an error occurs. In that case the {@link Channel} will be closed.
//                             */
//                            @Override
//                            protected void initChannel(SocketChannel ch) throws Exception {
//                                ch.pipeline()
//                                        .addLast(new HttpServerCodec(),
//                                                new org.ftccommunity.ftcxtensible.networking.http.RobotHttpServerHandler(context));
//                            }
//                        });
//
//                Channel ch = SimulatedUsbDevice.bind(PORT).sync().channel();
//
//                System.err.println("Open your web browser and navigate to " +
//                        ("http") + "://127.0.0.1:" + PORT + '/');
//
//                ch.closeFuture().sync();
//            } finally {
//                bossGroup.shutdownGracefully();
//                workerGroup.shutdownGracefully();
//            }
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        Log.i("NET_OP_MODE::", "OpMode Server is shutting down.");
    }
}

