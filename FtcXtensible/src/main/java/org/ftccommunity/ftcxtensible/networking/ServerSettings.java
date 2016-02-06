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
package org.ftccommunity.ftcxtensible.networking;

/**
 * Server Settings manager, this handles the control of the HTTP web server, and maintains all of
 * the critical information reguarding the server
 *
 * @author David Sargent
 * @since 0.1.0
 */
public class ServerSettings {
    private String webDirectory;
    private String index;
    private String hardwareMapJsonPage;
    private String logPage;

    private ServerSettings() {
        setWebDirectory("/sdcard/FIRST/web");
        setIndex("/index.html");
        setHardwareMapJsonPage("/robot.json");
        setLogPage("/robot-log.html");
    }

    /**
     * Factory method to create the server settings object
     *
     * @return a new Server Settings object, set to the default
     */
    public static ServerSettings createServerSettings() {
        return new ServerSettings();
    }

    /**
     * Returns the HTML root for the web portion
     *
     * @return HTML root
     */
    public String getWebDirectory() {
        return webDirectory;
    }

    /**
     * Sets the HTML root
     *
     * @param webDirectory full path to the web directory, this path must exist
     */
    public void setWebDirectory(String webDirectory) {
        this.webDirectory = webDirectory;
    }

    /**
     * Gets the name of the main page to the server, relative to HTML root
     *
     * @return name of index page
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the index page name; must be relative to HMTL root
     *
     * @param index index page name, relative to HTML root
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets the name of the JSON page representing the Hardware Map
     *
     * @return page name for a JSON serialized HardwareMap
     * @see org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap
     */
    public String getHardwareMapJsonPage() {
        return hardwareMapJsonPage;
    }

    /**
     * Sets the name of the JSON Hardware Map page. This value will not read any file from storage,
     * but instead provide a Hardware at this name
     *
     * @param hardwareMapJsonPage name of the hardware map
     */
    public void setHardwareMapJsonPage(String hardwareMapJsonPage) {
        this.hardwareMapJsonPage = hardwareMapJsonPage;
    }

    /**
     * Gets the name of the logging page, under the same conditions as the HardwareMap page
     *
     * @return logging page name
     */
    public String getLogPage() {
        return logPage;
    }

    /**
     * Sets the name of the logging page, under the same conditions as the HardwareMap page
     *
     * @param logPage name of the logging page
     */
    public void setLogPage(String logPage) {
        this.logPage = logPage;
    }
}