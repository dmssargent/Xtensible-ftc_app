package org.ftccommunity.ftcxtensible.opmodes.internal;

import android.support.annotation.CallSuper;

import org.ftccommunity.ftcxtensible.AnnotationFtcRegister;
import org.ftccommunity.ftcxtensible.dagger.SimpleDag;
import org.ftccommunity.ftcxtensible.interfaces.RobotLoop;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Created by David on 9/11/2016.
 */
public class FtcOpModeInterfaceRunner<T extends RobotLoop> extends AttachedOpMode {
    private final Class<T> loop;
    private T currentInstance;


    public FtcOpModeInterfaceRunner(Class<T> loop) {
        this.loop = loop;

    }

    @Override
    @CallSuper
    public void init() {
        super.init();
        recreateDelegate();
    }

    @Override
    @CallSuper
    public void loop() {
        super.loop();
        currentInstance.loop();
    }

    @Override
    @CallSuper
    public void stop() {
        currentInstance = null;
        super.stop();
    }

    @Contract(pure = true)
    @NotNull
    protected T delegate() {
        if (currentInstance == null) {
            recreateDelegate();
        }

        return currentInstance;

    }

    private void recreateDelegate() {
        if (currentInstance == null) {
            currentInstance = SimpleDag.create(loop, AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER);
        }
    }
}
