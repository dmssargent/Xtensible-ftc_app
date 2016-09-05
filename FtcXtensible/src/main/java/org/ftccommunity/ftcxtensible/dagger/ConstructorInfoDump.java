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

package org.ftccommunity.ftcxtensible.dagger;

import java.lang.annotation.Annotation;

/**
 * Created by David on 6/1/2016.
 */
class ConstructorInfoDump {
    public ParameterInfo[] parameterInfos;

    ConstructorInfoDump(Class[] parameters, Annotation[][] annotations) {
        parameterInfos = new ParameterInfo[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            ParameterInfo info = new ParameterInfo();
            info.className = parameters[i].getSimpleName();
            info.annotations = new String[annotations[i].length];
            for (int j = 0; j < annotations[i].length; j++) {
                info.annotations[j] = annotations[i][j].annotationType().getSimpleName();
            }
            parameterInfos[i] = info;
        }
    }

    private class ParameterInfo {
        String className;
        String[] annotations;
    }
}
