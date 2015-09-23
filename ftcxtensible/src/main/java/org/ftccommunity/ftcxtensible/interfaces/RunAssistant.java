package org.ftccommunity.ftcxtensible.interfaces;


import org.ftccommunity.ftcxtensible.RobotContext;

import java.util.LinkedList;

public interface RunAssistant {
    void onExecute(RobotContext ctx, LinkedList<Object> out) throws Exception;
}
