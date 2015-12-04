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

package org.ftccommunity.ftcxtensible.versioning;


import java.util.Comparator;

public enum RobotSdkApiLevel implements Comparator<RobotSdkApiLevel> {
    A1_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
            /*if (lhs.equals(rhs)) {
                return 0;
            }

            switch (lhs) {
                case A1_2015:
                    return 0;
                case R1_2015:
                    return 1;
                case R2_2015:
                    return 1;
                case R3_2015:
                    return 1;
                default:
                    Log.w("ROBOT_SDK::", "Comparison does not have a handled result");
                    return 1;
            }*/
        }
    }, R1_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
            /*if (lhs.equals(rhs)) {
                return 0;
            }

            switch (lhs) {
                case A1_2015:
                    return -1;
                case R1_2015:
                    return 0;
                case R2_2015:
                    return 1;
                case R3_2015:
                    return 1;
                default:
                    Log.w("ROBOT_SDK::", "Comparison does not have a handled result");
                    return 1;
            }*/
        }
    }, R2_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
            /*if (lhs.equals(rhs)) {
                return 0;
            }

            switch (lhs) {
                case A1_2015:
                    return -1;
                case R1_2015:
                    return -1;
                case R2_2015:
                    return 0;
                case R3_2015:
                    return 1;
                default:
                    Log.w("ROBOT_SDK::", "Comparison does not have a handled result");
                    return 1;
            }*/
        }
    }, R3_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
            /*if (lhs.equals(rhs)) {
                return 0;
            }

            switch (lhs) {
                case A1_2015:
                    return -1;
                case R1_2015:
                    return -1;
                case R2_2015:
                    return -1;
                case R3_2015:
                    return 0;
                default:
                    Log.w("ROBOT_SDK::", "Comparison does not have a handled result");
                    return 1;
            }*/
        }
    }, R4_2015_VER2_10 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    R5_2015_VER2_25 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    };

    public static RobotSdkApiLevel currentVerison() {
        return R3_2015;
    }

    int comparator(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
        return compare(lhs, rhs);
    }

    public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
        int leftHandSide = lhs.ordinal();
        int rightHandSide = rhs.ordinal();

        if (leftHandSide > rightHandSide) {
            return -1;
        } else if (leftHandSide == rightHandSide) {
            return 0;
        } else {
            return 1;
        }
    }
}
