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
