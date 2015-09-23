import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

/**
 * @author David Sargent - FTC5395, maths222, Qualcomm
 * @since 1.0
 */
public class FallbackOpModeRegister implements OpModeRegister {
    private static String TAG = "FTC_OP_MODE_REGISTER::";

    /**
     * The Op Mode Manager will call this method when it wants stopMode list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     * @param mgr op mode manager
     */
    @Override
    public void register(final OpModeManager mgr) {
        // Add your hardcoded OpModes here

        // Please note that this executes immediately right now
    }
}
