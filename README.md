android-tao-log
================

IN DEVELOPMENT NOW!!!

Tiny, lightweight and informative logger for Android.

Download from Bintray: [ ![Download](https://api.bintray.com/packages/lordtao/maven/android-tao-log/images/download.svg) ](https://bintray.com/lordtao/maven/android-tao-log/_latestVersion)

Code example:

```java
   Log.v("Verbose");
   Log.d("Debug");
   Log.i("Info");
   Log.e("Error");
   try{
       int i = 10/0;
   } catch (Exception e) {
       Log.e("Some exception", e);
   }
   try{
       int i = 10/0;
   } catch (Exception e) {
       Log.rt("RuntimeException is not handled by Log.rt()", e);
   }
```

You'll get in your LogCat the lines like below. 
Clicking on the tag brings you to log into the source code of the class which was caused by the logger:

![Image of LogCat example](log_example.png)


Add android-tao-core to your project
----------------------------
Android tao log lib is available on Bintray. Please ensure that you are using the latest versions by [ ![Download](https://api.bintray.com/packages/lordtao/maven/android-tao-log/images/download.svg) ](https://bintray.com/lordtao/maven/android-tao-log/_latestVersion)

Gradle dependency for your Android app:

add to general build.gradle
```
buildscript {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/lordtao/maven"
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
    }
}

allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/lordtao/maven"
        }
    }
}
```
add to your dependencies in build.gradle
```
    compile 'ua.at.tsvetkov:taolog:1.3.5'
```
