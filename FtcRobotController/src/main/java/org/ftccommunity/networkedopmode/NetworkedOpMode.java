package org.ftccommunity.networkedopmode;

import org.ftccommunity.networkedopmode.networking.http.HttpHelloWorldServer;

public class NetworkedOpMode {
    private Thread serverThread;
    private HttpHelloWorldServer server;
    private RobotContext context;

    public NetworkedOpMode(RobotContext ctx) {
        context = ctx;
        /*FileInputStream fileStream;
        String main;
        try {
            //fileStream = ctx.openFileInput(new File("/sdcard/FIRST/web/index.html"));
            fileStream = new FileInputStream(new File("/sdcard" + "/FIRST/web/index.html"));
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
            String temp;
            while (!((temp = reader.readLine()) == null)) {
                stringBuilder.append(temp);
            }
            main = stringBuilder.toString();
            fileStream.close();
        } catch (FileNotFoundException e) {
            Log.e("NET_OP_MODE::", "Cannot find main: " + "/sdcard" + "/FIRST/web/index.html");
            main = "File Not Found!";
        } catch (IOException e) {
            main = "An Error Occurred!\n" +
                    e.toString();
            Log.e("NET_OP_MODE::", e.toString(), e);
        }*/
        server = new HttpHelloWorldServer(context);
        serverThread = new Thread(server);
    }

    public void startServer() {
        serverThread.start();
    }

    public void stopServer() {
        serverThread.interrupt();
    }
}
