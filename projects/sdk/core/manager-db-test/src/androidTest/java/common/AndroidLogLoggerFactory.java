/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package common;

import android.util.Log;

import com.tencent.shadow.core.common.ILoggerFactory;
import com.tencent.shadow.core.common.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AndroidLogLoggerFactory implements ILoggerFactory {

    private static final int LOG_LEVEL_TRACE = 5;
    private static final int LOG_LEVEL_DEBUG = 4;
    private static final int LOG_LEVEL_INFO = 3;
    private static final int LOG_LEVEL_WARN = 2;
    private static final int LOG_LEVEL_ERROR = 1;

    private static AndroidLogLoggerFactory sInstance = new AndroidLogLoggerFactory();

    public static ILoggerFactory getInstance() {
        return sInstance;
    }

    final private ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

    public Logger getLogger(String name) {
        Logger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            Logger newInstance = new IVLogger(name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    class IVLogger implements Logger {
        private String name;

        IVLogger(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        private void log(int level, String message, Throwable t) {
            final String tag = String.valueOf(name);

            switch (level) {
                case LOG_LEVEL_TRACE:
                case LOG_LEVEL_DEBUG:
                    if (t == null)
                        Log.d(tag, message);
                    else
                        Log.d(tag, message, t);
                    break;
                case LOG_LEVEL_INFO:
                    if (t == null)
                        Log.i(tag, message);
                    else
                        Log.i(tag, message, t);
                    break;
                case LOG_LEVEL_WARN:
                    if (t == null)
                        Log.w(tag, message);
                    else
                        Log.w(tag, message, t);
                    break;
                case LOG_LEVEL_ERROR:
                    if (t == null)
                        Log.e(tag, message);
                    else
                        Log.e(tag, message, t);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        @Override
        public void trace(String msg) {
            log(LOG_LEVEL_TRACE, msg, null);
        }

        @Override
        public void trace(String format, Object o) {
            FormattingTuple tuple = MessageFormatter.format(format, o);
            log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
        }

        @Override
        public void trace(String format, Object o, Object o1) {
            FormattingTuple tuple = MessageFormatter.format(format, o, o1);
            log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
        }

        @Override
        public void trace(String format, Object... objects) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
            log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
        }

        @Override
        public void trace(String msg, Throwable throwable) {
            log(LOG_LEVEL_TRACE, msg, throwable);
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public void debug(String msg) {
            log(LOG_LEVEL_DEBUG, msg, null);
        }

        @Override
        public void debug(String format, Object o) {
            FormattingTuple tuple = MessageFormatter.format(format, o);
            log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
        }

        @Override
        public void debug(String format, Object o, Object o1) {
            FormattingTuple tuple = MessageFormatter.format(format, o, o1);
            log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
        }

        @Override
        public void debug(String format, Object... objects) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
            log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
        }

        @Override
        public void debug(String msg, Throwable throwable) {
            log(LOG_LEVEL_DEBUG, msg, throwable);
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        @Override
        public void info(String msg) {
            log(LOG_LEVEL_INFO, msg, null);
        }

        @Override
        public void info(String format, Object o) {
            FormattingTuple tuple = MessageFormatter.format(format, o);
            log(LOG_LEVEL_INFO, tuple.getMessage(), null);
        }

        @Override
        public void info(String format, Object o, Object o1) {
            FormattingTuple tuple = MessageFormatter.format(format, o, o1);
            log(LOG_LEVEL_INFO, tuple.getMessage(), null);
        }

        @Override
        public void info(String format, Object... objects) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
            log(LOG_LEVEL_INFO, tuple.getMessage(), null);
        }

        @Override
        public void info(String msg, Throwable throwable) {
            log(LOG_LEVEL_INFO, msg, throwable);
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void warn(String msg) {
            log(LOG_LEVEL_WARN, msg, null);
        }

        @Override
        public void warn(String format, Object o) {
            FormattingTuple tuple = MessageFormatter.format(format, o);
            log(LOG_LEVEL_WARN, tuple.getMessage(), null);
        }

        @Override
        public void warn(String format, Object o, Object o1) {
            FormattingTuple tuple = MessageFormatter.format(format, o, o1);
            log(LOG_LEVEL_WARN, tuple.getMessage(), null);
        }

        @Override
        public void warn(String format, Object... objects) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
            log(LOG_LEVEL_WARN, tuple.getMessage(), null);
        }

        @Override
        public void warn(String msg, Throwable throwable) {
            log(LOG_LEVEL_WARN, msg, throwable);
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(String msg) {
            log(LOG_LEVEL_ERROR, msg, null);
        }

        @Override
        public void error(String format, Object o) {
            FormattingTuple tuple = MessageFormatter.format(format, o);
            log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
        }

        @Override
        public void error(String format, Object o, Object o1) {
            FormattingTuple tuple = MessageFormatter.format(format, o, o1);
            log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
        }

        @Override
        public void error(String format, Object... objects) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
            log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
        }

        @Override
        public void error(String msg, Throwable throwable) {
            log(LOG_LEVEL_ERROR, msg, throwable);
        }
    }
}

class FormattingTuple {

    static public FormattingTuple NULL = new FormattingTuple(null);

    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public FormattingTuple(String message) {
        this(message, null, null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getArgArray() {
        return argArray;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}

final class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    /**
     * Performs single argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p>
     * For example,
     *
     * <pre>
     * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;);
     * </pre>
     * <p>
     * will return the string "Hi there.".
     * <p>
     *
     * @param messagePattern The message pattern which will be parsed and formatted
     * @param arg            The argument to be substituted in place of the formatting anchor
     * @return The formatted message
     */
    final public static FormattingTuple format(String messagePattern, Object arg) {
        return arrayFormat(messagePattern, new Object[]{arg});
    }

    /**
     * Performs a two argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p>
     * For example,
     *
     * <pre>
     * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
     * </pre>
     * <p>
     * will return the string "Hi Alice. My name is Bob.".
     *
     * @param messagePattern The message pattern which will be parsed and formatted
     * @param arg1           The argument to be substituted in place of the first formatting
     *                       anchor
     * @param arg2           The argument to be substituted in place of the second formatting
     *                       anchor
     * @return The formatted message
     */
    final public static FormattingTuple format(final String messagePattern, Object arg1, Object arg2) {
        return arrayFormat(messagePattern, new Object[]{arg1, arg2});
    }


    static final Throwable getThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }

        final Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable) lastEntry;
        }
        return null;
    }

    final public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
        Throwable throwableCandidate = getThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = trimmedCopy(argArray);
        }
        return arrayFormat(messagePattern, args, throwableCandidate);
    }

    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        final int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    final public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray, Throwable throwable) {

        if (messagePattern == null) {
            return new FormattingTuple(null, argArray, throwable);
        }

        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }

        int i = 0;
        int j;
        // use string builder for better multicore performance
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);

        int L;
        for (L = 0; L < argArray.length; L++) {

            j = messagePattern.indexOf(DELIM_STR, i);

            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return new FormattingTuple(messagePattern, argArray, throwable);
                } else { // add the tail string which contains no variables and return
                    // the result.
                    sbuf.append(messagePattern, i, messagePattern.length());
                    return new FormattingTuple(sbuf.toString(), argArray, throwable);
                }
            } else {
                if (isEscapedDelimeter(messagePattern, j)) {
                    if (!isDoubleEscaped(messagePattern, j)) {
                        L--; // DELIM_START was escaped, thus should not be incremented
                        sbuf.append(messagePattern, i, j - 1);
                        sbuf.append(DELIM_START);
                        i = j + 1;
                    } else {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\{}"
                        // we have to consume one backward slash
                        sbuf.append(messagePattern, i, j - 1);
                        deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                        i = j + 2;
                    }
                } else {
                    // normal case
                    sbuf.append(messagePattern, i, j);
                    deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                    i = j + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        sbuf.append(messagePattern, i, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), argArray, throwable);
    }

    final static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {

        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        if (potentialEscape == ESCAPE_CHAR) {
            return true;
        } else {
            return false;
        }
    }

    final static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR) {
            return true;
        } else {
            return false;
        }
    }

    // special treatment of array values was suggested by 'lizongbo'
    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            sbuf.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(sbuf, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                booleanArrayAppend(sbuf, (boolean[]) o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(sbuf, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(sbuf, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(sbuf, (short[]) o);
            } else if (o instanceof int[]) {
                intArrayAppend(sbuf, (int[]) o);
            } else if (o instanceof long[]) {
                longArrayAppend(sbuf, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(sbuf, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[]) o);
            } else {
                objectArrayAppend(sbuf, (Object[]) o, seenMap);
            }
        }
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable t) {
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1)
                    sbuf.append(", ");
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

}

