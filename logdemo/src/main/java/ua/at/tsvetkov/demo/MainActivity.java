package ua.at.tsvetkov.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ua.at.tsvetkov.util.Log;
import ua.at.tsvetkov.util.LongLog;
import ua.at.tsvetkov.util.interceptor.LogToFileInterceptor;

public class MainActivity extends AppCompatActivity {

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

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      final LogToFileInterceptor interceptor = new LogToFileInterceptor( this);

      Log.addInterceptor(interceptor);

      runLogExamples();

      runFragmentStackLoggerExample();

      runLongLogExamples();

      Test t = new Test("TTTTTT");
      t.getName();

      findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            interceptor.shareZippedLog(MainActivity.this);
         }
      });

   }

   private void runLogExamples() {
      // Code example

      Log.v("Verbose");
      Log.d("Debug");
      Log.i("Info");
      Log.e("Error");
      try {
         int i = 10 / 0;
      } catch (Exception e) {
         Log.e("Some exception", e);
      }

      // Logging tests

      Exception ex = new Exception("Boo Exception");

      Log.v(ex);
      Log.v(MSG);
      Log.v(this, MSG);
      Log.v(MSG, ex);
      Log.v(this, MSG, ex);

      Log.d(ex);
      Log.d(MSG);
      Log.d(this, MSG);
      Log.d(MSG, ex);
      Log.d(this, MSG, ex);

      Log.i(ex);
      Log.i(MSG);
      Log.i(this, MSG);
      Log.i(MSG, ex);
      Log.i(this, MSG, ex);

      Log.w(ex);
      Log.w(MSG);
      Log.w(this, MSG);
      Log.w(MSG, ex);
      Log.w(this, MSG, ex);

      Log.e(ex);
      Log.e(MSG);
      Log.e(this, MSG);
      Log.e(MSG, ex);
      Log.e(this, MSG, ex);

      Log.wtf(ex);
      Log.wtf(MSG);
      Log.wtf(this, MSG);
      Log.wtf(MSG, ex);
      Log.wtf(this, MSG, ex);

      Thread th = new Thread("Boo Thread");
      th.start();

      Log.threadInfo();
      Log.threadInfo(MSG);
      Log.threadInfo(ex);
      Log.threadInfo(MSG, ex);
      Log.threadInfo(th, ex);

      Log.stackTrace();
      Log.stackTrace(MSG);

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
      map.put(3, "Fird");
      Log.map(map);

      ArrayList<String> list = new ArrayList<>();
      list.add("First");
      list.add("Second");
      list.add("Fird");
      Log.list(list);

      Log.objl(list);
      Log.objn(map);

      byte[] data = new byte[]{5, 65, 23, 34, 32, 12, 45, 35, 66, 120, 100, 93, 31, 111};
      Log.hex(data);
      Log.hex(data, 5);

      String xml = "<note>\n" +
              "<to>Tove</to>\n" +
              "<from>Jani</from>\n" +
              "<heading>Reminder</heading>\n" +
              "<body>Don't forget me this weekend!</body>\n" +
              "</note>";
      Log.xml(xml);
      Log.xml(xml, 3);

   }

   private void runLongLogExamples() {
      // Code example

      // LongLogging tests

      Random random = new Random();

      LongLog.v(LONG_MSG);
      try {
         int i = 10 / 0;
      } catch (Exception e) {
         LongLog.e(LONG_MSG, e);
      }

      Exception ex = new Exception(LONG_MSG);

      LongLog.v(LONG_MSG);
      LongLog.v(this, LONG_MSG);
      LongLog.v(LONG_MSG, ex);
      LongLog.v(this, LONG_MSG, ex);

      LongLog.d(LONG_MSG);
      LongLog.d(this, LONG_MSG);
      LongLog.d(LONG_MSG, ex);
      LongLog.d(this, LONG_MSG, ex);

      LongLog.i(LONG_MSG);
      LongLog.i(this, LONG_MSG);
      LongLog.i(LONG_MSG, ex);
      LongLog.i(this, LONG_MSG, ex);

      LongLog.w(LONG_MSG);
      LongLog.w(this, LONG_MSG);
      LongLog.w(LONG_MSG, ex);
      LongLog.w(this, LONG_MSG, ex);

      LongLog.e(LONG_MSG);
      LongLog.e(this, LONG_MSG);
      LongLog.e(LONG_MSG, ex);
      LongLog.e(this, LONG_MSG, ex);

      LongLog.wtf(LONG_MSG);
      LongLog.wtf(this, LONG_MSG);
      LongLog.wtf(LONG_MSG, ex);
      LongLog.wtf(this, LONG_MSG, ex);

      Thread th = new Thread(LONG_MSG);
      th.start();

      LongLog.threadInfo();
      LongLog.threadInfo(LONG_MSG);
      LongLog.threadInfo(ex);
      LongLog.threadInfo(LONG_MSG, ex);
      LongLog.threadInfo(th, ex);

      LongLog.stackTrace();
      LongLog.stackTrace(LONG_MSG);

      RuntimeException rt = new RuntimeException();
      try {
         LongLog.rt(LONG_MSG, rt);
      } catch (RuntimeException e) {
         LongLog.v("Intercepted", e);
      }

      int[] ints = new int[5000];
      for (int i = 0; i < ints.length; i++) {
         ints[i] = random.nextInt();
      }
      LongLog.array(ints);

      double[] doubles = new double[5000];
      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = random.nextDouble();
      }
      LongLog.array(doubles);

      long[] longs = new long[5000];
      for (int i = 0; i < longs.length; i++) {
         longs[i] = random.nextLong();
      }
      LongLog.array(longs);

      float[] floats = new float[5000];
      for (int i = 0; i < floats.length; i++) {
         floats[i] = random.nextFloat();
      }
      LongLog.array(floats);

      boolean[] bools = new boolean[5000];
      for (int i = 0; i < bools.length; i++) {
         bools[i] = random.nextBoolean();
      }
      LongLog.array(bools);
      char[] chars = new char[5000];
      for (int i = 0; i < chars.length; i++) {
         chars[i] = (char) random.nextInt(30);
      }
      LongLog.array(chars);

      Object[] objects = new Object[1000];
      for (int i = 0; i < objects.length; i++) {
         objects[i] = new Object();
      }
      LongLog.array(objects);

      Map<Integer, String> map = new HashMap<>();
      for (int i = 0; i < 500; i++) {
         map.put(i, SHORT_MSG);
      }
      LongLog.map(map);

      ArrayList<String> list = new ArrayList<>();
      for (int i = 0; i < 500; i++) {
         list.add(SHORT_MSG);
      }
      LongLog.list(list);

      LongLog.objl(list);
      LongLog.objn(list);

      byte[] data = new byte[5000];
      for (int i = 0; i < data.length; i++) {
         data[i] = (byte) random.nextInt(60);
      }
      LongLog.hex(data);
      LongLog.hex(data, 20);

      StringBuilder sb = new StringBuilder("<note>\n");
      for (int i = 0; i < 500; i++) {
         sb.append("<to" + i + ">Tove" + i + "</to" + i + ">\n");
      }
      sb.append("</note>");
      String xml = sb.toString();
      LongLog.xml(xml);
      LongLog.xml(xml, 3);

   }

   private void runFragmentStackLoggerExample() {
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.content, new Fragment())
              .addToBackStack("Fragment New")
              .commit();

      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               Log.e(e);
            }
            replaceFragment();
         }
      }).start();
   }

   private void replaceFragment() {
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            TestFragment fr = new TestFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, fr)
                    .addToBackStack("TestFragment")
                    .commit();
            popFragment();
         }
      });
   }

   private void popFragment() {
      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               Log.e(e);
            }
            getSupportFragmentManager().popBackStack();
         }
      }).start();
   }

}
