package com.qualcomm.analytics;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Analytics extends BroadcastReceiver {
    public static final String UUID_PATH = ".analytics_id";
    public static final String DATA_COLLECTION_PATH = ".ftcdc";
    public static final String RC_COMMAND_STRING = "update_rc";
    public static final String DS_COMMAND_STRING = "update_ds";
    public static final String EXTERNAL_STORAGE_DIRECTORY_PATH = Environment.getExternalStorageDirectory() + "/";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    public static final String MAX_DEVICES = "max_usb_devices";
    static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static final Charset CHARSET = Charset.forName("UTF-8");
    public static int MAX_ENTRIES_SIZE = 100;
    public static int TRIMMED_SIZE = 90;
    static String DATA_API_URL = "https://ftcdc.qualcomm.com/DataApi";
    static long currentTime;
    static UUID uuid = null;
    static String applicationName;
    static String libraryVersion = "";
    String commandString;
    Context context;
    SharedPreferences sharedPreferences;
    boolean i = false;
    long lastUploadDate = 0L;
    int k = 0;

    public Analytics(Context context, String commandString, HardwareMap map) {
        this.context = context;
        this.commandString = commandString;

        try {
            try {
                this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                currentTime = System.currentTimeMillis();
                libraryVersion = Version.getLibraryVersion();
                this.handleUUID(UUID_PATH);
                int numberOfUsbDevices = this.calculateUsbDevices(map);
                int knownNumberOfUsbDevices = this.sharedPreferences.getInt(MAX_DEVICES, this.k);
                if (knownNumberOfUsbDevices < numberOfUsbDevices) {
                    Editor editor = this.sharedPreferences.edit();
                    editor.putInt(MAX_DEVICES, numberOfUsbDevices);
                    editor.apply();
                }

                setApplicationName(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());
                handleData();
                register();
                RobotLog.i("Analytics has completed initialization.");
            } catch (Exception ex) {
                this.i = true;
            }

            if (this.i) {
                this.cleanState();
                this.i = false;
            }
        } catch (Exception ex) {
            RobotLog.i("Analytics encountered a problem during initialization");
            RobotLog.logStacktrace(ex);
        }

    }

    public static void setApplicationName(String name) {
        applicationName = name;
    }

    public static String getDateFromTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date(time));
    }

    protected static UUID getUuid() {
        return uuid;
    }

    public static String ping(URL baseUrl, String data) {
        return call(baseUrl, data);
    }

    public static String call(URL url, String data) {
        String var4 = null;
        if (url != null && data != null) {
            try {
                long startTime = System.currentTimeMillis();
                Object var5;
                if (url.getProtocol().toLowerCase().equals("https")) {
                    c();
                    HttpsURLConnection var6 = (HttpsURLConnection) url.openConnection();
                    var6.setHostnameVerifier(HOSTNAME_VERIFIER);
                    var5 = var6;
                } else {
                    var5 = url.openConnection();
                }

                ((HttpURLConnection) var5).setDoOutput(true);
                OutputStreamWriter var7 = new OutputStreamWriter(((HttpURLConnection) var5).getOutputStream());
                var7.write(data);
                var7.flush();
                var7.close();
                BufferedReader var10 = new BufferedReader(new InputStreamReader(((HttpURLConnection) var5).getInputStream()));

                String var8;
                for (var4 = ""; (var8 = var10.readLine()) != null; var4 = var4 + var8) {
                }

                var10.close();
                RobotLog.i("Analytics took: " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException ex) {
                RobotLog.i("Analytics Failed to process command.");
            }
        }

        return var4;
    }

    private static void c() {
        TrustManager[] var0 = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, var0, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
        } catch (Exception ex) {
            RobotLog.e("Analytics encountered an error.\n%s", ex);
        }

    }

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("networkInfo")) {
            NetworkInfo var4 = (NetworkInfo) bundle.get("networkInfo");
            State var5 = var4.getState();
            if (var5.equals(State.CONNECTED)) {
                RobotLog.i("Analytics detected NetworkInfo.State.CONNECTED");
                this.communicateWithServer();
            }
        }

    }

    public void unregister() {
        this.context.unregisterReceiver(this);
    }

    public void register() {
        this.context.registerReceiver(this, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    protected int calculateUsbDevices(HardwareMap map) {
        byte var2 = 0;
        int var7 = var2 + map.legacyModule.size();
        var7 += map.deviceInterfaceModule.size();
        Iterator var3 = map.servoController.iterator();

        String var5;
        Pattern pattern = Pattern.compile("(?i)usb");
        while (var3.hasNext()) {
            ServoController var4 = (ServoController) var3.next();
            var5 = var4.getDeviceName();
            if (pattern.matcher(var5).find()) {
                ++var7;
            }
        }

        var3 = map.dcMotorController.iterator();

        while (var3.hasNext()) {
            DcMotorController var8 = (DcMotorController) var3.next();
            var5 = var8.getDeviceName();
            pattern = Pattern.compile("(?i)usb");
            if (pattern.matcher(var5).find()) {
                ++var7;
            }
        }

        return var7;
    }

    protected void handleData() throws IOException, ClassNotFoundException {
        String var1 = EXTERNAL_STORAGE_DIRECTORY_PATH + DATA_COLLECTION_PATH;
        File var2 = new File(var1);
        if (!var2.exists()) {
            this.createInitialFile(var1);
        } else {
            ArrayList<DataInfo> var3 = this.updateExistingFile(var1, getDateFromTime(currentTime));
            if (var3.size() >= MAX_ENTRIES_SIZE) {
                this.trimEntries(var3);
            }

            this.writeObjectsToFile(var1, var3);
        }

    }

    protected void trimEntries(ArrayList<Analytics.DataInfo> dataInfoArrayList) {
        dataInfoArrayList.subList(TRIMMED_SIZE, dataInfoArrayList.size()).clear();
    }

    protected ArrayList<Analytics.DataInfo> updateExistingFile(String filepath, String date) throws ClassNotFoundException, IOException {
        ArrayList<DataInfo> var3 = this.readObjectsFromFile(filepath);
        Analytics.DataInfo var4 = var3.get(var3.size() - 1);
        if (var4.date.equalsIgnoreCase(date)) {
            ++var4.numUsages;
        } else {
            Analytics.DataInfo var5 = new Analytics.DataInfo(date, 1);
            var3.add(var5);
        }

        return var3;
    }

    protected ArrayList<Analytics.DataInfo> readObjectsFromFile(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream var2 = new FileInputStream(new File(filepath));
        ObjectInputStream var3 = new ObjectInputStream(var2);
        ArrayList<DataInfo> var4 = new ArrayList<>();
        boolean var5 = true;

        while (var5) {
            try {
                Analytics.DataInfo var6 = (Analytics.DataInfo) var3.readObject();
                var4.add(var6);
            } catch (EOFException var7) {
                var5 = false;
            }
        }

        var3.close();
        return var4;
    }

    protected void createInitialFile(String filepath) throws IOException {
        Analytics.DataInfo var2 = new Analytics.DataInfo(getDateFromTime(currentTime), 1);
        ArrayList<DataInfo> var3 = new ArrayList<>();
        var3.add(var2);
        this.writeObjectsToFile(filepath, var3);
    }

    private void cleanState() {
        RobotLog.i("Analytics is starting with a clean slate.");
        Editor editor = this.sharedPreferences.edit();
        editor.putLong(LAST_UPLOAD_DATE, this.lastUploadDate);
        editor.apply();
        editor.putInt(MAX_DEVICES, 0);
        editor.apply();
        File dataCollectionFile = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + DATA_COLLECTION_PATH);
        if (!dataCollectionFile.delete()) {
            RobotLog.w("Deleting file " + dataCollectionFile + " failed.");
        }
        File uuidFile = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + UUID_PATH);
        if (!uuidFile.delete()) {
            RobotLog.w("Deleting file " + uuidFile + " failed.");
        }
        this.i = false;
    }

    public void communicateWithServer() {
        String[] var1 = new String[]{DATA_API_URL};
        (new AnalyticsUploadTask()).execute(var1);
    }

    public void handleUUID(String filename) {
        File var2 = new File(EXTERNAL_STORAGE_DIRECTORY_PATH + filename);
        if (!var2.exists()) {
            uuid = UUID.randomUUID();
            this.handleCreateNewFile(EXTERNAL_STORAGE_DIRECTORY_PATH + filename, uuid.toString());
        }

        String var3 = this.readFromFile(var2);

        try {
            uuid = UUID.fromString(var3);
        } catch (IllegalArgumentException var5) {
            RobotLog.i("Analytics encountered an IllegalArgumentException");
            uuid = UUID.randomUUID();
            this.handleCreateNewFile(EXTERNAL_STORAGE_DIRECTORY_PATH + filename, uuid.toString());
        }

    }

    protected String readFromFile(File file) {
        try {
            char[] var2 = new char[4096];
            FileReader var3 = new FileReader(file);
            int var4 = var3.read(var2);
            var3.close();
            String var5 = new String(var2, 0, var4);
            return var5.trim();
        } catch (FileNotFoundException var6) {
            RobotLog.i("Analytics encountered a FileNotFoundException while trying to read a file.");
        } catch (IOException var7) {
            RobotLog.i("Analytics encountered an IOException while trying to read.");
        }

        return "";
    }

    protected void writeObjectsToFile(String filepath, ArrayList<Analytics.DataInfo> info) throws IOException {
        ObjectOutputStream var3 = new ObjectOutputStream(new FileOutputStream(filepath));
        Iterator var4 = info.iterator();

        while (var4.hasNext()) {
            Analytics.DataInfo var5 = (Analytics.DataInfo) var4.next();
            var3.writeObject(var5);
        }

        var3.close();
    }

    protected void handleCreateNewFile(String filepath, String data) {
        BufferedWriter var3 = null;

        try {
            File var4 = new File(filepath);
            FileOutputStream var5 = new FileOutputStream(var4);
            var3 = new BufferedWriter(new OutputStreamWriter(var5, "utf-8"));
            var3.write(data);
        } catch (IOException var14) {
            RobotLog.i("Analytics encountered an IOException: " + var14.toString());
        } finally {
            if (var3 != null) {
                try {
                    var3.close();
                } catch (IOException ignored) {

                }
            }

        }

    }

    public String updateStats(String user, ArrayList<Analytics.DataInfo> dateInfo, String commandString) {
        String var4 = this.a("cmd", "=", commandString) + "&" + this.a("uuid", "=", user) + "&" + this.a("device_hw", "=", Build.MANUFACTURER) + "&" + this.a("device_ver", "=", Build.MODEL) + "&" + this.a("chip_type", "=", this.b()) + "&" + this.a("sw_ver", "=", libraryVersion) + "&";
        if (commandString.equalsIgnoreCase(RC_COMMAND_STRING)) {
            int var5 = this.sharedPreferences.getInt(MAX_DEVICES, this.k);
            var4 = var4 + this.a("max_dev", "=", String.valueOf(var5)) + "&";
        }

        String var7 = "";

        for (int var6 = 0; var6 < dateInfo.size(); ++var6) {
            if (var6 > 0) {
                var7 = var7 + ",";
            }

            var7 = var7 + this.a((dateInfo.get(var6)).date(), ",", String.valueOf((dateInfo.get(var6)).numUsages()));
        }

        var4 = var4 + this.a("dc", "=", "");
        var4 = var4 + var7;
        return var4;
    }

    private String a(String var1, String var2, String var3) {
        String var4 = "";

        try {
            var4 = URLEncoder.encode(var1, CHARSET.name()) + var2 + URLEncoder.encode(var3, CHARSET.name());
        } catch (UnsupportedEncodingException var6) {
            RobotLog.i("Analytics caught an UnsupportedEncodingException");
        }

        return var4;
    }

    private String b() {
        String var1 = "UNKNOWN";
        final String CPU_INFO_LOCATION = "/proc/cpuinfo";
        String[] var3 = new String[]{"CPU implementer", "Hardware"};
        HashMap<String, String> var4 = new HashMap<>();

        try {
            BufferedReader var5 = new BufferedReader(new FileReader(CPU_INFO_LOCATION));

            for (String var6 = var5.readLine(); var6 != null; var6 = var5.readLine()) {
                var6 = var6.toLowerCase();
                String[] var7 = var6.split(":");
                if (var7.length >= 2) {
                    var4.put(var7[0].trim(), var7[1].trim());
                }
            }

            var5.close();
            String var14 = "";

            for (String segment : var3) {
                var14 += var4.get(segment.toLowerCase()) + " ";
            }

            var14 = var14.trim();
            if (var14.isEmpty()) {
                return var1;
            }

            return var14;
        } catch (FileNotFoundException var12) {
            RobotLog.i("Analytics encountered a FileNotFoundException while looking for CPU info");
        } catch (IOException var13) {
            RobotLog.i("Analytics encountered an IOException while looking for CPU info");
        }

        return var1;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static class DataInfo implements Serializable {
        private final String date;
        protected int numUsages;

        public DataInfo(String date, int numUsages) {
            this.date = date;
            this.numUsages = numUsages;
        }

        public String date() {
            return this.date;
        }

        public int numUsages() {
            return this.numUsages;
        }
    }

    private class AnalyticsUploadTask extends AsyncTask<Object, Object, Object> {
        protected Object doInBackground(Object... params) {
            if (Analytics.this.isConnected()) {
                try {
                    URL var2 = new URL(Analytics.DATA_API_URL);
                    long var3 = Analytics.this.sharedPreferences.getLong(LAST_UPLOAD_DATE, Analytics.this.lastUploadDate);
                    if (!Analytics.getDateFromTime(Analytics.currentTime).equals(Analytics.getDateFromTime(var3))) {
                        label53:
                        {
                            String var5 = Analytics.this.a("cmd", "=", "ping");
                            String var6 = Analytics.ping(var2, var5);
                            String var7 = "\"rc\": \"OK\"";
                            if (var6 != null && var6.contains(var7)) {
                                RobotLog.i("Analytics ping succeeded.");
                                String var8 = Analytics.EXTERNAL_STORAGE_DIRECTORY_PATH + DATA_COLLECTION_PATH;
                                ArrayList var9 = Analytics.this.readObjectsFromFile(var8);
                                if (var9.size() >= Analytics.MAX_ENTRIES_SIZE) {
                                    Analytics.this.trimEntries(var9);
                                }

                                String var10 = Analytics.this.updateStats(Analytics.uuid.toString(), var9, Analytics.this.commandString);
                                String var11 = Analytics.call(var2, var10);
                                if (var11 != null && var11.contains(var7)) {
                                    RobotLog.i("Analytics: Upload succeeded.");
                                    Editor var12 = Analytics.this.sharedPreferences.edit();
                                    var12.putLong(LAST_UPLOAD_DATE, Analytics.currentTime);
                                    var12.apply();
                                    var12.putInt(MAX_DEVICES, 0);
                                    var12.apply();
                                    File var13 = new File(var8);
                                    if (!var13.delete()) {
                                        RobotLog.w("Failed to delete file: " + var13);
                                    }
                                    break label53;
                                }

                                RobotLog.e("Analytics: Upload failed.");
                                return null;
                            }

                            RobotLog.e("Analytics: Ping failed.");
                            return null;
                        }
                    }
                } catch (MalformedURLException var15) {
                    RobotLog.e("Analytics encountered date malformed URL exception");
                } catch (Exception var16) {
                    Analytics.this.i = true;
                }

                if (Analytics.this.i) {
                    RobotLog.i("Analytics encountered date problem during communication");
                    Analytics.this.cleanState();
                    Analytics.this.i = false;
                }
            }

            return null;
        }
    }
}
