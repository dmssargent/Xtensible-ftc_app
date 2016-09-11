package org.ftccommunity.ftcxtensible.opmodes.internal;

import org.ftccommunity.ftcxtensible.interfaces.RobotInitStopLoop;

public class FtcOpModeStopInitInterfaceRunner<T extends RobotInitStopLoop> extends FtcOpModeInterfaceRunner<T> {
    public FtcOpModeStopInitInterfaceRunner(Class<T> loop) {
        super(loop);
    }

    @Override
    public void init() {
        super.init();
        delegate().init();
    }

    @Override
    public void stop() {
        delegate().stop();
        super.stop();
    }
}
