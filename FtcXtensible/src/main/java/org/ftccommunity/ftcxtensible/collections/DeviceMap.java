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
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeviceMap<T extends HardwareDevice> extends ForwardingMap<String, T> implements Iterable<T> {
    private final TypeToken<T> type = new TypeToken<T>(getClass()) {
    };
    private final ImmutableMap<String, T> delegate;

    public DeviceMap(HardwareMap.DeviceMapping<T> deviceMapping) {
        super();
        delegate = buildFromDeviceMapping(deviceMapping);
    }

    public DeviceMap(Map<String, T> map) {
        delegate = ImmutableMap.copyOf(map);
    }

    public ImmutableMap<String, T> buildFromDeviceMapping(HardwareMap.DeviceMapping<T> deviceMapping) {
        ImmutableMap.Builder<String, T> mapBuilder = ImmutableMap.builder();
        Set<Map.Entry<String, T>> entries = deviceMapping.entrySet();
        for (Map.Entry<String, T> device : entries) {
            mapBuilder.put(device.getKey(), device.getValue());
        }

        return mapBuilder.build();
    }

    @Override
    protected Map<String, T> delegate() {
        return delegate;
    }

    @Override
    public T remove(@NotNull Object value) {
        throw new UnsupportedOperationException("Attempted to use remove()");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Attempted to clear the map");
    }

    @Override
    public T get(Object key) {
        T o = super.get(checkNotNull(key, "Parameter key is null"));
        if (o == null) {
            throw new IllegalArgumentException("Cannot find device for " + key);
        }

        return o;
    }

    @Override
    public T put(@NotNull String key, @NotNull T object) {
        throw new AssertionError("Attempted to put \"" + key +
                "\" into a DeviceMap");
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends T> map) {
        throw new UnsupportedOperationException("Attempted to use putAll(map)");
    }

    /**
     * Returns an {@link Iterator} for the elements in this object.
     *
     * @return An {@code Iterator} instance.
     */
    @Override
    public Iterator<T> iterator() {
        return delegate.values().iterator();
    }
}
