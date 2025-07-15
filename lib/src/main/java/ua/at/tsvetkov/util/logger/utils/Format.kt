/******************************************************************************
 * Copyright (c) 2010 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 * Alexandr Tsvetkov
 *
 * Project:
 * TAO Log
 *
 * License agreement:
 *
 * 1. This code is published AS IS. Author is not responsible for any damage that can be
 * caused by any application that uses this code.
 * 2. Author does not give a garantee, that this code is error free.
 * 3. This code can be used in NON-COMMERCIAL applications AS IS without any special
 * permission from author.
 * 4. This code can be modified without any special permission from author IF AND ONLY IF
 * this license agreement will remain unchanged.
 * *****************************************************************************/
package ua.at.tsvetkov.util.logger.utils

import android.os.Build
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import ua.at.tsvetkov.util.logger.Log
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.lang.reflect.Field
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Prepare formatted string from different objects for visual printing in [Log] class
 *
 * @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:tsvetkov2010@gmail.com
 */
internal object Format {

    private val LOG_CLASS_NAME = Log::class.java.name

    const val MAX_TAG_LENGTH = 65
    const val MAGIC_SPACES_COUNT = 34
    const val PREFIX = '|'
    const val COLON = ':'
    const val HEX_FORM = "%02X "
    const val GROUP = "|Group:"
    const val PRIORITY = "|Priority:"
    const val ID = "|Id:"
    const val THREAD_NAME = "Thread Name:"
    const val HALF_LINE = "---------------------"
    const val ACTIVITY_CLASS = "android.app.Activity"
    const val DELIMITER_START = "· "
    const val DELIMITER =
        "············································································"
    const val CHAR_DELIMITER = '·'
    const val THROWABLE_DELIMITER_START = "‖ "
    const val THROWABLE_DELIMITER_PREFIX = "    "
    const val THROWABLE_DELIMITER =
        "============================================================================"
    const val NL = "\n"
    const val SPACE = ' '
    const val AT = "at "
    const val ARRAY = "Array"

    @Volatile
    var beforeTagSpacesCount: Int = 0

    init {
        beforeTagSpacesCount = getPackageNameLength() + MAGIC_SPACES_COUNT
    }

    private fun getPackageNameLength(): Int {
        var notFound = true
        var id = 0
        var appPackageName: String? = null

        val thisPackageName = Log::class.java.getPackage()!!.name
        val tr = Thread.currentThread().stackTrace
        while (notFound) {
            appPackageName = tr[id++].className
            if (appPackageName!!.contains(thisPackageName)) {
                notFound = false
            }
        }
        notFound = true
        while (notFound) {
            appPackageName = tr[id++].className
            if (!appPackageName!!.contains(thisPackageName)) {
                notFound = false
                appPackageName = appPackageName.substring(0, appPackageName.lastIndexOf('.'))
            }
        }
        return if (notFound) {
            0
        } else {
            appPackageName!!.length
        }
    }

    /**
     * Return String representation of map. Each item in new line.
     *
     * @param map a Map
     * @return String representation of map
     */
    @JvmStatic
    fun map(map: Map<*, *>?): String {
        if (map == null) {
            return "null"
        }
        var max = 0
        for ((key) in map) {
            val length = key.toString().length
            if (max < length) {
                max = length
            }
        }
        val sb = StringBuilder()
        val formatString = "%-" + max + "s = %s"
        for ((key, value) in map) {
            sb.append(String.format(formatString, key, value))
            sb.append(NL)
        }
        return sb.toString()
    }

    /**
     * Return String representation of list. Each item in new line.
     *
     * @param list a List
     * @return String representation of map
     */
    @JvmStatic
    fun list(list: List<*>?): String {
        if (list == null) {
            return "null"
        }
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item.toString())
            sb.append(NL)
        }
        return sb.toString()
    }

    /**
     * Return String representation of Objects array. Each item in new line.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun <T : Any> arrayT(array: Array<T>?): String {
        if (array == null) {
            return "null"
        }
        val list = listOf(*array)
        val sb = StringBuilder()
        var index = 0
        for (item in list) {
            sb.append('[')
            sb.append(index++)
            sb.append("] ")
            sb.append(item.javaClass.simpleName)
            sb.append(": ")
            sb.append(item.toString())
            sb.append(NL)
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: IntArray?): String {
        if (array == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append('[')
        for (i in array.indices) {
            sb.append(array[i])
            if (i < array.size - 1) {
                sb.append(',')
            } else {
                sb.append(']')
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: FloatArray?): String {
        if (array == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append('[')
        for (i in array.indices) {
            sb.append(array[i])
            if (i < array.size - 1) {
                sb.append(',')
            } else {
                sb.append(']')
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: BooleanArray?): String {
        if (array == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append('[')
        for (i in array.indices) {
            sb.append(array[i])
            if (i < array.size - 1) {
                sb.append(',')
            } else {
                sb.append(']')
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: CharArray?): String {
        if (array == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append('[')
        for (i in array.indices) {
            sb.append(array[i])
            if (i < array.size - 1) {
                sb.append(',')
            } else {
                sb.append(']')
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: DoubleArray?): String {
        if (array == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append('[')
        for (i in array.indices) {
            sb.append(array[i])
            if (i < array.size - 1) {
                sb.append(',')
            } else {
                sb.append(']')
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun array(array: LongArray?): StringBuilder {
        val sb = StringBuilder()
        if (array != null) {
            sb.append('[')
            for (i in array.indices) {
                sb.append(array[i])
                if (i < array.size - 1) {
                    sb.append(',')
                } else {
                    sb.append(']')
                }
            }
        }
        return sb
    }

    /**
     * Return String representation of array.
     *
     * @param array an array
     * @return String representation of array
     */
    @JvmStatic
    fun arrayString(array: Array<String>?): String {
        if (array == null) {
            return "null"
        }
        val list = listOf(*array)
        val sb = StringBuilder()
        var index = 0
        for (item in list) {
            sb.append('[')
            sb.append(index++)
            sb.append("] ")
            sb.append(item)
            sb.append(NL)
        }
        return sb.toString()
    }

    /**
     * Return String representation of Object. Each field in new line.
     *
     * @param objs a class for representation
     * @return String representation of class
     */
    @JvmStatic
    fun objectInfo(objs: Any?): String {
        if (objs == null) {
            return "null"
        }
        val cl = objs.javaClass
        val sb = StringBuilder()
        val fields = cl.declaredFields

        var max = 0
        for (field in fields) {
            val length = field.name.length
            if (max < length) {
                max = length
            }
        }
        val formatString = PREFIX + "%-" + max + "s = %s" + NL

        for (field in fields) {
            try {
                val myField = getField(cl, field.name)
                myField.isAccessible = true
                sb.append(String.format(formatString, field.name, myField.get(objs)))
            } catch (e: Exception) {
                sb.append(PREFIX)
                sb.append(e.message)
                sb.append(field.name)
            }
        }
        return sb.toString()
    }

    /**
     * Return String representation of class.
     *
     * @param myObj a class for representation
     * @return String representation of class
     */
    @JvmStatic
    fun classInfo(myObj: Any?): String {
        if (myObj == null) {
            return "null"
        }
        val cl = myObj.javaClass
        val fields = cl.declaredFields
        val sb = StringBuilder()
        sb.append(cl.simpleName)
        sb.append(" [")
        for (i in fields.indices) {
            try {
                val myField = getField(cl, fields[i].name)
                myField.isAccessible = true
                sb.append(fields[i].name)
                sb.append("=")
                sb.append(myField.get(myObj))
                if (fields.size != 1 && i < fields.size - 1) {
                    sb.append(", ")
                }
            } catch (e: Exception) {
                sb.append(PREFIX)
                sb.append(sb.append(e.message))
                sb.append(fields[i].name)
            }
        }
        sb.append("]")
        return sb.toString()
    }

    /**
     * Return readable representation of bytes array data like 0F CD AD.... Each countPerLine bytes will print in new line
     *
     * @param data         your bytes array data
     * @param countPerLine count byte per line
     * @return readable representation
     */
    @JvmStatic
    fun hex(data: ByteArray?, countPerLine: Int): StringBuilder {
        if (data == null) {
            Log.v("null")
        }
        val sb = StringBuilder()
        var count = 0
        for (element in data!!) {
            count++
            sb.append(String.format(HEX_FORM, element))
            if (count >= countPerLine) {
                count = 0
                sb.append(NL)
            }
        }
        return sb
    }

    /**
     * Return readable representation of bytes array data like 0F CD AD....
     *
     * @param data your bytes array data
     * @return readable representation
     */
    @JvmStatic
    fun hex(data: ByteArray?): StringBuilder {
        if (data == null) {
            Log.v("null")
        }
        val sb = StringBuilder(data!!.size * 3)
        for (element in data) {
            sb.append(String.format(HEX_FORM, element))
        }
        sb.trimToSize()
        return sb
    }

    /**
     * Return readable representation of xml
     *
     * @param xmlStr your xml data
     * @param indent xml identetion
     * @return readable representation
     */
    @JvmStatic
    @JvmOverloads
    fun xml(xmlStr: String?, indent: Int = 2): StringBuilder {
        if (xmlStr == null) {
            Log.v("null")
        }
        try {
            // Turn xml string into a document
            val document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(InputSource(ByteArrayInputStream(xmlStr!!.toByteArray(charset("utf-8")))))

            // Remove whitespaces outside tags
            document.normalize()
            val xPath = XPathFactory.newInstance().newXPath()
            val nodeList = xPath.evaluate(
                "//text()[normalize-space()='']",
                document,
                XPathConstants.NODESET
            ) as NodeList

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i)
                node.parentNode.removeChild(node)
            }

            // Setup pretty print options
            val transformerFactory = TransformerFactory.newInstance()
            //         transformerFactory.setAttribute("indent-number", indent);
            val transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                indent.toString()
            )
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")

            // Return pretty print xml string
            val stringWriter = StringWriter()
            transformer.transform(DOMSource(document), StreamResult(stringWriter))
            return StringBuilder(stringWriter.toString())
        } catch (e: Exception) {
            return if (!e.message.isNullOrEmpty()) {
                StringBuilder(e.message!!)
            } else {
                StringBuilder()
            }
        }
    }

    // ============================== Fragments ==============================

    @JvmStatic
    fun getFragmentsStackInfo(fm: FragmentManager, operation: String): String {
        val backStackCount = fm.backStackEntryCount
        val logs = mutableListOf<String>()
        logs.add(operation)
        var idx = backStackCount
        for(i in backStackCount - 1 downTo 0) {
            val entry = fm.getBackStackEntryAt(i)
            entry.name?.let{
                if(idx == backStackCount) {
                    logs.add( "   # ${idx--} ${entry.name} \uD83D\uDD1D")
                } else {
                    logs.add( "   # ${idx--} ${entry.name}")
                }
            }
        }

        return getFSString(logs.toTypedArray())
    }

    @JvmStatic
    private fun getFSString(array: Array<String?>): String {
        val sb = StringBuilder()
        for (i in array.indices) {
            sb.append(array[i])
            sb.append(NL)
        }
        return sb.toString()
    }

    // ============================ Private common methods ==============================

    @JvmStatic
    @Throws(NoSuchFieldException::class)
    fun getField(clazz: Class<*>, fieldName: String): Field {
        return try {
            clazz.getDeclaredField(fieldName)
        } catch (e: NoSuchFieldException) {
            val superClass = clazz.superclass
            if (superClass == null) {
                throw e
            } else {
                getField(superClass, fieldName)
            }
        }
    }

    fun getLocationContainer(): LocationContainer {
        var found = false
        val traces = Thread.currentThread().stackTrace.toMutableList()
        for (i in traces.indices) {
            try {
                if (found) {
                    if (!traces[i].className.startsWith(LOG_CLASS_NAME)) {
                        return LocationContainer(traces.drop(i))
                    }
                } else if (traces[i].className.startsWith(LOG_CLASS_NAME)) {
                    found = true
                }
            } catch (e: ClassNotFoundException) {
                android.util.Log.e("LOG", e.toString())
            }
        }
        return LocationContainer(traces)
    }

    fun getClassName(clazz: Class<*>?): String {
        return if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.simpleName)) {
                if (clazz.name.contains("$")) {
                    clazz.name.substring(
                        clazz.name.lastIndexOf('.') + 1,
                        clazz.name.lastIndexOf('$')
                    )
                } else {
                    clazz.simpleName
                }
            } else getClassName(clazz.enclosingClass)
        } else ""
    }

    fun findStackTraceElement(
        traces: Array<StackTraceElement>,
        startsFrom: String
    ): StackTraceElement? {
        var trace: StackTraceElement? = null
        for (i in traces.indices) {
            if (traces[i].className.startsWith(startsFrom)) {
                trace = traces[i]
                break
            }
        }
        return trace
    }

    @JvmStatic
    fun getFormattedMessage(data: LocationContainer, message: String?): StringBuilder {
        return getFormattedMessage(data, null, message, null)
    }

    @JvmStatic
    fun getFormattedMessage(
        data: LocationContainer,
        message: String,
        title: String?
    ): StringBuilder {
        return getFormattedMessage(data, null, message, title)
    }

    @JvmStatic
    fun getFormattedThrowable(data: LocationContainer, throwable: Throwable): StringBuilder {
        return getFormattedThrowable(data, null, throwable)
    }

    @JvmStatic
    @JvmOverloads
    fun getFormattedMessage(
        data: LocationContainer,
        stringBuilder: StringBuilder?,
        message: String? = null,
        title: String? = null
    ): StringBuilder {
        var lines = if (stringBuilder != null) {
            if (message != null) {
                stringBuilder.append(message)
            }
            Pattern.compile("\\n").split(stringBuilder)
        } else {
            message!!.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        val linesCount = getLinesCount(title, lines)
        lines = createLines(title, lines, linesCount)

        val sb = StringBuilder()
        sb.append(data.getLink())
        sb.append("::")
        data.appendMethodName(sb)
        sb.append('\n')
        appendLines(lines, sb)
        return sb
    }

    @JvmStatic
    fun getFormattedThrowable(
        data: LocationContainer,
        message: String?,
        throwable: Throwable
    ): StringBuilder {
        var lines = if (!message.isNullOrEmpty()) {
            (message + ("\n" + getThrowableMessage(throwable))).split("\\n".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        } else {
            getThrowableMessage(throwable).split("\\n".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        }

        val linesCount = getLinesCount(lines, throwable)
        lines = createLines(throwable, lines, linesCount)

        val sb = StringBuilder()
        sb.append(data.getLink())
        sb.append("::")
        data.appendMethodName(sb)
        sb.append('\n')
        appendLines(lines, sb)

        return sb
    }

    private fun getThrowableMessage(throwable: Throwable): String {
        return throwable.javaClass.name + ": " + throwable.message
    }

    private fun getLinesCount(title: String?, lines: Array<String>?): Int {
        var count = lines?.size ?: 0
        if (Log.isLogOutlined) {
            count += 2
        } else if (title != null) {
            count++
        }
        return count
    }

    private fun getLinesCount(lines: Array<String>?, throwable: Throwable?): Int {
        var count = lines?.size ?: 0
        if (Log.isLogOutlined) {
            count += 2
        }
        if (throwable != null) {
            count += throwable.stackTrace.size
        } else {
            count++
        }
        return count
    }

    private fun createLines(title: String?, lines: Array<String>, count: Int): Array<String> {
        val lns = Array(count) { "" }

        if (Log.isLogOutlined) {
            lns[0] = makeTitleLine(title, DELIMITER, CHAR_DELIMITER)
            lns[lns.size - 1] = DELIMITER
            for (i in lines.indices) {
                lns[i + 1] = DELIMITER_START + lines[i]
            }
        } else { // Not Boxed
            var shift = 0
            if (title != null) {
                lns[0] = title
                shift = 1
            }
            for (i in lines.indices) {
                lns[i + shift] = lines[i]
            }
        }

        return lns
    }

    @Suppress("SameParameterValue")
    private fun makeTitleLine(title: String?, separator: String, char: Char): String {
        val string: String = if (title == null) {
            separator
        } else {
            val longTitle = SPACE + title + SPACE
            val fillLength: Int = (separator.length + longTitle.length) / 2
            if (fillLength > 0) {
                val beginString = longTitle.padStart(fillLength, char)
                beginString.padEnd(separator.length, char)
            } else {
                longTitle
            }
        }
        return string
    }

    private fun createLines(
        throwable: Throwable?,
        lines: Array<String>?,
        count: Int
    ): Array<String> {
        val lns = Array(count) { "" }

        val linesCount = lines?.size ?: 0

        if (Log.isLogOutlined) {
            lns[0] = THROWABLE_DELIMITER
            lns[lns.size - 1] = THROWABLE_DELIMITER

            for (i in 0 until linesCount) {
                lns[i + 1] = THROWABLE_DELIMITER_START + lines!![i]
            }

            if (throwable == null) {
                lns[linesCount + 1] = THROWABLE_DELIMITER_START + "throwable == null"
            } else {
                val stack = throwable.stackTrace
                for (i in stack.indices) {
                    if (i == 0) {
                        lns[linesCount + 1 + i] =
                            THROWABLE_DELIMITER_START + AT + stack[i].toString()
                    } else {
                        lns[linesCount + 1 + i] =
                            THROWABLE_DELIMITER_START + THROWABLE_DELIMITER_PREFIX + AT + stack[i].toString()
                    }
                }
            }
        } else { // Not Boxed
            for (i in 0 until linesCount) {
                lns[i] = lines!![i]
            }
            if (throwable == null) {
                lns[0] = "throwable == null"
            } else {
                val stack = throwable.stackTrace
                for (i in stack.indices) {
                    if (i == 0) {
                        lns[linesCount + i] = stack[i].toString()
                    } else {
                        lns[linesCount + i] = THROWABLE_DELIMITER_PREFIX + stack[i].toString()
                    }
                }
            }
        }

        return lns
    }

    private fun appendLines(lines: Array<String>, sb: StringBuilder) {
        for (i in lines.indices) {
            sb.append(lines[i])
            sb.append(NL)
        }
    }

    // ==================== Stack trace ======================

    @JvmStatic
    fun addStackTrace(sb: StringBuilder, throwable: Throwable) {
        val traces = throwable.stackTrace
        addStackTrace(sb, traces)
    }

    @JvmStatic
    fun addStackTrace(sb: StringBuilder, thread: Thread) {
        val traces = thread.stackTrace
        addStackTrace(sb, traces)
    }

    @JvmStatic
    fun addStackTrace(sb: StringBuilder, traces: Array<StackTraceElement>) {
        val reduced = traces.drop(4).dropLastWhile { it.fileName!!.contentEquals("Log.kt") }
        for (element in reduced) {
            sb.append(THROWABLE_DELIMITER_PREFIX)
            sb.append(AT)
            sb.append(element.toString())
            sb.append('\n')
        }
    }

    /**
     * @param thread thread for Logged
     * @return filled StringBuilder for next filling
     */
    @JvmStatic
    fun addThreadInfo(sb: StringBuilder, thread: Thread?) {
        if (thread != null) {
            val id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                thread.threadId()
            } else {
                @Suppress("DEPRECATION")
                thread.id
            }
            val name = thread.name
            val priority = thread.priority.toLong()
            sb.append(THREAD_NAME)
            sb.append(name)
            sb.append(ID)
            sb.append(id)
            sb.append(PRIORITY)
            sb.append(priority)
            sb.append(GROUP)
            val group = thread.threadGroup
            if (group != null) {
                val groupName = group.name
                sb.append(groupName)
            } else {
                sb.append("null")
            }
        } else {
            sb.append("The thread == null")
        }
    }

    @JvmStatic
    fun addMessage(sb: StringBuilder, message: String?) {
        if (!message.isNullOrEmpty()) {
            sb.append(message)
            sb.append(NL)
        }
    }

    data class LocationContainer(val traces: List<StackTraceElement>) {

        val tag: String
        private val className: String
        private val methodName: String
        private val lineNumber: Int
        var stackTraceNumber: Int = 0

        init {
            if (traces.isEmpty()) {
                tag = "<no-tag>"
                className = UNDEFINED
                methodName = UNDEFINED
                lineNumber = 0
            } else {
                var notZeroLineNumberOffset = 0
                var number = 0
                while (number == 0) {
                    number = traces[notZeroLineNumberOffset++].lineNumber
                }
                className = traces[0].fileName
                methodName = traces[0].methodName
                tag = className.substringBeforeLast('.')
                lineNumber = number
            }
        }

        fun isKotlin() = className.endsWith(".kt", true)

        fun addLinkTo(sb: StringBuilder) {
            sb.append('(')
            sb.append(className)
            sb.append(COLON)
            sb.append(lineNumber)
            sb.append(')')
        }

        fun appendLink(sb: StringBuilder) {
            sb.append(getLink())
        }

        fun getLink(stackTraceDepth: Int = stackTraceNumber): String {
            val sb = StringBuilder()
            when (stackTraceDepth) {
                0 -> {
                    sb.append('(')
                    sb.append(className)
                    sb.append(COLON)
                    sb.append(lineNumber)
                    sb.append(')')
                }

                in traces.indices -> {
                    var notZeroLineNumberOffset = 0
                    var number = 0
                    while (number == 0) {
                        number = traces[stackTraceDepth + notZeroLineNumberOffset++].lineNumber
                    }
                    sb.append('(')
                    sb.append(traces[stackTraceDepth].fileName)
                    sb.append(COLON)
                    sb.append(number)
                    sb.append(')')
                }

                else -> {
                    sb.append("No stacktrace ")
                    sb.append(stackTraceDepth)
                }
            }
            return sb.toString()
        }

        fun getMethodName(): String {
            return if (stackTraceNumber == 0) {
                methodName
            } else {
                traces[stackTraceNumber].methodName
            }
        }

        fun appendMethodName(sb: StringBuilder) {
            sb.append(getMethodName())
            sb.append("()")
        }

        companion object {
            const val UNDEFINED = "Undefined"
        }
    }
}