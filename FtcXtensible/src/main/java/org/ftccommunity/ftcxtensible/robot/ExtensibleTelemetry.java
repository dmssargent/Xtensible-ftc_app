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


import com.google.common.base.Strings;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;

@Alpha
@NotDocumentedWell
public class ExtensibleTelemetry implements Telemetry {
    @Deprecated
    public static final int DEFAULT_DATA_MAX = 192;
    @Deprecated
    public static final int MAX_DATA_MAX = 255;

    private final Telemetry delegate;

    ExtensibleTelemetry(@NotNull Telemetry telemetry) {
        this.delegate = telemetry;
    }

    @Deprecated
    private ExtensibleTelemetry(int dataPointsToSend, @NotNull Telemetry telemetry) {
        this(telemetry);

        //this.dataPointsToSend = dataPointsToSend;
//        cache = CacheBuilder.newBuilder().
//                concurrencyLevel(4).
//                expireAfterAccess(250, TimeUnit.MILLISECONDS).
//                maximumSize(dataPointsToSend).build();
//
//        dataCache = new SynchronousArrayQueue<>(50);
//        data = LinkedHashMultimap.create();
//        log = new LinkedList<>();
    }

    public Item data(String tag, String message) {
        checkArgument(!Strings.isNullOrEmpty(message), "Your message shouldn't be empty.");
        tag = Strings.nullToEmpty(tag);
        return delegate.addData(tag, message);
//        lastModificationTime = System.nanoTime();
//        //parent.addData(tag, message);
//        dataCache.add(new TelemetryItem(tag.equals(EMPTY) ? EMPTY : tag.toUpperCase(Locale.US) + SPACE, message));
    }

    public Item addPersistentData(String tag, String mess) {
        return delegate.addData(tag, mess).setRetained(true);
    }

    public Item data(String tag, double message) {
        return data(tag, Double.toString(message));
    }

    public Item data(String tag, Object object) {
        return data(tag, object.toString());
    }

    @Deprecated
    synchronized void close() {
//        executorService.shutdown();
//        reader.close();
//        logcat.destroy();
//        synchronized (delegate) {
//            delegate.clearAll();
//        }
//        synchronized (log) {
//            log.clear();
//        }
//
//        //synchronized (dataCache) {
//        //dataCache.clear();
//        //}
//
//        synchronized (data) {
//            data.clear();
//        }
//        synchronized (cache) {
//            cache.invalidateAll();
//        }
        delegate.clear();
    }

// --Commented out by Inspection START (11/5/2016 9:08 PM):
//    @Deprecated
//    private void updateCache() {
//        forceUpdateCache();
//    }
// --Commented out by Inspection STOP (11/5/2016 9:08 PM)

// --Commented out by Inspection START (11/5/2016 9:12 PM):
//    private synchronized void forceUpdateCache() {
////        int numberOfElements;
////        synchronized (cache) {
////            cache.invalidateAll();
////
////            //synchronized (dataCache) {
////                int numberOfElementsAdded = 0;
////            int min = Math.min(dataCache.length(), (int) (dataPointsToSend * .75));
////                int stringLength = String.valueOf(min).length();
////                for (; numberOfElementsAdded < min; numberOfElementsAdded++) {
////                    final TelemetryItem remove = dataCache.remove();
////                    cache.put(cancelOut(stringLength, String.valueOf(numberOfElementsAdded)), remove.getTag() + remove.getMessage());
////                }
////
////                numberOfElements = numberOfElementsAdded;
////            //}
////
////            synchronized (data) {
////                numberOfElementsAdded = 0;
////                HashMap<String, String> entries = new HashMap<>();
////
////                LinkedList<Multiset.Entry<String>> keys = new LinkedList<>(data.keys().entrySet());
////                for (Multiset.Entry<String> key : keys) {
////                    LinkedList<String> dataElements = new LinkedList<>(data.get(key.getElement()));
////
////                    int size = dataElements.size();
////
////                    for (int index = 0; numberOfElementsAdded < size; numberOfElementsAdded++) {
////                        entries.put(cancelOut(1, key.getElement() + Integer.toString(index)), dataElements.get(index));
////                    }
////                }
////
////                try {
////                    LinkedList<Map.Entry<String, String>> entriesToSend = new LinkedList<>(entries.entrySet());
////                    for (; numberOfElementsAdded < Math.min(entriesToSend.size(), dataPointsToSend - numberOfElements);
////                         numberOfElementsAdded++) {
////                        Map.Entry<String, String> entry = entriesToSend.get(numberOfElementsAdded - 1);
////                        cache.put(entry.getKey(), entry.getValue());
////                    }
////                } catch (IndexOutOfBoundsException ex) {
////                    RobotLog.e(ex.getMessage(), ex);
////                }
////            }
////        }
//        delegate.update();
//    }
// --Commented out by Inspection STOP (11/5/2016 9:12 PM)

    @Deprecated
    synchronized void sendData() {
//        updateCache();
//
//        LinkedList<Map.Entry<String, String>> data;
//        synchronized (cache) {
//            data = new LinkedList<>(cache.asMap().entrySet());
//        }
//        for (Map.Entry<String, String> entry : data) {
//            delegate.addData(entry.getKey(), entry.getValue());
//        }
//
//        synchronized (log) {
//            if (log.size() < dataPointsToSend) {
//                int numberOfElementsAdded = 0;
//                int min = Math.min(dataPointsToSend - log.size(), log.size());
//                for (; numberOfElementsAdded < min; numberOfElementsAdded++) {
//                    delegate.addData("xLog" + String.valueOf(numberOfElementsAdded), log.poll());
//                }
//            }
//        }
        delegate.update();
    }

// --Commented out by Inspection START (11/5/2016 9:08 PM):
//    /**
//     * Pads the end of a string with enough {@code '\SimulatedUsbDevice'} characters to cancel out the original string, if
//     * it is every printed
//     * @param length the length to pad out
//     * @param string the string to cancel out
//     * @return the string padded with {@code '\SimulatedUsbDevice'} characters
//     */
//    @NotNull
//    private String cancelOut(int length, @NotNull String string) {
//        return Strings.padStart(checkNotNull(string), length, '\SimulatedUsbDevice');
//    }
// --Commented out by Inspection STOP (11/5/2016 9:08 PM)

    /**
     * Adds an item to the end of the telemetry being built for driver station display. The value shown
     * will be the result of calling {@link String#format(Locale, String, Object...) String.format()}
     * with the indicated format and arguments. The caption and value are shown on the driver station
     * separated by the {@link #getCaptionValueSeparator() caption value separator}. The item
     * is removed if {@link #clear()} or {@link #clearAll()} is called.
     *
     * @param caption the caption to use
     * @param format  the string by which the arguments are to be formatted
     * @param args    the arguments to format
     * @return an {@link Item} that can be used to update the value or append further {@link Item}s
     * @see #addData(String, Object)
     * @see #addData(String, Func)
     */
    @Override
    public Item addData(String caption, String format, Object... args) {
        return delegate.addData(caption, format, args);
    }

    /**
     * Adds an item to the end if the telemetry being built for driver station display. The value shown
     * will be the result of calling {@link Object#toString() toString()} on the provided value
     * object. The caption and value are shown on the driver station separated by the {@link
     * #getCaptionValueSeparator() caption value separator}. The item is removed if {@link #clear()}
     * or {@link #clearAll()} is called.
     *
     * @param caption the caption to use
     * @param value   the value to display
     * @return an {@link Item} that can be used to update the value or append further {@link Item}s
     * @see #addData(String, String, Object...)
     * @see #addData(String, Func)
     */
    @Override
    public Item addData(String caption, Object value) {
        return delegate.addData(caption, value);
    }

    /**
     * Adds an item to the end of the telemetry being built for driver station display. The value shown
     * will be the result of calling {@link Object#toString() toString()} on the object which is
     * returned from invoking valueProducer.{@link Func#value()} value()}. The caption and value are
     * shown on the driver station separated by the {@link #getCaptionValueSeparator() caption value
     * separator}. The item is removed if {@link #clearAll()} is called, but <em>not</em> if
     * {@link #clear()} is called.
     * <p>
     * <p>The valueProducer is evaluated only if actual transmission to the driver station
     * is to occur. This is important, as it provides a means of displaying telemetry which
     * is relatively expensive to evaluate while avoiding computation or delay on evaluations
     * which won't be transmitted due to transmission interval throttling.</p>
     *
     * @param caption       the caption to use
     * @param valueProducer the object which will provide the value to display
     * @return an {@link Item} that can be used to update the value or append further {@link Item}s
     * @see #addData(String, String, Object...)
     * @see #addData(String, Object)
     * @see #addData(String, String, Func)
     * @see #getMsTransmissionInterval()
     */
    @Override
    public <T> Item addData(String caption, Func<T> valueProducer) {
        return delegate.addData(caption, valueProducer);
    }

    /**
     * Adds an item to the end of the telemetry being built for driver station display. The value shown
     * will be the result of calling {@link String#format} on the object which is returned from invoking
     * valueProducer.{@link Func#value()} value()}. The caption and value are shown on the driver station
     * separated by the {@link #getCaptionValueSeparator() caption value separator}. The item is removed
     * if {@link #clearAll()} is called, but <em>not</em> if {@link #clear()} is called.
     * <p>
     * <p>The valueProducer is evaluated only if actual transmission to the driver station
     * is to occur. This is important, as it provides a means of displaying telemetry which
     * is relatively expensive to evaluate while avoiding computation or delay on evaluations
     * which won't be transmitted due to transmission interval throttling.</p>
     *
     * @param caption       the caption to use
     * @param format a string format
     * @param valueProducer the object which will provide the value to display  @return                  an {@link Item} that can be used to update the value or append further {@link Item}s
     * @see #addData(String, String, Object...)
     * @see #addData(String, Object)
     * @see #addData(String, Func)
     * @see #getMsTransmissionInterval()
     * @see String#format(String, Object...)
     */
    @Override
    public <T> Item addData(String caption, String format, Func<T> valueProducer) {
        return delegate.addData(caption, format, valueProducer);
    }

    /**
     * Removes an item from the receiver telemetry, if present.
     *
     * @param item the item to remove
     * @return true if any change was made to the receive (ie: the item was present); false otherwise
     */
    @Override
    public boolean removeItem(Item item) {
        return delegate.removeItem(item);
    }

    /**
     * Removes all items from the receiver whose value is not to be retained.
     *
     * @see Item#setRetained(Boolean)
     * @see Item#isRetained()
     * @see #clearAll()
     * @see #addData(String, Func)
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /**
     * Removes <em>all</em> items, lines, and actions from the receiver
     *
     * @see #clear()
     */
    @Override
    public void clearAll() {
        delegate.clearAll();
    }

    /**
     * In addition to items and lines, a telemetry may also contain a list of actions.
     * When the telemetry is to be updated, these actions are evaluated before the telemetry
     * lines are composed just prior to transmission. A typical use of such actions is to
     * initialize some state variable, parts of which are subsequently displayed in items.
     * This can help avoid needless re-evaluation.
     * <p>
     * <p>Actions are cleared with {@link #clearAll()}, and can be removed with {@link
     * #removeAction(Object) removeAction()}.</p>
     *
     * @param action the action to execute before composing the lines telemetry
     * @return a token by which the action can be later removed.
     * @see #addData(String, Object)
     * @see #removeAction(Object)
     * @see #addLine()
     * @see #update()
     */
    @Override
    public Object addAction(Runnable action) {
        return delegate.addAction(action);
    }

    /**
     * Removes a previously added action from the receiver.
     *
     * @param token the token previously returned from {@link #addAction(Runnable) addAction()}.
     * @return whether any change was made to the receiver
     */
    @Override
    public boolean removeAction(Object token) {
        return delegate.removeAction(token);
    }

    /**
     * Sends the receiver {@link Telemetry} to the driver station if more than the {@link #getMsTransmissionInterval()
     * transmission interval} has elapsed since the last transmission, or schedules the transmission
     * of the receiver should no subsequent {@link Telemetry} state be scheduled for transmission before
     * the {@link #getMsTransmissionInterval() transmission interval} expires.
     *
     * @return whether a transmission to the driver station occurred or not
     */
    @Override
    public boolean update() {
        return delegate.update();
    }

    /**
     * Creates and returns a new line in the receiver {@link Telemetry}.
     *
     * @return a new line in the receiver {@link Telemetry}
     */
    @Override
    public Line addLine() {
        return delegate.addLine();
    }

    /**
     * Creates and returns a new line in the receiver {@link Telemetry}.
     *
     * @param lineCaption the caption for the line
     * @return a new line in the receiver {@link Telemetry}
     */
    @Override
    public Line addLine(String lineCaption) {
        return delegate.addLine(lineCaption);
    }

    /**
     * Removes a line from the receiver telemetry, if present.
     *
     * @param line the line to be removed
     * @return whether any change was made to the receiver
     */
    @Override
    public boolean removeLine(Line line) {
        return delegate.removeLine(line);
    }

    /**
     * Answers whether {@link #clear()} is automatically called after each call to {@link #update()}.
     *
     * @return whether {@link #clear()} is automatically called after each call to {@link #update()}.
     * @see #setAutoClear(boolean)
     */
    @Override
    public boolean isAutoClear() {
        return delegate.isAutoClear();
    }

    /**
     * Sets whether {@link #clear()} is automatically called after each call to {@link #update()}.
     *
     * @param autoClear if true, {@link #clear()} is automatically called after each call to {@link #update()}.
     */
    @Override
    public void setAutoClear(boolean autoClear) {
        delegate.setAutoClear(autoClear);
    }

    /**
     * Returns the minimum interval between {@link Telemetry} transmissions from the robot controller
     * to the driver station
     *
     * @return the minimum interval between {@link Telemetry} transmissions from the robot controller to the diver station
     * @see #setMsTransmissionInterval(int)
     */
    @Override
    public int getMsTransmissionInterval() {
        return delegate.getMsTransmissionInterval();
    }

    /**
     * Sets the minimum interval between {@link Telemetry} transmissions from the robot controller
     * to the driver station.
     *
     * @param msTransmissionInterval the minimum interval between {@link Telemetry} transmissions
     *                               from the robot controller to the driver station
     * @see #getMsTransmissionInterval()
     */
    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {
        delegate.setMsTransmissionInterval(msTransmissionInterval);
    }

    /**
     * Returns the string which is used to separate {@link Item}s contained within a line. The default
     * separator is " | ".
     *
     * @return the string which is use to separate {@link Item}s contained within a line.
     * @see #setItemSeparator(String)
     * @see #addLine()
     */
    @Override
    public String getItemSeparator() {
        return delegate.getItemSeparator();
    }

    /**
     * Changes how strings are separated from this point on
     *
     * @param itemSeparator the string that defines how strings should be separated
     * @see #setItemSeparator(String)
     */
    @Override
    public void setItemSeparator(String itemSeparator) {
        delegate.setItemSeparator(itemSeparator);
    }

    /**
     * Returns the string which is used to separate caption from value within a {@link Telemetry}
     * {@link Item}. The default separator is " : ";
     *
     * @return the string which is used to separate caption from value within a {@link Telemetry} {@link Item}.
     */
    @Override
    public String getCaptionValueSeparator() {
        return delegate.getCaptionValueSeparator();
    }

    /**
     * Changes how captions are separated from their respective value
     *
     * @param captionValueSeparator the new string separating captions from their respective values
     * @see #getCaptionValueSeparator()
     */
    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {
        delegate.setCaptionValueSeparator(captionValueSeparator);
    }

    /**
     * Returns the log of this {@link Telemetry} to which log entries may be appended.
     *
     * @return the log of this {@link Telemetry} to which log entries may be appended.
     * @see Log#addData(String, Object)
     */
    @Override
    public Log log() {
        return delegate.log();
    }

// --Commented out by Inspection START (11/5/2016 9:08 PM):
//    @Deprecated
//    private class TelemetryItem {
//        private final String tag;
//        private final String message;
//
//// --Commented out by Inspection START (11/5/2016 9:08 PM):
////        private TelemetryItem(String tag, String message) {
////            this.tag = tag;
////            this.message = message;
////        }
//// --Commented out by Inspection STOP (11/5/2016 9:08 PM)
//
//        public String getTag() {
//            return tag;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//    }
// --Commented out by Inspection STOP (11/5/2016 9:08 PM)
}
