package ua.at.tsvetkov.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.at.tsvetkov.util.Log;

public class MainActivity extends AppCompatActivity {

   private static final String MSG = "TEST MESSAGE LINE 1\nLINE 2";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

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

      Exception ex = new Exception("Test Exception");

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

      Thread th = new Thread("Test Thread");
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
      Log.xml(xml,3);
   }
}
