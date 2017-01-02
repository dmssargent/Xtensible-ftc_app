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
package org.firstinspires.ftc.teamcode.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.google.common.collect.HashBiMap;
import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RunShellCommand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Autonomous
@Disabled
public class NetworkAnalyzer extends OpMode {
    private WifiManager wifi;
    private String mostCommon = "None";
    private String secondMostCommon = "None";

    private HashBiMap<Integer, String> wifiChannelMap;
    private String operChannel;
    private String recChannel = "Not Ready";

    @Override
    public void init() {
        wifiChannelMap = HashBiMap.create();
        wifiChannelMap.put(2412, "2.4G Ch01");
        wifiChannelMap.put(2417, "2.4G Ch02");
        wifiChannelMap.put(2422, "2.4G Ch03");
        wifiChannelMap.put(2427, "2.4G Ch04");
        wifiChannelMap.put(2432, "2.4G Ch05");
        wifiChannelMap.put(2437, "2.4G Ch06");
        wifiChannelMap.put(2442, "2.4G Ch07");
        wifiChannelMap.put(2447, "2.4G Ch08");
        wifiChannelMap.put(2452, "2.4G Ch09");
        wifiChannelMap.put(2457, "2.4G Ch10");
        wifiChannelMap.put(2462, "2.4G Ch11");
        wifiChannelMap.put(2467, "2.4G Ch12");
        wifiChannelMap.put(2472, "2.4G Ch13");
        wifiChannelMap.put(2484, "2.4G Ch14");

        wifi = (WifiManager) hardwareMap.appContext.getSystemService(Context.WIFI_SERVICE);
        hardwareMap.appContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LinkedList<ScanResult> results = new LinkedList<>(wifi.getScanResults());
                scanResults(results);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.startScan();

        try {
            String absolutePath = hardwareMap.appContext.getFilesDir().getAbsolutePath() + "/";
            RunShellCommand cmd = new RunShellCommand();
            String output;
            if ((output = cmd.run(String.format("cp /data/misc/wifi/p2p_supplicant.conf %sp2p_supplicant.conf \n", absolutePath))).length() > 0) {
                RobotLog.e("Cannot copy p2p file" + output);
                operChannel = output;
            }


            String fileData = Files.toString(new File(absolutePath + "p2p_supplicant.conf"), Charset.defaultCharset());
            String[] datas = fileData.split("/n");
            for (String data : datas) {
                if (data.contains("p2p_oper_channel")) {
                    operChannel = data.substring(data.indexOf("="));
                }
            }
        } catch (IOException ex) {
            if (operChannel.equals("")) {
                operChannel = ex.getMessage();
            }
        }
    }

    @Override
    public void loop() {
        telemetry.addData("Most Common Freq: ", mostCommon);
        telemetry.addData("Second Most Common Freq: ", secondMostCommon);
        telemetry.addData("Recommended Channel", recChannel);
        telemetry.addData("Current Channel: ", operChannel);
    }

    private void scanResults(LinkedList<ScanResult> results) {
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Integer> frequencyCount = new HashMap<>();

        for (ScanResult result : results) {
            WifiManager.calculateSignalLevel(result.level, 100);

            if (frequencyCount.get(result.frequency) == 0) {
                int count = frequencyCount.get(result.frequency);
                frequencyCount.put(result.frequency, ++count);
            } else {
                frequencyCount.put(result.frequency, 1);
            }

            RobotLog.d("Network on: " + wifiChannelMap.get(result.frequency));
        }
        RobotLog.d("Done displaying networks!");

        LinkedList<Map.Entry<Integer, Integer>> frequencies = new LinkedList<>(frequencyCount.entrySet());
        Collections.sort(frequencies, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> lhs, Map.Entry<Integer, Integer> rhs) {
                if (lhs.equals(rhs)) {
                    return 0;
                }

                if (lhs.getValue() > rhs.getValue()) {
                    return 1;
                } else if (lhs.getValue() < rhs.getValue()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        if (frequencies.size() > 0) {
            int location = frequencies.size() - 1;
            mostCommon = wifiChannelMap.containsKey(frequencies.get(location).getKey()) ?
                    wifiChannelMap.get(frequencies.get(location).getKey()) : "5 GHz";
        }

        if (frequencies.size() > 1) {
            int location = frequencies.size() - 2;
            secondMostCommon = wifiChannelMap.containsKey(frequencies.get(location).getKey()) ?
                    wifiChannelMap.get(frequencies.get(location).getKey()) : "5 GHz";
        }

        recChannel = recommendedChannel();
    }

    private String recommendedChannel() {
        int mostCommonChannel = wifiChannelMap.inverse().get(mostCommon);
        int secondMostCommonChannel = wifiChannelMap.inverse().get(secondMostCommon);

        int[] recommendations = secondMostRecommended(secondMostCommonChannel);
        if (mostCommonChannel <= 2422) { // Most Common is Channel 1-3
            if (recommendations[0] > 2452) {
                return wifiChannelMap.get(recommendations[0]);
            } else {
                return wifiChannelMap.get(recommendations[1]);
            }
        } else if (mostCommonChannel < 2452) { // Channel 4-9
            if (recommendations[0] <= 2422) {
                return wifiChannelMap.get(recommendations[0]);
            } else {
                return wifiChannelMap.get(recommendations[1]);
            }
        } else { // Channel 9-14
            if (recommendations[0] < 2452) {
                return wifiChannelMap.get(recommendations[0]);
            } else {
                return wifiChannelMap.get(recommendations[1]);
            }
        }
    }

    private int[] secondMostRecommended(int secondChannel) {
        int[] results = new int[2];

        if (secondChannel <= 2422) { // Channel 1-3
            results[0] = 2462; // Channel 11
            results[1] = 2437; // Channel 6
        } else if (secondChannel < 2452) { // Channel 4-9
            results[0] = 2412; // Channel 1
            results[1] = 2462; // Channel 11
        } else { // Channel 9-14
            results[0] = 2412; // Channel 1
            results[1] = 2437; // Channel 6
        }

        return results;
    }
}
