/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous;

public interface BasicLevels {
    class InitLevel implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class Level1 implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class Level2 implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class Level3 implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class Level4 implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class StopLevel implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }

    class SafeLevel implements RunLevel {
        public int execute() {
            //TODO: write code that is composes your init state
            return 0;
        }

        public void close() {
            //TODO: write your code to cleanup to prevent leakage
        }

        // This runs when you want or when a negative error code is returned from execute, or
        // when a exeception is thrown in execute, the return value specifies whether to continue
        // with the next state or not.
        public boolean safe() {
            //TODO: write  your code that runs if an error occurs
            return false;
        }

        public int safe(int errorCode) {
            //TODO: write your code that runs if a error occurs that is positive

            //Uncomment the following line to always run the default safe
            //safe();

            //Returning a non-zero number runs  standard safe
            return 0;
        }
    }
}