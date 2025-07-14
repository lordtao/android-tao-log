package ua.at.tsvetkov.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import ua.at.tsvetkov.util.logger.Log;
import ua.at.tsvetkov.util.logger.LogLong;
import ua.at.tsvetkov.util.logger.interceptor.Level;
import ua.at.tsvetkov.util.logger.ui.LogFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MSG = "TEST MESSAGE\nLINE 1\nLINE 2\nLINE 3";
    private static final String SHORT_MSG = "TEST MESSAGE";
    private static final String LONG_MSG = "TEST MESSAGE\nLINE 1\nLINE 2\nLINE 3" +
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
            "\n a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message a message 30";
    private TestObject testObject = new TestObject("A Test Object");
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private LogFragment logFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        logFragment = new LogFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameContent, logFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        runScope(item);
        return true;
    }

    private void runScope(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_log_quick: {
                runLogQuick();
                break;
            }
            case R.id.nav_log_common: {
                runLogCommons();
                break;
            }
            case R.id.nav_log_thread: {
                runLogThread();
                break;
            }
            case R.id.nav_log_arrays: {
                runLogArrays();
                break;
            }
            case R.id.nav_log_xml_hex: {
                runLogXmlHex();
                break;
            }
            case R.id.nav_log_objects: {
                runLogClassAndObjects();
                break;
            }
            case R.id.nav_log_long_common: {
                runLogLongCommons();
                break;
            }
            case R.id.nav_log_long_thread: {
                runLogLongThread();
                break;
            }
            case R.id.nav_log_long_arrays: {
                runLogLongArrays();
                break;
            }
            case R.id.nav_log_long_xml_hex: {
                runLongLogXmlHex();
                break;
            }
        }
        logFragment.scrollDown();
    }

    private void runLogQuick() {

        Log.v(Level.VERBOSE.name());
        Log.d(Level.DEBUG.name());
        Log.i(Level.INFO.name());
        Log.w(Level.WARNING.name());
        Log.e(Level.ERROR.name());
        Log.wtf(Level.WTF.name());

    }

    private void runLogCommons() {

        try {
            int i = 10 / 0;
        } catch (Exception e) {
            Log.e("Exception", e);
        }

        Exception ex = new Exception("Log Exception");

        Log.v(ex);
        Log.v(MSG);
        Log.v(MSG, ex);

        Log.d(ex);
        Log.d(MSG);
        Log.d(MSG, ex);

        Log.i(ex);
        Log.i(MSG);
        Log.i(MSG, ex);

        Log.w(ex);
        Log.w(MSG);
        Log.w(MSG, ex);

        Log.e(ex);
        Log.e(MSG);
        Log.e(MSG, ex);

        Log.wtf(ex);
        Log.wtf(MSG);
        Log.wtf(MSG, ex);

        RuntimeException rt = new RuntimeException();
        try {
            Log.rt(rt);
        } catch (RuntimeException e) {
            Log.v("Intercepted", e);
        }
        try {
            Log.rt(MSG, rt);
        } catch (RuntimeException e) {
            Log.v("Intercepted", e);
        }
    }

    private void runLogThread() {
        Thread th = new Thread("Boo Thread");
        th.start();

        Exception ex = new Exception("Thread Exception");

        Log.threadInfo();
        Log.threadInfo(MSG);
        Log.threadInfo(ex);
        Log.threadInfo(MSG, ex);
        Log.threadInfo(th, ex);

        Log.stackTrace();
        Log.stackTraceV(MSG);
        Log.stackTraceI(MSG);
        Log.stackTraceD(MSG);
        Log.stackTraceW(MSG);
        Log.stackTraceE(MSG);
        Log.stackTraceWTF(MSG);
    }

    private void runLogArrays() {
        int[] ints = new int[]{472834, 4235, 657, -1728, 0};
        Log.array(ints);
        double[] doubles = new double[]{472834, 4235, 657, -1728, 0};
        Log.array(doubles);
        long[] longs = new long[]{472834, 4235, 657, -1728, 0};
        Log.array(longs);
        float[] floats = new float[]{645.0f, 235, 57, -128, 0};
        Log.array(floats);
        boolean[] bools = new boolean[]{true, false, true};
        Log.array(bools);
        char[] chars = new char[]{'c', 'h', 'a', 'r', 's'};
        Log.array(chars);
        Object[] objects = new Object[]{"String object", new Object(), 'a', new Object(), 's'};
        Log.array(objects);

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "First");
        map.put(2, "Second");
        map.put(3, "Third");
        Log.map(map);

        ArrayList<String> list = new ArrayList<>();
        list.add("First");
        list.add("Second");
        list.add("Third");
        Log.list(list);
    }

    private void runLogXmlHex() {
        byte[] data = new byte[]{5, 65, 23, 34, 32, 12, 45, 35, 66, 120, 100, 93, 31, 111};
        Log.hex(data);
        Log.hex(data, 5);

        String xml = "<note>\n" +
                "<to>Alex</to>\n" +
                "<from>July</from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
        Log.xml(xml);
        Log.xml(xml, 3);
    }

    private void runLogClassAndObjects() {
        Log.classInfo(testObject);
        Log.objectInfo(testObject);
    }

    private void runLogLongCommons() {
        Exception ex = new Exception();

        LogLong.v(LONG_MSG);
        LogLong.v(LONG_MSG, ex);

        LogLong.d(LONG_MSG);
        LogLong.d(LONG_MSG, ex);

        LogLong.i(LONG_MSG);
        LogLong.i(LONG_MSG, ex);

        LogLong.w(LONG_MSG);
        LogLong.w(LONG_MSG, ex);

        LogLong.e(LONG_MSG);
        LogLong.e(LONG_MSG, ex);

        LogLong.wtf(LONG_MSG);
        LogLong.wtf(LONG_MSG, ex);

        RuntimeException rt = new RuntimeException();
        try {
            LogLong.rt(LONG_MSG, rt);
        } catch (RuntimeException e) {
            LogLong.v("Intercepted", e);
        }
    }

    private void runLogLongThread() {
        Exception ex = new Exception();

        Thread th = new Thread(LONG_MSG);
        th.start();

        LogLong.threadInfo();
        LogLong.threadInfo(LONG_MSG);
        LogLong.threadInfo(ex);
        LogLong.threadInfo(LONG_MSG, ex);
        LogLong.threadInfo(th, ex);

        LogLong.stackTrace();
        LogLong.stackTraceV(LONG_MSG);
        LogLong.stackTraceI(LONG_MSG);
        LogLong.stackTraceD(LONG_MSG);
        LogLong.stackTraceW(LONG_MSG);
        LogLong.stackTraceE(LONG_MSG);
        LogLong.stackTraceWTF(LONG_MSG);
    }

    private void runLogLongArrays() {
        Random random = new Random();

        int[] ints = new int[5000];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = random.nextInt();
        }
        LogLong.array(ints);

        double[] doubles = new double[5000];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = random.nextDouble();
        }
        LogLong.array(doubles);

        long[] longs = new long[5000];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = random.nextLong();
        }
        LogLong.array(longs);

        float[] floats = new float[5000];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = random.nextFloat();
        }
        LogLong.array(floats);

        boolean[] bools = new boolean[5000];
        for (int i = 0; i < bools.length; i++) {
            bools[i] = random.nextBoolean();
        }
        LogLong.array(bools);
        char[] chars = new char[5000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) random.nextInt(30);
        }
        LogLong.array(chars);

        Object[] objects = new Object[1000];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new Object();
        }
        LogLong.array(objects);

        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 500; i++) {
            map.put(i, SHORT_MSG);
        }
        LogLong.map(map);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            list.add(SHORT_MSG);
        }
        LogLong.list(list);
    }

    private void runLongLogXmlHex() {
        Random random = new Random();

        byte[] data = new byte[5000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) random.nextInt(60);
        }
        LogLong.hex(data);
        LogLong.hex(data, 20);

        StringBuilder sb = new StringBuilder("<note>\n");
        for (int i = 0; i < 500; i++) {
            sb
                    .append("<to")
                    .append(i)
                    .append(">Tove")
                    .append(i)
                    .append("</to")
                    .append(i)
                    .append(">\n");
        }
        sb.append("</note>");
        String xml = sb.toString();
        LogLong.xml(xml);
        LogLong.xml(xml, 3);
    }

}