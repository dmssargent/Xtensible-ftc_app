package org.ftccommunity.ftcxtensible.collections;

import com.google.common.reflect.TypeToken;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by David on 12/12/2015.
 */
public class DeviceMap<K, T extends HardwareDevice> extends HashMap<String, T> {
    private TypeToken<T> type = new TypeToken<T>(getClass()) {};
    private boolean mock;


    private DeviceMap(HardwareMap.DeviceMapping<T> deviceMapping) {
        super(deviceMapping.size());
        //buildFromDeviceMapping(deviceMapping);
    }

    public void enableMocking() {
        throw new IllegalArgumentException("Stub");

        /*if (supportsMocking(type.getRawType())) {
            mock = true;
        } else {
            throw new IllegalArgumentException("Object type does not support mocking");
        }*/
    }

   /* public void disableMocking() {
        mock = false;
    }*/

    public static <T extends HardwareDevice> DeviceMap<String, T> buildFromDeviceMapping(HardwareMap.DeviceMapping<T> deviceMapping) {
        DeviceMap<String, T> map = new DeviceMap<>(deviceMapping);
        Set<Entry<String, T>> entries = deviceMapping.entrySet();
        for (Entry<String, T> device : entries) {
            map.put(device.getKey(), device.getValue());
        }
        return map;
    }

    @Override
    @Nullable
    public T get(Object key) {
        T o = super.get(key);
        if (o == null) {
            throw new IllegalArgumentException("Cannot find device for " + key);
        }

        // todo
       /* if (o == null && mock) {
            try {
                T value = (T) type.getRawType().getConstructors()[0].newInstance("new");
                put((String) key, value);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new IllegalStateException("Need to create a stub object, but" +
                        "failed to do so: " + ex.toString());
            }
        }*/

        return o;
    }
}
