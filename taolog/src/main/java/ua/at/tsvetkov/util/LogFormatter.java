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
public class LogFormatter {

   private static final String HEX_FORM = "%02X ";
   private static final char PREFIX = '|';
   private static final String NL = "\n";
   private static final String HALF_LINE = "---------------------";
   private static final String MAP_LINE = "-------------------------- Map ---------------------------" + NL;
   private static final String LIST_LINE = "-------------------------- List --------------------------" + NL;
   private static final String OBJECT_ARRAY_LINE = "----------------------- Objects Array ---------------------" + NL;
   private static final String LINE = "----------------------------------------------------------" + NL;

   private LogFormatter() {
   }

   /**
    * Return String representation of map. Each item in new line.
    *
    * @param map a Map
    * @return String representation of map
    */
   public static String map(Map<?, ?> map) {
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
      sb.append(MAP_LINE);
      for (Map.Entry<?, ?> item : map.entrySet()) {
         sb.append(String.format(formatString, item.getKey(), item.getValue()));
         sb.append(NL);
      }
      sb.append(LINE);
      return sb.toString();
   }

   /**
    * Return String representation of list. Each item in new line.
    *
    * @param list a List
    * @return String representation of map
    */
   public static String list(List<?> list) {
      if (list == null) {
         return "null";
      }
      StringBuilder sb = new StringBuilder();
      sb.append(LIST_LINE);
      for (Object item : list) {
         sb.append(item.toString());
         sb.append(NL);
      }
      sb.append(LINE);
      return sb.toString();
   }

   /**
    * Return String representation of Objects array. Each item in new line.
    *
    * @param array an array
    * @return String representation of array
    */
   public static <T> String array(T[] array) {
      if (array == null) {
         return "null";
      }
      List<T> list = Arrays.asList(array);
      StringBuilder sb = new StringBuilder();
      sb.append(OBJECT_ARRAY_LINE);
      for (Object item : list) {
         sb.append(item.toString());
         sb.append(NL);
      }
      sb.append(LINE);
      return sb.toString();
   }

   /**
    * Return String representation of array.
    *
    * @param array an array
    * @return String representation of array
    */
   public static String array(int[] array) {
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
   public static String array(float[] array) {
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
   public static String array(boolean[] array) {
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
   public static String array(char[] array) {
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
   public static String array(double[] array) {
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
   public static String array(long[] array) {
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
   public static String objn(Object objs) {
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

      sb.append(HALF_LINE);
      sb.append(' ');
      sb.append(cl.getSimpleName());
      sb.append(' ');
      sb.append(HALF_LINE);
      sb.append(NL);
      for (Field field : fields) {
         try {
            Field myField = Log.getField(cl, field.getName());
            myField.setAccessible(true);
            sb.append(String.format(formatString, field.getName(), myField.get(objs)));
         } catch (Exception e) {
            sb.append(PREFIX);
            sb.append(e.getMessage());
            sb.append(field.getName());
         }
      }
      sb.append(LINE);
      return sb.toString();
   }

   /**
    * Return String representation of class.
    *
    * @param myObj a class for representation
    * @return String representation of class
    */
   public static String objl(Object myObj) {
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
            Field myField = Log.getField(cl, fields[i].getName());
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
   public static String hex(byte[] data, int countPerLine) {
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
   public static String hex(byte[] data) {
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
   public static String xml(String xmlStr) {
      return xml(xmlStr, 2);
   }

   /**
    * Return readable representation of xml
    *
    * @param xmlStr      your xml data
    * @param indent xml identetion
    * @return readable representation
    */
   public static String xml(String xmlStr, int indent) {
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

}
