package org.ftccommunity.ftcxtensible.opmodes.internal;

import android.support.annotation.CallSuper;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.ftccommunity.ftcxtensible.AnnotationFtcRegister;


public class AttachedOpMode extends OpMode {
    @Override
    @CallSuper
    public void init() {
        AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER.attach(this);
    }

    @Override
    public void loop() {

    }

    @CallSuper
    @Override
    public void stop() {
        super.stop();
        AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER.detach();
    }
}
