/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import org.ftc.opmodes.FallbackOpModeRegister;
import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.AnnotationFtcRegister;

/**
 *
 * @author David Sargent - FTC5395, maths222, Qualcomm
 * @since 1.0
 */
public class FtcOpModeRegister implements OpModeRegister {
    private static String TAG = "FTC_OP_MODE_REGISTER::";

    /**
     * The Op Mode Manager will call this method when it wants stopMode list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     * @param mgr op mode manager
     *
     * @see FallbackOpModeRegister
     * @see org.ftccommunity.ftcxtensible.opmodes.TeleOp
     * @see org.ftccommunity.ftcxtensible.opmodes.Autonomous
     * @see org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion
     */
    public void register(final OpModeManager mgr) {
        DataBinder binding = DataBinder.getInstance();

        binding.integers().put(DataBinder.RC_VIEW, R.id.RelativeLayout);
        binding.objects().put(DataBinder.RC_MANAGER, mgr);

        FallbackOpModeRegister.register(mgr);
        AnnotationFtcRegister.loadOpModes(mgr);
    }
}
