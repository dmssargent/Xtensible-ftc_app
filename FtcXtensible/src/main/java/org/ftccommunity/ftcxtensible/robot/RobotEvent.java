package org.ftccommunity.ftcxtensible.robot;

import org.ftccommunity.ftcxtensible.interfaces.RobotAction;

public class RobotEvent implements Runnable {
    public final Object object;
    public final Object valueToExecuteOn;
    public final RobotAction action;
    private EventCheck check;
    private volatile boolean isExecuting;

    public RobotEvent(Object object, Object valueToExecuteOn, RobotAction action) {
        this.object = object;
        this.valueToExecuteOn = valueToExecuteOn;
        this.action = action;

        if (object instanceof Integer || object instanceof Double || object instanceof Long || object instanceof Short) {
            check = DefaultEventChecks.NUMERIC;
        } else {
            check = DefaultEventChecks.OBJECT;
        }
    }

    public RobotEvent registerCheck(EventCheck check) {
        this.check = check;
        return this;
    }

    public boolean check() {
        return isCheckRegistered() && check.check(object, valueToExecuteOn);
    }

    public boolean isCheckRegistered() {
        return check != null;
    }

    public synchronized void execute() {
        isExecuting = true;
        action.perform();
        isExecuting = false;
    }

    @Override
    public void run() {
        if (check()) execute();
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    private enum DefaultEventChecks implements EventCheck {
        NUMERIC {
            @Override
            public boolean check(Object obj, Object goalValue) {
                return obj == goalValue;
            }
        }, OBJECT {
            @Override
            public boolean check(Object obj, Object goalValue) {
                return goalValue.equals(obj);
            }
        }
    }

    public interface EventCheck {
        boolean check(Object obj, Object goalValue);
    }
}
