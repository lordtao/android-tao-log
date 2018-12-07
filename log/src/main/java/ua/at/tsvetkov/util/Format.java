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
import java.util.regex.Pattern;

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
    static final int MAGIC_SPACES_COUNT = 34;
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
    static final String DELIMITER_START = "· ";
    static final String DELIMITER = "···························································································";
    static final String HALF_DELIMITER = "·····································";
    static final String THROWABLE_DELIMITER_START = "‖ ";
    static final String THROWABLE_DELIMITER_PREFIX = "    ";
    static final String THROWABLE_DELIMITER = "===========================================================================================";
    static final String NL = "\n";
    static final String ARRAY = "Array";
    public static final char SPACE = ' ';
   public static final String AT = "at ";

   static volatile int maxTagLength = MAX_TAG_LENGTH;
   static volatile int beforeTagSpacesCount;
   static volatile String stamp = null;

   static {
      beforeTagSpacesCount = getPackageNameLength() + MAGIC_SPACES_COUNT;
   }

   private Format() {

   }

   private static int getPackageNameLength() {
      String thisPackageName = Log.class.getPackage().getName();
      StackTraceElement[] tr = Thread.currentThread().getStackTrace();
      boolean notFound = true;
      int id = 0;
      String appPackageName = null;
      while (notFound) {
         appPackageName = tr[id++].getClassName();
         if (appPackageName.contains(thisPackageName)) {
            notFound = false;
         }
      }
      notFound = true;
      while (notFound) {
         appPackageName = tr[id++].getClassName();
         if (!appPackageName.contains(thisPackageName)) {
            notFound = false;
            appPackageName = appPackageName.substring(0, appPackageName.lastIndexOf('.'));
         }
      }
      if (notFound) {
         return 0;
      } else {
         return appPackageName.length();
      }
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
    * Return String representation of array.
    *
    * @param array an array
    * @return String representation of array
    */
   static String array(String[] array) {
      if (array == null) {
         return "null";
      }
      List<String> list = Arrays.asList(array);
      StringBuilder sb = new StringBuilder();
      int indx = 0;
      for (String item : list) {
         sb.append('[');
         sb.append(indx++);
         sb.append("] ");
         sb.append(item);
         sb.append(NL);
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
   static StringBuilder hex(byte[] data, int countPerLine) {
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
      return sb;
   }

   /**
    * Return readable representation of bytes array data like 0F CD AD....
    *
    * @param data your bytes array data
    * @return readable representation
    */
   static StringBuilder hex(byte[] data) {
      if (data == null) {
         Log.v("null");
      }
      StringBuilder sb = new StringBuilder(data.length * 3);
      for (byte element : data) {
         sb.append(String.format(HEX_FORM, element));
      }
      sb.trimToSize();
      return sb;
   }

   /**
    * Return readable representation of xml with indentation 2
    *
    * @param xmlStr your xml data
    * @return readable representation
    */
   static StringBuilder xml(String xmlStr) {
      return xml(xmlStr, 2);
   }

   /**
    * Return readable representation of xml
    *
    * @param xmlStr your xml data
    * @param indent xml identetion
    * @return readable representation
    */
   static StringBuilder xml(String xmlStr, int indent) {
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
         return new StringBuilder(stringWriter.toString());
      } catch (Exception e) {
         return new StringBuilder(e.getMessage());
      }

   }

   //============================== Fragments ==============================

   static String getFragmentsStackInfo(FragmentManager fm, String operation, int backStackCount) {
      String[] logs = new String[backStackCount + 1];
      logs[0] = operation;
      for (int i = 0; i < backStackCount; i++) {
         FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
         logs[i + 1] = "   #" + i + "  " + entry.getName();
      }
      return getFSString(logs);
   }


   static String getFragmentsStackInfo(android.support.v4.app.FragmentManager fm, String operation, int backStackCount) {
      String[] logs = new String[backStackCount + 1];
      logs[0] = operation;
      for (int i = 0; i < backStackCount; i++) {
         android.support.v4.app.FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
         logs[i + 1] = "   #" + i + "  " + entry.getName();
      }
      return getFSString(logs);
   }

   static String getFSString(String[] array) {
      if (array == null) {
         return "null";
      }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < array.length; i++) {
         sb.append(array[i]);
         sb.append(NL);
      }
      return sb.toString();
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

   static String getTag(Object obj) {
      if (obj == null) {
         Log.v("null");
      }
      Class clazz = obj.getClass();
      String className = clazz.getName();
      String parentClassName = Log.class.getName();

      final StackTraceElement[] traces = Thread.currentThread().getStackTrace();

      StringBuilder sb = new StringBuilder();
      sb.append(PREFIX_MAIN_STRING);
      addStamp(sb);

      int sbPrefixLength = sb.length();

      if (!clazz.isAnonymousClass()) {
         for (int i = 0; i < traces.length; i++) {
            if (traces[i].getClassName().startsWith(className)) {
               sb.append(traces[i].getFileName());
               sb.append(SPACE);
               break;
            }
         }
      } else {
         sb.append("(Anonymous Class) ");
      }
      if (sb.length() > sbPrefixLength) {
         sb.append('<');
         sb.append('-');
         sb.append(SPACE);
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

      sb.append(SPACE);
      sb.append(classSimpleName);

      if (isOverride) {
         sb.append(" (method overridden)");
      }

      sb.append(" -> ");
      sb.append(trace.getMethodName());
      sb.append(SPACE);
      sb.append(HALF_LINE);

      return sb.toString();
   }

   static void addStamp(StringBuilder sb) {
      if (stamp != null && stamp.length() > 0) {
         sb.append(stamp);
         sb.append(SPACE);
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
//                        addClassLink(sb, getClassName(clazz), lineNumber);
                  addClassLink(sb, traces[i].getFileName(), lineNumber);
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

   static void addClassLink(StringBuilder sb, String fileName, int lineNumber) {
      sb.append('(');
      sb.append(fileName);
      sb.append(COLON);
      sb.append(lineNumber);
      sb.append(')');
      sb.append(SPACE);
   }

   static void addSpaces(StringBuilder sb) {
      if (!Log.isAlignNewLines()) {
         return;
      }
      int extraSpaceCount = maxTagLength - sb.length();
      if (extraSpaceCount < 0) {
         maxTagLength = sb.length();
         extraSpaceCount = 0;
      }
      for (int i = 0; i < extraSpaceCount; i++) {
         sb.append(SPACE);
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

   static StringBuilder getFormattedMessage(String message) {
      return getFormattedMessage(null, message, null);
   }

   static StringBuilder getFormattedMessage(StringBuilder stringBuilder) {
      return getFormattedMessage(stringBuilder, null, null);
   }

   static StringBuilder getFormattedMessage(String message, String title) {
      return getFormattedMessage(null, message, title);
   }

   static StringBuilder getFormattedThrowable(Throwable throwable) {
      return getFormattedThrowable(null, throwable);
   }

   static StringBuilder getFormattedMessage(StringBuilder stringBuilder, String message, String title) {
      String[] lines;
      if (stringBuilder != null) {
         if (message != null) {
            stringBuilder.append(message);
         }
         lines = Pattern.compile("\\n").split(stringBuilder);
      } else {
         lines = message.split("\\n");
      }
      int linesCount = getLinesCount(title, lines);
      lines = createLines(title, lines, linesCount);
      if (Log.isAlignNewLines()) {
         appendAlignmentForLines(lines);
      }

      StringBuilder sb = new StringBuilder();
      if (linesCount > 1 && !Log.isAlignNewLines()) {
         sb.append(" \n");
      }
      appendLines(lines, sb);
      return sb;
   }

    static StringBuilder getFormattedThrowable(String message, Throwable throwable) {
        String[] lines = null;
        if (message != null) {
            lines = message.concat("\n" + getThrowableMessage(throwable)).split("\\n");
      } else {
         lines = getThrowableMessage(throwable).split("\\n");
        }
        int linesCount = getLinesCount(lines, throwable);
        lines = createLines(throwable, lines, linesCount);

      if (Log.isAlignNewLines()) {
         appendAlignmentForLines(lines);
      }

      StringBuilder sb = new StringBuilder();
      if (linesCount > 1 && !Log.isAlignNewLines()) {
         sb.append(" \n");
      }
      appendLines(lines, sb);

      return sb;
   }

   @NonNull
   private static String getThrowableMessage(Throwable throwable) {
      return throwable.getClass().getName() + ": " + throwable.getMessage();
   }private static int getLinesCount(String title, String[] lines) {
      int count = (lines == null) ? 0 : lines.length;
      if (Log.isLogOutlined) {
         count = count + 2;
      } else if (title != null) {
         count++;
      }
      return count;
   }

   private static int getLinesCount(String[] lines, Throwable throwable) {
      int count = (lines == null) ? 0 : lines.length;
      if (Log.isLogOutlined) {
         count = count + 2;
      }
      if (throwable != null) {
         count = count + throwable.getStackTrace().length;
      } else {
         count++;
      }
      return count;
   }

   private static String[] createLines(String title, String[] lines, int count) {
      String[] lns = new String[count];
      if (Log.isLogOutlined) {
         if (title == null) {
            lns[0] = DELIMITER;
         } else {
            lns[0] = HALF_DELIMITER + SPACE + title + SPACE + HALF_DELIMITER;
         }
         lns[lns.length - 1] = DELIMITER;
         for (int i = 0; i < lines.length; i++) {
            lns[i + 1] = DELIMITER_START + lines[i];
         }
      } else { // Not Boxed
         int shift = 0;
         if (title != null) {
            lns[0] = title;
            shift = 1;
         }
         for (int i = 0; i < lines.length; i++) {
            lns[i + shift] = lines[i];
         }
      }

      return lns;
   }

   private static String[] createLines(Throwable throwable, String[] lines, int count) {
      String[] lns = new String[count];
      int linesCount = (lines == null) ? 0 : lines.length;
      if (Log.isLogOutlined) {
         lns[0] = THROWABLE_DELIMITER;
         lns[lns.length - 1] = THROWABLE_DELIMITER;


         for (int i = 0; i < linesCount; i++) {
            lns[i + 1] = THROWABLE_DELIMITER_START + lines[i];
         }

            if (throwable == null) {
                lns[linesCount + 1] = THROWABLE_DELIMITER_START + "throwable == null";
            } else {
                StackTraceElement[] stack = throwable.getStackTrace();
                for (int i = 0; i < stack.length; i++) {
                    if (i == 0) {
                        lns[linesCount + 1 + i] = THROWABLE_DELIMITER_START + AT + stack[i].toString();
                    } else {
                        lns[linesCount + 1 + i] = THROWABLE_DELIMITER_START + THROWABLE_DELIMITER_PREFIX + AT + stack[i].toString();
                    }
                }
            }
        } else { // Not Boxed
            for (int i = 0; i < linesCount; i++) {
                lns[i] = lines[i];
            }
            if (throwable == null) {
                lns[0] = "throwable == null";
            } else {
                StackTraceElement[] stack = throwable.getStackTrace();
                for (int i = 0; i < stack.length; i++) {
                    if (i == 0) {
                        lns[linesCount + i] = stack[i].toString();
                    } else {
                        lns[linesCount + i] = THROWABLE_DELIMITER_PREFIX + stack[i].toString();
                    }
                }
            }
        }

      return lns;
   }

   private static void appendLines(String[] lines, StringBuilder sb) {
      for (int i = 0; i < lines.length; i++) {
         sb.append(lines[i]);
         sb.append(NL);
      }
   }

   private static void appendAlignmentForLines(String[] lines) {
      if (lines == null || lines.length == 0) {
         return;
      }
      int spacesCount = maxTagLength + beforeTagSpacesCount;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < spacesCount; i++) {
         sb.append(SPACE);
      }
      for (int i = 1; i < lines.length; i++) {
         lines[i] = sb + lines[i];
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
         sb.append(AT);
         sb.append( traces[i].toString());
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
