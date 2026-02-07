/******************************************************************************
 * Copyright (c) 2010-2025 Alexandr Tsvetkov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * https://opensource.org/license/mit
 *
 * * Contributors:
 * Alexandr Tsvetkov - initial API and implementation
 *
 * Project:
 * TAO Log
 *
 * License agreement:
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ua.at.tsvetkov.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.at.tsvetkov.demo.databinding.ActivityMainBinding
import ua.at.tsvetkov.util.logger.Log
import ua.at.tsvetkov.util.logger.LogLong
import ua.at.tsvetkov.util.logger.PacketLog
import ua.at.tsvetkov.util.logger.interceptor.Level
import ua.at.tsvetkov.util.logger.ui.LogFragment
import java.util.*

/**
 * Created by Alexandr Tsvetkov on 06.12.2017.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var binding: ActivityMainBinding

    private val testObject = TestObject("A Test Object")
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var logFragment: LogFragment
    private lateinit var testFragment1: TestFragment1
    private lateinit var testFragment2: TestFragment2
    private lateinit var testFragment3: TestFragment3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        logFragment = LogFragment()
        testFragment1 = TestFragment1()
        testFragment2 = TestFragment2()
        testFragment3 = TestFragment3()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frameContent, logFragment)
            .commit()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        runScope(item)
        return true
    }

    private fun runScope(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_log_quick -> runLogQuick()
            R.id.nav_log_common -> runLogCommons()
            R.id.nav_log_thread -> runLogThread()
            R.id.nav_log_arrays -> runLogArrays()
            R.id.nav_log_xml_hex -> runLogXmlHex()
            R.id.nav_log_objects -> runLogClassAndObjects()
            R.id.nav_log_long_common -> runLogLongCommons()
            R.id.nav_log_long_thread -> runLogLongThread()
            R.id.nav_log_long_arrays -> runLogLongArrays()
            R.id.nav_log_long_xml_hex -> runLongLogXmlHex()
            R.id.nav_log_packet_log -> runPacketLog()
            R.id.nav_log_component_activity -> runActivity()
            R.id.nav_log_component_fragment -> runFragment()
        }
        logFragment.scrollDown()
    }

    private fun runLogQuick() {
        Log.v(Level.VERBOSE.name)
        Log.d(Level.DEBUG.name)
        Log.i(Level.INFO.name)
        Log.w(Level.WARNING.name)
        Log.e(Level.ERROR.name)
        Log.wtf(Level.WTF.name)
    }

    private fun runLogCommons() {
        try {
           val i = 10 / 0
        } catch (e: Exception) {
            Log.e("Exception", e)
        }
        val ex = Exception("Log Exception")
        Log.v(ex)
        Log.v(MSG)
        Log.v(MSG, ex)
        Log.d(ex)
        Log.d(MSG)
        Log.d(MSG, ex)
        Log.i(ex)
        Log.i(MSG)
        Log.i(MSG, ex)
        Log.w(ex)
        Log.w(MSG)
        Log.w(MSG, ex)
        Log.e(ex)
        Log.e(MSG)
        Log.e(MSG, ex)
        Log.wtf(ex)
        Log.wtf(MSG)
        Log.wtf(MSG, ex)
        val rt = RuntimeException()
        try {
            Log.rt(rt)
        } catch (e: RuntimeException) {
            Log.v("Intercepted", e)
        }
        try {
            Log.rt(MSG, rt)
        } catch (e: RuntimeException) {
            Log.v("Intercepted", e)
        }
    }

    private fun runLogThread() {
        val th = Thread("Test Thread")
        th.start()
        val ex = Exception("Thread Exception")
        Log.threadInfo()
        Log.threadInfo(MSG)
        Log.threadInfo(ex)
        Log.threadInfo(MSG, ex)
        Log.threadInfo(th, ex)
        Log.stackTrace()
        Log.stackTraceV(MSG)
        Log.stackTraceI(MSG)
        Log.stackTraceD(MSG)
        Log.stackTraceW(MSG)
        Log.stackTraceE(MSG)
        Log.stackTraceWTF(MSG)
    }

    private fun runLogArrays() {
        val ints = intArrayOf(472834, 4235, 657, -1728, 0)
        Log.array(ints)
        val doubles = doubleArrayOf(472834.0, 4235.0, 657.0, -1728.0, 0.0)
        Log.array(doubles)
        val longs = longArrayOf(472834, 4235, 657, -1728, 0)
        Log.array(longs)
        val floats = floatArrayOf(645.0f, 235f, 57f, -128f, 0f)
        Log.array(floats)
        val bools = booleanArrayOf(true, false, true)
        Log.array(bools)
        val chars = charArrayOf('c', 'h', 'a', 'r', 's')
        Log.array(chars)
        val objects = arrayOf("String object", Any(), 'a', Any(), 's')
        Log.array(objects)
        val map: MutableMap<Int?, String?> = HashMap()
        map[1] = "First"
        map[2] = "Second"
        map[3] = "Third"
        Log.map(map)
        val list = ArrayList<String?>()
        list.add("First")
        list.add("Second")
        list.add("Third")
        Log.list(list)
    }

    private fun runLogXmlHex() {
        val data = byteArrayOf(5, 65, 23, 34, 32, 12, 45, 35, 66, 120, 100, 93, 31, 111)
        Log.hex(data)
        Log.hex(data, 5)
        val xml = """
            <note>
            <to>Alex</to>
            <from>July</from>
            <heading>Reminder</heading>
            <body>Don't forget me this weekend!</body>
            </note>
            """.trimIndent()
        Log.xml(xml)
        Log.xml(xml, 3)
    }

    private fun runLogClassAndObjects() {
        Log.classInfo(testObject)
        Log.objectInfo(testObject)
    }

    private fun runLogLongCommons() {
        val ex = Exception()
        LogLong.v(LONG_MSG)
        LogLong.v(LONG_MSG, ex)
        LogLong.d(LONG_MSG)
        LogLong.d(LONG_MSG, ex)
        LogLong.i(LONG_MSG)
        LogLong.i(LONG_MSG, ex)
        LogLong.w(LONG_MSG)
        LogLong.w(LONG_MSG, ex)
        LogLong.e(LONG_MSG)
        LogLong.e(LONG_MSG, ex)
        LogLong.wtf(LONG_MSG)
        LogLong.wtf(LONG_MSG, ex)
        val rt = RuntimeException()
        try {
            LogLong.rt(LONG_MSG, rt)
        } catch (e: RuntimeException) {
            LogLong.v("Intercepted", e)
        }
    }

    private fun runLogLongThread() {
        val ex = Exception()
        val th = Thread(LONG_MSG)
        th.start()
        LogLong.threadInfo()
        LogLong.threadInfo(LONG_MSG)
        LogLong.threadInfo(ex)
        LogLong.threadInfo(LONG_MSG, ex)
        LogLong.threadInfo(th, ex)
        LogLong.stackTrace()
        LogLong.stackTraceV(LONG_MSG)
        LogLong.stackTraceI(LONG_MSG)
        LogLong.stackTraceD(LONG_MSG)
        LogLong.stackTraceW(LONG_MSG)
        LogLong.stackTraceE(LONG_MSG)
        LogLong.stackTraceWTF(LONG_MSG)
    }

    private fun runLogLongArrays() {
        val random = Random()
        val ints = IntArray(5000)
        for (i in ints.indices) {
            ints[i] = random.nextInt()
        }
        LogLong.array(ints)
        val doubles = DoubleArray(5000)
        for (i in doubles.indices) {
            doubles[i] = random.nextDouble()
        }
        LogLong.array(doubles)
        val longs = LongArray(5000)
        for (i in longs.indices) {
            longs[i] = random.nextLong()
        }
        LogLong.array(longs)
        val floats = FloatArray(5000)
        for (i in floats.indices) {
            floats[i] = random.nextFloat()
        }
        LogLong.array(floats)
        val bools = BooleanArray(5000)
        for (i in bools.indices) {
            bools[i] = random.nextBoolean()
        }
        LogLong.array(bools)
        val chars = CharArray(5000)
        for (i in chars.indices) {
            chars[i] = random.nextInt(30).toChar()
        }
        LogLong.array(chars)
        val objects = arrayOf<Any>(100)
        for (i in objects.indices) {
            objects[i] = Any()
        }
        LogLong.array(objects)
        val map: MutableMap<Int, String> = HashMap()
        for (i in 0..499) {
            map[i] = SHORT_MSG
        }
        LogLong.map(map)
        val list = ArrayList<String>()
        for (i in 0..499) {
            list.add(SHORT_MSG)
        }
        LogLong.list(list)
    }

    private fun runLongLogXmlHex() {
        val random = Random()
        val data = ByteArray(5000)
        for (i in data.indices) {
            data[i] = random.nextInt(60).toByte()
        }
        LogLong.hex(data)
        LogLong.hex(data, 20)
        val sb = StringBuilder("<note>\n")
        for (i in 0..499) {
            sb
                .append("<to")
                .append(i)
                .append(">Tove")
                .append(i)
                .append("</to")
                .append(i)
                .append(">\n")
        }
        sb.append("</note>")
        val xml = sb.toString()
        LogLong.xml(xml)
        LogLong.xml(xml, 3)
    }

    private fun runPacketLog() {
        val packet = PacketLog()
        packet.logI("PacketLog test started")
        packet.logD("This is a debug message")
        packet.logW("This is a warning")
        packet.logE("This is an error")
        packet.logV("And a verbose message")
        packet.printLogs("PacketLog Test")
    }

    private fun runActivity() {
        val intent = Intent(this, TestActivity::class.java)
        startActivity(intent)
    }

    private fun runFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContent, testFragment1)
            .addToBackStack("TestFragment1")
            .commit()

        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            withContext(Dispatchers.Main) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContent, testFragment2)
                    .addToBackStack("TestFragment2")
                    .commit()
            }
            delay(500)
            withContext(Dispatchers.Main) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContent, testFragment3)
                    .addToBackStack("TestFragment3")
                    .commit()
            }
            Log.i("You can see changes in the fragment stack in your LogCat")
        }
    }

    companion object {
        private const val MSG = "TEST MESSAGE\nLINE 1\nLINE 2\nLINE 3"
        private const val SHORT_MSG = "TEST MESSAGE"
        private const val LONG_MSG = "TEST MESSAGE\nLINE 1\nLINE 2\nLINE 3" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 1" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 2" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 3" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 4" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 5" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 6" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 7" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 8" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 9" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 10" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 11" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 12" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 13" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 14" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 15" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 16" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 17" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 18" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 19" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 20" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 21" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 22" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 23" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 24" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 25" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 26" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 27" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 28" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 29" +
                "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 30"
    }
}
