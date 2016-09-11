package org.ftccommunity.ftcxtensible;

import org.ftccommunity.ftcxtensible.interfaces.RobotInitStartStopLoop;
import org.ftccommunity.ftcxtensible.opmodes.internal.FtcOpModeStopInitInterfaceRunner;

public class FtcOpModeInitStartStopInterfaceRunner<T extends RobotInitStartStopLoop> extends FtcOpModeStopInitInterfaceRunner<T> {
    public FtcOpModeInitStartStopInterfaceRunner(Class<T> loop) {
        super(loop);
    }

    @Override
    public void start() {
        delegate().start();
    }
}
