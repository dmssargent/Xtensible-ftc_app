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

package org.ftccommunity.ftcxtensible.gamepad.learning;

import android.util.Log;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeIOException;
import org.ftccommunity.ftcxtensible.core.io.Files2;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class GamepadRecord implements Iterable<GamepadState> {
    private final static String RECORD_DIR = "/sdcard/xtensible/gamepad/records/";
    private final static List<GamepadRecord> RECORDS = getAvailableRecords();
    private static final String VERSION = "0.1";
    private final String name;
    private final int id;
    private final LinkedList<GamepadState> states;
    private transient int index = 0;

    public GamepadRecord(String name) {
        this.name = checkNotNull(name);
        this.id = nextRecordId();
        this.states = new LinkedList<>();


    }

    public static int nextRecordId(List<GamepadRecord> records) {
        Map<Integer, GamepadRecord> recordMap = toIdMap(records);

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!recordMap.containsKey(i)) {
                return i;
            }
        }

        throw new IllegalArgumentException("The list of gamepad records doesn't have an available slot left between 0-" + Integer.MAX_VALUE);
    }

    @NotNull
    public static List<GamepadRecord> getAvailableRecords() {
        File gamepadDir = getRecordDir();
        List<File> files = Arrays.asList(gamepadDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getPath().endsWith(".gsr.json");
            }
        }));

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        List<GamepadRecord> records = new LinkedList<>();
        for (File possibleFile : files) {
            try {
                GamepadRecord record = gson.fromJson(Files.newReader(possibleFile, Charset.forName("UTF-8")), GamepadRecord.class);
                if (record == null) {
                    continue;
                }
                records.add(record);
            } catch (FileNotFoundException e) {
                Log.e("GAMEPAD_RECORDS", "The file \"" + possibleFile.getPath() + "\" did not exist when it was attempted to be parsed", e);
            } catch (JsonIOException ex) {
                Log.e("GAMEPAD_RECORDS", "The file \"" + possibleFile.getPath() + "\" was unable to be read from its input stream", ex);
            } catch (JsonSyntaxException syntax) {
                Log.e("GAMEPAD_RECORDS", "The file \"" + possibleFile.getPath() + "\" has a syntax error", syntax);
            }
        }

        return records;
    }

    private static Map<Integer, GamepadRecord> toIdMap(List<GamepadRecord> records) {
        HashMap<Integer, GamepadRecord> recordMap = new HashMap<>();
        for (GamepadRecord record : records) {
            if (recordMap.containsKey(record.id)) {
                throw new IllegalArgumentException("The list of gamepad records contain non-unique ids; this is unsupported. The conflict is " + record.id + " in which the record " + recordMap.get(record.id) + " would be overridden by " + record);
            }
            recordMap.put(record.id, record);
        }

        return recordMap;
    }

    public static Map<String, GamepadRecord> toNameMap(List<GamepadRecord> records) {
        HashMap<String, GamepadRecord> recordMap = new HashMap<>();
        for (GamepadRecord record : records) {
            if (recordMap.containsKey(record.name)) {
                throw new IllegalArgumentException("The list of gamepad records contain non-unique names; this is unsupported. The conflict is " + record.name + " in which the record " + recordMap.get(record.name) + " would be overridden by " + record);
            }
            recordMap.put(record.name, record);
        }

        return recordMap;
    }

    public static Map<String, GamepadRecord> getMap() {
        return toNameMap(getAvailableRecords());
    }

    private static File getRecordDir() throws RuntimeIOException {
        if (!Files2.mkdirs(RECORD_DIR)) {
            throw new IllegalStateException("Cannot make the record directory");
        } else {
            return new File(RECORD_DIR);
        }
    }

    public void addRecord(@NotNull GamepadState state) {
        states.add(checkNotNull(state));
    }

    public GamepadState nextRecord() {
        try {
            return states.get(index++);
        } catch (IndexOutOfBoundsException ex) {
            Log.e("GAMEPAD RECORD", "End of Record! Returning null");
            return null;
        }
    }

    public boolean isFinished() {
        return index == states.size() - 1;
    }

    public Iterator<GamepadState> iterator() {
        return states.iterator();
    }

    public String name() {
        return name;
    }

    public int id() {
        return id;
    }

    public int nextRecordId() {
        return nextRecordId(RECORDS);
    }

    public void save() throws IOException {
        File file = new File(RECORD_DIR + name + ".gsr");
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            br.write("GSr" + VERSION);
            br.write(this.id + ";" + this.name);
            GamepadState lastState = null;
            for (GamepadState state : states) {
                br.write(state.compressedString(lastState));
                lastState = state;
            }
            br.flush();

        }
        //Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().disableHtmlEscaping().create();
        //Writer writer = Files2.writer(RECORD_DIR + name + ".gsr.json");

        //gson.toJson(this, writer);
        //writer.flush();
        //writer.close();
    }


    @Override
    public int hashCode() {
        int hashcode = 43657;
        final int nameHash = name.hashCode();
        final int idHash = id;
        final int statesHash = states.hashCode();

        hashcode = 31 * hashcode + nameHash;
        hashcode = 31 * hashcode + idHash;
        hashcode = 31 * hashcode + statesHash;

        return hashcode;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GamepadRecord)) {
            return false;
        }

        GamepadRecord gamepadRecord = (GamepadRecord) other;

        int numOfPasses = 0;
        numOfPasses += name.equals(gamepadRecord.name) ? 1 : 0;
        numOfPasses += id == gamepadRecord.id ? 1 : 0;
        numOfPasses += states.equals(gamepadRecord.states) ? 1 : 0;

        return numOfPasses == 3;
    }


}
