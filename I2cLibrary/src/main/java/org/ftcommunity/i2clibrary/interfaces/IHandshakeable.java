package org.ftcommunity.i2clibrary.interfaces;

import org.ftcommunity.i2clibrary.HandshakeThreadStarter;

/**
 * Interface for use with HandshakeThreadStarter
 */
public interface IHandshakeable {
    void run(HandshakeThreadStarter starter);
}
