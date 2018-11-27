package ua.at.tsvetkov.demo;

import ua.at.tsvetkov.util.Log;

public class ClassA {

   public static void call(Object obj, String msa, Throwable th) {
      ClassB.call(obj, msa, th);
   }

   public static void call(Object obj, String msa) {
      ClassB.call(obj, msa);
   }

}

class ClassB {
   public static void call(Object obj, String msa, Throwable th) {
      ClassC.call(obj, msa, th);
   }

   public static void call(Object obj, String msa) {
      ClassC.call(obj, msa);
   }
}

class ClassC {
   public static void call(Object obj, String msa, Throwable th) {
      ClassD.call(obj, msa, th);
   }

   public static void call(Object obj, String msa) {
      ClassD.call(obj, msa);
   }
}

class ClassD {

   private static int i = 0;

   public static void call(Object obj, String msa, Throwable th) {
      if(i++ > 50) {
         Log.i(obj, msa, th);
      }else {
         ClassD.call(obj, msa, th);
      }
   }

   public static void call(Object obj, String msa) {
      if(i++ > 50) {
         Log.i(obj, msa);
      }else {
         ClassD.call(obj, msa);
      }
   }

}