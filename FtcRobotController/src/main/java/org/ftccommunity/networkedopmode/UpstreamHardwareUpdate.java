package org.ftccommunity.networkedopmode;

import java.util.HashMap;

/**
 * @param <T> Type of device to use for an upstream call
 */
public interface UpstreamHardwareUpdate<T> {
    void processRead(T in, HashMap<String, Object> out);
}
