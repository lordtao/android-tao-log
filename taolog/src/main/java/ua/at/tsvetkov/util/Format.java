/**
 * ****************************************************************************
 * Copyright (c) 2010 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p/>
 * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 * <p/>
 * Project:
 * TAO Core
 * <p/>
 * License agreement:
 * <p/>
 * 1. This code is published AS IS. Author is not responsible for any damage that can be
 * caused by any application that uses this code.
 * 2. Author does not give a garantee, that this code is error free.
 * 3. This code can be used in NON-COMMERCIAL applications AS IS without any special
 * permission from author.
 * 4. This code can be modified without any special permission from author IF AND ONLY IF
 * this license agreement will remain unchanged.
 * ****************************************************************************
 */
package ua.at.tsvetkov.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Prepare formatted string from different objects for visual printing in {@link Log} class
 *
 * @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:al@ukr.net
 */
final class Format {

    static final int MAX_TAG_LENGTH = 65;
    static final char PREFIX = '|';
    static final char COLON = ':';
    static final String HEX_FORM = "%02X ";
    static final String PREFIX_MAIN_STRING = " ▪ ";
    static final String GROUP = "|Group:";
    static final String PRIORITY = "|Priority:";
    static final String ID = "|Id:";
    static final String THREAD_NAME = "Thread Name:";
    static final String HALF_LINE = "---------------------";
    static final String ACTIVITY_MESSAGE = " Activity: ";
    static final String JAVA = ".java";
    static final String ACTIVITY_CLASS = "android.app.Activity";
    static final String DELIMITER_START = " · ";
    static final String DELIMITER = "···························································································";
    static final String HALF_DELIMITER = "·····································";
    static final String THROWABLE_DELIMITER_START = " ‖ ";
    static final String THROWABLE_DELIMITER_PREFIX = "    ";
    static final String THROWABLE_DELIMITER = "===========================================================================================";
    static final String NL = "\n";
    static final String ARRAY = "Array";

    static volatile int maxTagLength = MAX_TAG_LENGTH;
    static volatile String stamp = null;


    private Format() {
    }

    /**
     * Return String representation of map. Each item in new line.
     *
     * @param map a Map
     * @return String representation of map
     */
    static String map(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        int max = 0;
        for (Map.Entry<?, ?> item : map.entrySet()) {
            int length = item.getKey().toString().length();
            if (max < length) {
                max = length;
            }
        }
        StringBuilder sb = new StringBuilder();
        String formatString = "%-" + max + "s = %s";
        for (Map.Entry<?, ?> item : map.entrySet()) {
            sb.append(String.format(formatString, item.getKey(), item.getValue()));
            sb.append(NL);
        }
        return sb.toString();
    }

    /**
     * Return String representation of list. Each item in new line.
     *
     * @param list a List
     * @return String representation of map
     */
    static String list(List<?> list) {
        if (list == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            sb.append(item.toString());
            sb.append(NL);
        }
        return sb.toString();
    }

    /**
     * Return String representation of Objects array. Each item in new line.
     *
     * @param array an array
     * @return String representation of array
     */
    static <T> String array(T[] array) {
        if (array == null) {
            return "null";
        }
        List<T> list = Arrays.asList(array);
        StringBuilder sb = new StringBuilder();
        int indx = 0;
        for (Object item : list) {
            sb.append('[');
            sb.append(indx++);
            sb.append("] ");
            sb.append(item.getClass().getSimpleName());
            sb.append(": ");
            sb.append(item.toString());
            sb.append(NL);
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(int[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(float[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(boolean[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(char[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(double[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    static String array(long[] array) {
        if (array == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(',');
            } else {
                sb.append(']');
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of Object. Each field in new line.
     *
     * @param objs a class for representation
     * @return String representation of class
     */
    static String objn(Object objs) {
        if (objs == null) {
            return "null";
        }
        Class<?> cl = objs.getClass();
        StringBuilder sb = new StringBuilder();
        Field[] fields = cl.getDeclaredFields();

        int max = 0;
        for (Field field : fields) {
            int length = field.getName().length();
            if (max < length) {
                max = length;
            }
        }
        String formatString = PREFIX + "%-" + max + "s = %s" + NL;

        for (Field field : fields) {
            try {
                Field myField = getField(cl, field.getName());
                myField.setAccessible(true);
                sb.append(String.format(formatString, field.getName(), myField.get(objs)));
            } catch (Exception e) {
                sb.append(PREFIX);
                sb.append(e.getMessage());
                sb.append(field.getName());
            }
        }
        return sb.toString();
    }

    /**
     * Return String representation of class.
     *
     * @param myObj a class for representation
     * @return String representation of class
     */
    static String objl(Object myObj) {
        if (myObj == null) {
            return "null";
        }
        Class<?> cl = myObj.getClass();
        Field[] fields = cl.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(cl.getSimpleName());
        sb.append(" [");
        for (int i = 0; i < fields.length; i++) {
            try {
                Field myField = getField(cl, fields[i].getName());
                myField.setAccessible(true);
                sb.append(fields[i].getName());
                sb.append("=");
                sb.append(myField.get(myObj));
                if (fields.length != 1 && i < (fields.length - 1)) {
                    sb.append(", ");
                }
            } catch (Exception e) {
                sb.append(PREFIX);
                sb.append(sb.append(e.getMessage()));
                sb.append(fields[i].getName());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Return readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     * @return readable representation
     */
    static String hex(byte[] data, int countPerLine) {
        if (data == null) {
            Log.v("null");
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (byte element : data) {
            count++;
            sb.append(String.format(HEX_FORM, element));
            if (count >= countPerLine) {
                count = 0;
                sb.append(NL);
            }
        }
        return sb.toString();
    }

    /**
     * Return readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     * @return readable representation
     */
    static String hex(byte[] data) {
        if (data == null) {
            Log.v("null");
        }
        StringBuilder sb = new StringBuilder(data.length * 3);
        for (byte element : data) {
            sb.append(String.format(HEX_FORM, element));
        }
        sb.trimToSize();
        return sb.toString();
    }

    /**
     * Return readable representation of xml with indentation 2
     *
     * @param xmlStr your xml data
     * @return readable representation
     */
    static String xml(String xmlStr) {
        return xml(xmlStr, 2);
    }

    /**
     * Return readable representation of xml
     *
     * @param xmlStr your xml data
     * @param indent xml identetion
     * @return readable representation
     */
    static String xml(String xmlStr, int indent) {
        if (xmlStr == null) {
            Log.v("null");
        }
        try {
            // Turn xml string into a document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xmlStr.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//         transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    //============================== Fragments ==============================

    static void printFragmentsStack(String className, FragmentManager fm, String title, String operation, int backStackCount) {
        String[] logs = new String[backStackCount];
        for (int i = 0; i < backStackCount; i++) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
            logs[i] = "     #" + i + "  " + entry.getName();
        }
        printFragmentsStack(className, title, operation, logs);
    }


    static void printFragmentsStack(String className, android.support.v4.app.FragmentManager fm, String title, String operation, int backStackCount) {
        String[] logs = new String[backStackCount];
        for (int i = 0; i < backStackCount; i++) {
            android.support.v4.app.FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
            logs[i] = "|    #" + i + "  " + entry.getName();
        }
        printFragmentsStack(className, title, operation, logs);
    }

    static void printFragmentsStack(String className, String title, String operation, String[] logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------ ");
        sb.append(title);
        sb.append(" ------------------\n");
        sb.append("|                 ");
        sb.append(operation);
        sb.append(NL);
        for (int i = 0; i < logs.length; i++) {
            sb.append(logs[i]);
            sb.append(NL);
        }
        sb.append(" --------------------------------------------------------");

        StringBuilder spSb = new StringBuilder();
        spSb.append(PREFIX_MAIN_STRING);
        addStamp(spSb);
        spSb.append(className);
        addSpaces(spSb);
        android.util.Log.v(spSb.toString(), sb.toString());
    }

    // ============================ Private common methods ==============================

    static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    static String getTag() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();

        fillTag(className, traces, sb);

        return sb.toString();
    }

    static void fillTag(String className, StackTraceElement[] traces, StringBuilder sb) {
        sb.append(PREFIX_MAIN_STRING);
        addStamp(sb);
        addLocation(className, traces, sb);
        addSpaces(sb);
    }

    static String gatExtendedTag(Object obj) {
        if (obj == null) {
            Log.v("null");
        }
        Class clazz = obj.getClass();
        String className = clazz.getName();
        String classSimpleName = clazz.getSimpleName();
        String parentClassName = Log.class.getName();

        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_MAIN_STRING);
        addStamp(sb);

        int sbPrefixLength = sb.length();

        if (!clazz.isAnonymousClass()) {
            for (int i = 0; i < traces.length; i++) {
                if (traces[i].getClassName().startsWith(className)) {
                    sb.append(classSimpleName);
                    sb.append(JAVA);
                    sb.append(' ');
                    break;
                }
            }
        } else {
            sb.append("(Anonymous Class) ");
        }
        if (sb.length() > sbPrefixLength) {
            sb.append('<');
            sb.append('-');
            sb.append(' ');
        }
        addLocation(parentClassName, traces, sb);
        addSpaces(sb);

        return sb.toString();
    }

    static String getActivityTag(Activity activity) {
        String className = activity.getClass().getCanonicalName();
        String classSimpleName = activity.getClass().getSimpleName();

        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_MAIN_STRING);
        addStamp(sb);

        StackTraceElement trace = findStackTraceElement(traces, className);

        if (trace != null) {
            addClassLink(sb, classSimpleName, trace.getLineNumber());
        } else {
            addClassLink(sb, classSimpleName, 0);
            trace = findStackTraceElement(traces, ACTIVITY_CLASS);
        }

        sb.append(trace.getMethodName());

        addSpaces(sb);

        return sb.toString();
    }

    static String getActivityMethodInfo(Activity activity) {
        String className = activity.getClass().getCanonicalName();
        String classSimpleName = activity.getClass().getSimpleName();

        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

        StringBuilder sb = new StringBuilder();
        sb.append(HALF_LINE);
        sb.append(ACTIVITY_MESSAGE);

        StackTraceElement trace = findStackTraceElement(traces, className);

        boolean isOverride = false;
        if (trace != null) {
            isOverride = true;
        } else {
            trace = findStackTraceElement(traces, ACTIVITY_CLASS);
        }

        sb.append(' ');
        sb.append(classSimpleName);

        if (isOverride) {
            sb.append(" (method overridden)");
        }

        sb.append(" -> ");
        sb.append(trace.getMethodName());
        sb.append(' ');
        sb.append(HALF_LINE);

        return sb.toString();
    }

    static void addStamp(StringBuilder sb) {
        if (stamp != null && stamp.length() > 0) {
            sb.append(stamp);
            sb.append(' ');
        }
    }

    static void addLocation(String className, StackTraceElement[] traces, StringBuilder sb) {
        boolean found = false;
        for (int i = 0; i < traces.length; i++) {
            try {
                if (found) {
                    if (!traces[i].getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(traces[i].getClassName());
                        int notZeroLineNumberOffset = 0;
                        int lineNumber = 0;
                        while (lineNumber == 0) {
                            lineNumber = traces[i + notZeroLineNumberOffset++].getLineNumber();
                        }
                        addClassLink(sb, getClassName(clazz), lineNumber);
                        sb.append(traces[i].getMethodName());
                        break;
                    }
                } else if (traces[i].getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
                android.util.Log.e("LOG", e.toString());
            }
        }
    }

    static void addClassLink(StringBuilder sb, String className, int lineNumber) {
        sb.append('(');
        sb.append(className);
        sb.append(JAVA);
        sb.append(COLON);
        sb.append(lineNumber);
        sb.append(')');
        sb.append(' ');
    }

    static void addSpaces(StringBuilder sb) {
        sb.append(' ');
        int extraSpaceCount = maxTagLength - sb.length();
        if (extraSpaceCount < 0) {
            maxTagLength = sb.length();
            extraSpaceCount = 0;
        }
        for (int i = 0; i < extraSpaceCount; i++) {
            sb.append(' ');
        }
        sb.append('\u21DB');
    }

    static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                if (clazz.getName().contains("$")) {
                    return clazz.getName().substring(clazz.getName().lastIndexOf(0x2e) + 1, clazz.getName().lastIndexOf(0x24));
                } else {
                    return clazz.getSimpleName();
                }
            }
            return getClassName(clazz.getEnclosingClass());
        }
        return "";
    }

    static StackTraceElement findStackTraceElement(StackTraceElement[] traces, String startsFrom) {
        StackTraceElement trace = null;
        for (int i = 0; i < traces.length; i++) {
            if (traces[i].getClassName().startsWith(startsFrom)) {
                trace = traces[i];
                break;
            }
        }
        return trace;
    }

    static String getFormattedMessage(String message) {
        return getFormattedMessage(message, null);
    }

    static String getFormattedMessage(String message, String title) {
        String[] lines = message.split("\\n");
        StringBuilder sb = new StringBuilder();

        if (Log.isLogOutlined) {
            if (title == null) {
                sb.append(DELIMITER);
            } else {
                sb.append(HALF_DELIMITER);
                sb.append(' ');
                sb.append(title);
                sb.append(' ');
                sb.append(HALF_DELIMITER);
            }
            sb.append(NL);
        }

        for (int i = 0; i < lines.length; i++) {
            if (Log.isLogOutlined) {
                sb.append(DELIMITER_START);
            } else if (i != 0) {
                sb.append(' ');
            }
            sb.append(lines[i]);
            sb.append(NL);
        }

        if (Log.isLogOutlined) {
            sb.append(' ');
            sb.append(DELIMITER);
        }

        return sb.toString();
    }

    static String getFormattedThrowable(Throwable throwable) {
        return getFormattedThrowable(null, throwable);
    }

    static String getFormattedThrowable(String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        if (Log.isLogOutlined) {
            sb.append(THROWABLE_DELIMITER);
            sb.append(NL);
        }
        addFormattedMessageForTrowable(sb, message);
        addThrowableStartDelimeter(sb);
        if (!Log.isLogOutlined) {
            sb.append(' ');
        }
        sb.append(throwable.toString());
        sb.append(NL);
        if (throwable == null) {
            addThrowableStartDelimeter(sb);
            sb.append(THROWABLE_DELIMITER_PREFIX);
            sb.append("throwable == null");
            sb.append(NL);
        } else {
            StackTraceElement[] st = throwable.getStackTrace();
            for (int i = 0; i < st.length; i++) {
                addThrowableStartDelimeter(sb);
                sb.append(THROWABLE_DELIMITER_PREFIX);
                sb.append(st[i].toString());
                if(Log.isLogOutlined) {
                    sb.append(NL);
                } else if(i < st.length-1){
                    sb.append(NL);
                }
            }
        }
        sb.append(' ');
        if (Log.isLogOutlined) {
            sb.append(THROWABLE_DELIMITER);
        }
        return sb.toString();
    }

    static void addFormattedMessageForTrowable(StringBuilder sb, String message) {
        if (message != null) {
            String[] lines = message.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                if (i != 0 && !Log.isLogOutlined) {
                    sb.append(' ');
                }
                addThrowableStartDelimeter(sb);
                sb.append(lines[i]);
                sb.append(NL);
            }
        }
    }

    private static void addThrowableStartDelimeter(StringBuilder sb) {
        if (Log.isLogOutlined) {
            sb.append(THROWABLE_DELIMITER_START);
        }
    }

    //==================== Stack trace ======================


    static void addStackTrace(StringBuilder sb, Throwable throwable) {
        final StackTraceElement[] traces = throwable.getStackTrace();
        addStackTrace(sb, traces);
    }

    static void addStackTrace(StringBuilder sb, Thread thread) {
        final StackTraceElement[] traces = thread.getStackTrace();
        addStackTrace(sb, traces);
    }

    @NonNull
    static void addStackTrace(StringBuilder sb, StackTraceElement[] traces) {
        for (int i = 4; i < traces.length; i++) {
            sb.append(THROWABLE_DELIMITER_PREFIX);
            sb.append(traces[i].toString());
            sb.append('\n');
        }
    }

    /**
     * @param thread thread for Logged
     * @return filled StringBuilder for next filling
     */
    static void addThreadInfo(StringBuilder sb, Thread thread) {
        if (thread != null) {
            long id = thread.getId();
            String name = thread.getName();
            long priority = thread.getPriority();
            sb.append(THREAD_NAME);
            sb.append(name);
            sb.append(ID);
            sb.append(id);
            sb.append(PRIORITY);
            sb.append(priority);
            sb.append(GROUP);
            ThreadGroup group = thread.getThreadGroup();
            if (group != null) {
                String groupName = group.getName();
                sb.append(groupName);
            } else {
                sb.append("null");
            }
        } else {
            sb.append("The thread == null");
        }
    }

    static void addMessage(StringBuilder sb, String message) {
        if (message != null && message.length() > 0) {
            sb.append(message);
            sb.append(Format.NL);
        }
    }

}
