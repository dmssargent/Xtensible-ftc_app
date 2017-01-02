package org.ftccommunity.ftcxtensible.opmodes.internal;

import org.ftccommunity.ftcxtensible.interfaces.RobotFullOp;

public class FtcOpModeFullOpModeRunner<T extends RobotFullOp> extends FtcOpModeStopInitInterfaceRunner<T> {
    public FtcOpModeFullOpModeRunner(Class<T> loop) {
        super(loop);
    }

    public void init_loop() {
        delegate().init_loop();
    }
}
