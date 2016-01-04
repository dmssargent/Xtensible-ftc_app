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
package org.ftccommunity.ftcxtensible.abstraction.services;

import org.ftccommunity.ftcxtensible.robot.RobotContext;

/**
 * A {@link com.google.common.util.concurrent.Service} who needs to have a {@link} RobotContext at somepoint during execution for the service's
 * primary goal. The service needs to be able to function correctly until a {@code RobotContext} has
 * been binded to the service
 *
 * @author David Sargent
 * @since 0.3.2
 */
public interface LatebindingContextService {
    /**
     * This method is automatically called whenever the first {@link RobotContext} has been created by
     * an {@link org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode}
     *
     * @param ctx the Robot Context
     */
    void bindToContext(RobotContext ctx);
}
