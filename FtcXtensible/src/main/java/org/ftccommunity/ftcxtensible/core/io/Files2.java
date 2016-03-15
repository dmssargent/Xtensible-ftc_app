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

package org.ftccommunity.ftcxtensible.core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeIOException;

/**
 * Created by David on 1/24/2016.
 */
public final class Files2 {
    public static boolean mkdirs(String name) {
        File folder = new File(name);
        return folder.isDirectory() || folder.mkdirs();
    }

    public static boolean mkdir(String name) {
        File folder = new File(name);
        return folder.isDirectory() || folder.mkdir();
    }

    public static Reader reader(File file) {
        if (!file.exists()) {
            throw new RuntimeIOException("The file at \"" + file.getPath() + "\" cannot be found");
        }

        if (file.isDirectory()) {
            throw new RuntimeIOException("The file at \"" + file.getPath() + "\" is a directory, a reader cannot be provided for such");
        }

        if (!file.canRead()) {
            throw new RuntimeIOException("The file at \"" + file.getPath() + "\" cannot be read");
        }

        try {
            return new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public static Reader reader(String name) {
        return reader(new File(name));
    }

    public static Writer writer(File file) {
        if (file.isDirectory()) {
            throw new RuntimeIOException("The file at \"" + file.getPath() + "\" is a directory, a reader cannot be provided for such");
        }

        if (file.exists() && !file.canWrite()) {
            throw new RuntimeIOException("The file at \"" + file.getPath() + "\" cannot be written");
        }

        try {
            return new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public static Writer writer(String name) {
        return writer(new File(name));
    }
}
