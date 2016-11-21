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

import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;

@NotDocumentedWell
public interface FullOpMode extends RobotInitStartStopLoop {
    void init(final RobotContext ctx,
              final LinkedList<Object> out) throws Exception;

    void init_loop(final RobotContext ctx,
                   final LinkedList<Object> out) throws Exception;

    void start(final RobotContext ctx,
               final LinkedList<Object> out) throws Exception;

    void loop(final RobotContext ctx,
              final LinkedList<Object> out) throws Exception;

    void stop(final RobotContext ctx, LinkedList<Object> out) throws Exception;

    void onSuccess(final RobotContext ctx, Object event, Object in);

    int onFailure(final RobotContext ctx,
                  RobotStatus.Type eventType, Object event, Object in);
}
