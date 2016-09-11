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
package org.ftccommunity.ftcxtensible.opmodes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code Autonomous} annotation is used to specify that an op mode should be registered for
 * display on the driver station.
 *
 * @author David Sargent, maths222
 * @see TeleOp
 * @since 0.1.0
 * @deprecated see {@link com.qualcomm.robotcore.eventloop.opmode.Autonomous}
 * @see com.qualcomm.robotcore.eventloop.opmode.Autonomous
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface Autonomous {
    /**
     * The name of the op mode. This is displayed on the driver station. If empty, the name of the
     * op mode class will be used.
     */
    String name() default "";

    String pairWithTeleOp() default "";
}