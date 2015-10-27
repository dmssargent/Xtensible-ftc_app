package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Version;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Autonomous
public class Diag extends OpMode {
    private static String TAG = "DIAGNOSTICS::";

    private TextView textView;
    private ScrollView scrollView;

    @Override
    public void init() {
        StringBuilder result = new StringBuilder(this.getClass().getSimpleName() + '\n');

        String log = "Log:\n";
        try {
            Process logcat = Runtime.getRuntime().exec("logcat -d");
            Thread.sleep(1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcat.getInputStream()));

            CharBuffer chars = CharBuffer.allocate(8192);
            reader.read(chars);
            log += chars.toString();
        } catch (IOException e) {
            Log.e(TAG, "Cannot start logcat monitor", e);
            log += e.getLocalizedMessage();
        } catch (InterruptedException e) {
            return;
        }

        if (log.equals("Log:\n")) {
            FileInputStream logcatFile;
            try {
                File file = new File("/sdcard/com.qualcomm.ftcrobotcontroller.logcat");
                logcatFile = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(logcatFile);

                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                log += new String(chars);
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
        result.append("Board: ").append(Build.BOARD).append('\n'); //board
        result.append("Brand: ").append(Build.BRAND).append('\n'); //brand
        result.append("Model No: ").append(Build.MODEL).append('\n'); //model
        result.append("RC Serial: ").append(Build.SERIAL).append('\n'); //serial no
        result.append("Android Version: ").append(Build.VERSION.RELEASE).append('\n'); // android version

        String rcVersion;
        try {
            rcVersion = "RC Version: " + hardwareMap.appContext.getPackageManager().
                    getPackageInfo(this.getClass().getPackage().getName(), 0).versionName + '\n';
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            rcVersion = e.getLocalizedMessage() + '\n';
        }
        result.append(rcVersion);

        result.append("RC Library: ").append(Version.getLibraryVersion()).append('\n'); // rc library

        StringBuilder deviceLogBuilder = new StringBuilder("Devices: \n");
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.dcMotorController)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.dcMotor)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.servoController)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.servo)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.legacyModule)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.deviceInterfaceModule)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.analogInput)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.digitalChannel)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.pwmOutput)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.i2cDevice)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.analogOutput)).details());
        deviceLogBuilder.append((new DeviceMap<>(hardwareMap.led)).details());


        result.append(deviceLogBuilder).append(log).trimToSize();

        String msg = result.toString();
        Log.i(this.getClass().getSimpleName(), msg);
        RobotLog.setGlobalErrorMsg(msg.substring(0, msg.length() < 256 ? msg.length() : 255));

        final RelativeLayout layout = (RelativeLayout)
                ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);
        final String message = msg;
        layout.post(new Runnable() {
            @Override
            public void run() {
                textView = new TextView(hardwareMap.appContext);
                textView.setText(message);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.textErrorMessage);
                scrollView = new ScrollView(hardwareMap.appContext);
                scrollView.addView(textView);
                layout.addView(scrollView, layoutParams);
            }
        });

        File directory = new File("/sdcard/FIRST/diag/");
        final File diagFile = new File(directory + "diag_" + System.currentTimeMillis() + ".log");
        directory.mkdirs();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(diagFile.getPath());
            fileOutputStream.write(msg.getBytes(Charset.defaultCharset()));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException ex) {
            Log.e(TAG, ex.getLocalizedMessage(), ex);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(hardwareMap.appContext)
                        .setMessage("Send Report?")
                        .setTitle("Report")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "FTC Robot Controller: Error Log report");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dmssargent@yahoo.com"});
                                intent.putExtra(Intent.EXTRA_TEXT, "Please see attached file");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(diagFile));
                                intent.setType("text/plain");
                                Intent.createChooser(intent, "Send Report");
                                hardwareMap.appContext.startActivity(intent);
                            }
                        }).setCancelable(true).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialogBuilder.create().show();
            }
        });

        hardwareMap.dcMotorController.get("").getClass().getSimpleName();
    }

    @Override
    public void loop() {

    }

    private void runOnUiThread(Runnable runnable) {
        final RelativeLayout layout = ((RelativeLayout) ((Activity) hardwareMap.appContext)
                .findViewById(R.id.RelativeLayout));
        layout.post(runnable);
    }

    @Override
    public void stop() {
        final RelativeLayout layout = ((RelativeLayout) ((Activity) hardwareMap.appContext)
                .findViewById(R.id.RelativeLayout));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeView(scrollView);
            }
        });

        textView = null;
        scrollView = null;

        Class klazz = this.getClass();
    }

}


class DeviceMap<K, T> extends HashMap<String, T> {
    public DeviceMap(HardwareMap.DeviceMapping<T> deviceMapping) {
        super(deviceMapping.size());
        buildFromDeviceMapping(deviceMapping);
    }

    public DeviceMap<K, T> buildFromDeviceMapping(HardwareMap.DeviceMapping<T> deviceMapping) {
        Set<Entry<String, T>> entries = deviceMapping.entrySet();
        for (Entry<String, T> device : entries) {
            super.put(device.getKey(), device.getValue());
        }
        return this;
    }

    public String details() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, T> entry : entrySet()) {
            T value = entry.getValue();
            stringBuilder.append(value.getClass().getSimpleName()).append(" ")
                    .append(entry.getKey()).append(" ")
                    .append(value.toString()).append('\n');
        }
        return stringBuilder.toString();
    }
}