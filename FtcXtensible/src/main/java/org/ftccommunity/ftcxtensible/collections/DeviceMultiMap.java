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

package org.ftccommunity.ftcxtensible.collections;
import com.google.common.collect.ForwardingMap;

import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceMultiMap extends ForwardingMap<Class<? extends HardwareDevice>, DeviceMap<? extends HardwareDevice>> implements Iterable<DeviceMap<? extends HardwareDevice>> {
    private final HashMap<Class<? extends HardwareDevice>, DeviceMap<? extends HardwareDevice>> delegate;

    public DeviceMultiMap() {
        this.delegate = new HashMap<>();
    }

    public <T extends HardwareDevice> DeviceMap<T> checkedGet(@NotNull Class<T> type) {
        if (super.containsKey(type)) {
            throw new IllegalArgumentException("Map doesn't contain " + type.getSimpleName());
        }

        return (DeviceMap<T>) delegate.get(type);
    }

    public <T extends HardwareDevice> DeviceMap<T> checkedPut(@NotNull Class<T> kClass, @NotNull DeviceMap<T> value) {
        if (super.containsKey(kClass)) {
            throw new IllegalArgumentException("Map already contains a reference for " + kClass.getSimpleName());
        }

        return (DeviceMap<T>) delegate.put(kClass, value);
    }

    @Override
    protected Map<Class<? extends HardwareDevice>, DeviceMap<? extends HardwareDevice>> delegate() {
        return delegate;
    }

    @Override
    public DeviceMap<? extends HardwareDevice> get(@Nullable Object object) {
        throw new UnsupportedOperationException("Attempted to use get(); use checkedGet()");
    }

    @Override
    public DeviceMap<? extends HardwareDevice> put(@NotNull Class kClass, @NotNull DeviceMap value) {
        throw new UnsupportedOperationException("Attempted to use put(); use checkedPut()");
    }

    /**
     * Returns an {@link Iterator} for the elements in this object.
     *
     * @return An {@code Iterator} instance.
     */
    @Override
    public Iterator<DeviceMap<? extends HardwareDevice>> iterator() {
        return delegate.values().iterator();
    }
}
