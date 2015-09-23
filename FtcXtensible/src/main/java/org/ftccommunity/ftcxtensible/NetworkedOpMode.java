/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible;

import org.ftccommunity.ftcxtensible.networking.http.RobotHttpServer;

/**
 * The Networking components of the library; inherit this class to use the Networking components
 * in an {@link com.qualcomm.robotcore.eventloop.opmode.OpMode}
 *
 * @author David Sargent
 * @since 0.1
 */
public class NetworkedOpMode {
    private Thread serverThread;
    private RobotHttpServer server;
    private RobotContext context;

    /**
     * Create the networked version of an OpMode
     */
    protected NetworkedOpMode(RobotContext ctx) {
        super();
        context = ctx;
        server = new RobotHttpServer(context);
        serverThread = new Thread(server);
    }

    /**
     * Handles the server bootstrap, starts the HTTP server in a new thread
     */
    public void startServer() {
        serverThread.start();
    }

    /**
     * Kills the HTTP server
     */
    public void stopServer() {
        serverThread.interrupt();
    }
}
