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

package org.ftccommunity.ftcxtensible.core.charsets;

import android.util.Base64;

import java.nio.charset.Charset;

/**
 * Created by David on 1/24/2016.
 */
public class Charsets2 {
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static final Charset ascii = Charset.forName("ASCII");

    public Charset defaultCharset() {
        return utf8();
    }

    public Charset utf8() {
        return utf8;
    }

    public Charset ascii() {
        return ascii;
    }

    public String base64encode(String data) {
        return Base64.encodeToString(data.getBytes(defaultCharset()), Base64.DEFAULT);
    }

    public byte[] base64EncodeToByteArray(byte[] bytes) {
        return Base64.encode(bytes, Base64.DEFAULT);
    }

    public byte[] base64decode(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }
}
