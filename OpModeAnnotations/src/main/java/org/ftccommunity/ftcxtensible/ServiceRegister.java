/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible;


import org.ftccommunity.ftcxtensible.services.RobotService;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages {@link RobotService} annotated classes, future loading is only guaranteed if the class also
 * implements a {@code Service} interface from Guava.
 *
 * @author David Sargent
 * @author 0.3.2
 */
public class ServiceRegister {
    private static LinkedList<Class> services;

    /**
     * The input class array, this will scan the list and remember the classes annonated with
     * {@code RobotService} annotations
     *
     * @param classes an array of classes to check
     */
    public static void init(Class... classes) {
        services = new LinkedList<>();
        for (Class klazz : classes) {
            if (klazz.isAnnotationPresent(RobotService.class)) {
                services.add(klazz);
            }
        }
    }

    /**
     * A List of remembered {@link Class}es to fetch from previous pre-loading
     *
     * @return the Classes found to be annotated with at {@link RobotService}
     */
    public static List<Class> getServices() {
        return services;
    }
}
