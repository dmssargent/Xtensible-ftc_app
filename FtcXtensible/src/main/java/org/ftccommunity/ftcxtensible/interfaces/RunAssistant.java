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
package org.ftccommunity.ftcxtensible.interfaces;


import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.util.LinkedList;

/**
 * A loop fragment, this gets put into a Loop Manager
 *
 * @author David Sargent
 * @see org.ftccommunity.ftcxtensible.robot.ExtensibleLoopManager
 * @since 0.1.0
 */
public interface RunAssistant {
    /**
     * Gets called when this run assistant conditions are meet in the Loop Manager
     *
     * @param ctx Robot Context
     * @param out a Linked List to provide a means to return specific values
     * @throws Exception
     */
    void onExecute(RobotContext ctx, LinkedList<Object> out) throws Exception;
}
