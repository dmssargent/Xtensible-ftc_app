package org.ftccommunity.ftcxtensible.opmodes.internal;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.core.Attachable;
import org.ftccommunity.ftcxtensible.dagger.annonations.Named;
import org.ftccommunity.ftcxtensible.dagger.annonations.Provides;
import org.ftccommunity.ftcxtensible.dagger.annonations.VariableNamedProvider;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkState;

public class OpModeRunnerProvider implements Attachable<AttachedOpMode> {
    private AttachedOpMode coreProvider = null;
    private ExtensibleHardwareMap extensibleHardwareMap = null;

    @Override
    @Nullable
    public AttachedOpMode attach(@NotNull AttachedOpMode provider) {
        AttachedOpMode temp = detach();
        coreProvider = provider;
        extensibleHardwareMap = new ExtensibleHardwareMap(coreProvider.hardwareMap);
        return temp;
    }

    @Override
    @Nullable
    public AttachedOpMode detach() {
        AttachedOpMode temp = coreProvider;
        coreProvider = null;
        extensibleHardwareMap = null;
        return temp;
    }

    @Override
    @Contract(pure = true)
    public boolean isAttached() {
        return coreProvider != null;
    }

    @Provides
    public Telemetry providesTelemetry() {
        checkState(isAttached(), "provider is not attached");
        return coreProvider.telemetry;
    }

    @Provides
    public Gamepad providesGamepad() {
        checkState(isAttached(), "provider is not attached");
        return coreProvider.gamepad1;
    }

    @Provides
    @Named("gamepad2")
    public Gamepad providesGamepad2() {
        checkState(isAttached(), "provider is not attached");
        return coreProvider.gamepad2;
    }

    @Provides
    @Named("gamepad1")
    public Gamepad providesGamepad1() {
        checkState(isAttached(), "provider is not attached");
        return coreProvider.gamepad1;
    }

    @Provides
    public HardwareMap providesHardwareMap() {
        checkState(isAttached(), "provider is not attached");
        return coreProvider.hardwareMap;
    }

    @Provides
    @VariableNamedProvider
    public HardwareDevice providesHardwareDevices(String name) {
        checkState(isAttached(), "provider is not attached");
        return extensibleHardwareMap.get(name);
    }
}
