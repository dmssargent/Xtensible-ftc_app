/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ftccommunity.ftcxtensible.robot;

import com.google.common.collect.ImmutableMap;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.ftccommunity.ftcxtensible.collections.DeviceMap;
import org.ftccommunity.ftcxtensible.collections.DeviceMultiMap;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An unmodifiable version of a {@link ExtensibleHardwareMap}. To get this most functionality
 * out of this, recreate the the {@code ExtensibleHardwareMap} using the special
 * {@link ExtensibleHardwareMap(ImmutableHardwareMap)} constructor.
 *
 * To access this map, use {@link #getUnderlyingMap()}
 *
 * @author David Sargent
 * @since 0.1.0
 * @see ImmutableMap
 */
public class ImmutableHardwareMap {
    private final ImmutableMap<Class<? extends HardwareDevice>,
            DeviceMap<? extends HardwareDevice>> deviceMap;

    /**
     * Creates an {@link ImmutableMap} based off of a {@link ExtensibleHardwareMap}
     * @param hardwareMap a {@code ExtensibleHardwareMap} to create a {@code ImmutableMap off of}
     */
    public ImmutableHardwareMap(@NotNull ExtensibleHardwareMap hardwareMap) {
        DeviceMultiMap map = checkNotNull(hardwareMap).delegate();
        deviceMap = ImmutableMap.copyOf(map);
    }

    /**
     * Gets the {@link ImmutableMap} based off of a {@link ExtensibleHardwareMap}
     *
     * @return the {@code ImmutableMap} for the {@code ExtensibleHardwareMap}
     */
    @NotNull
    public ImmutableMap<Class<? extends HardwareDevice>,
                DeviceMap<? extends HardwareDevice>> getUnderlyingMap() {
        return deviceMap;
    }
}
