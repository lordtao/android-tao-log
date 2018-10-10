package ua.at.tsvetkov.util.interceptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ua.at.tsvetkov.util.Log;

public class LogToFileInterceptor extends LogInterceptor {

    private static final String TAG = "LogInterceptor";
    private static final Executor executor = Executors.newFixedThreadPool(1);
    private static final int BUFFER_SIZE = 8192;
    private final Context context;

    private String filePath;
    private String fileName;
    private String extension;
    private File file;

    /**
     * Create the Log Interceptor which save a log messages to file (default dir - context.getCacheDir() file Log.txt).
     * You can share the zipped log with help of any external applications. For example by email, google drive and etc.
     *
     * @param context
     */
    public LogToFileInterceptor(Context context) {
        this.context = context;
        setFileName(context.getCacheDir().getAbsolutePath(),
                "Log",
                "txt");
    }

    @Override
    public void log(Level level, String tag, String msg, @Nullable Throwable th) {
        if (file == null) {
            Log.w(TAG, "Log file is not set");
            return;
        }
        final String message = new Date().toString() + " " + level + " " + msg;
        writeAsync(message);
    }

    /**
     * Set log file which replace the defaults
     *
     * @param filePath
     * @param fileName
     * @param extension
     */
    public void setFileName(String filePath, String fileName, String extension) {
        if (this.file != null) {
            this.file.delete();
        }
        this.filePath = filePath;
        this.fileName = fileName;
        this.extension = extension;
        this.file = new File(getLogFileName());
        this.file.delete();

        Log.i("Created new log file " + file.toString());
        String header = createHeader();
        writeAsync(header);
    }

    @NonNull
    public String getLogFileName() {
        return filePath + File.separator + fileName + '.' + extension;
    }

    public String getZipFileName() {
        return context.getExternalCacheDir() + File.separator + fileName + ".zip";
    }

    /**
     * Clear current log file
     */
    public void clear() {
        if (file != null) {
            file.delete();
        } else {
            Log.w(TAG, "Log file name is not set");
        }
    }

    /**
     * Zip the current log file and send it by email
     *
     * @param activity
     */
    @UiThread
    public void shareZippedLog(final Activity activity) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                logToZipFile(new ZipListener() {
                    @Override
                    public void onZipCreated(final String zipFileName) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                File file = new File(zipFileName);
                                Uri uri = Uri.fromFile(file);
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("*/*");
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Zipped log");
                                activity.startActivity(Intent.createChooser(intent, "Share zipped log file"));
                            }

                        });
                    }

                    @Override
                    public void onZipError(String zipFileName, Exception e) {
                        Log.e("Can't create " + zipFileName, e);
                    }
                });

            }

        });
    }

    @WorkerThread
    public void logToZipFile(ZipListener listener) {
        zip(getLogFileName(), getZipFileName(), listener);
    }

    @NonNull
    private String createHeader() {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String version = "";
        int verCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(appInfo.packageName, 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(e);
        }
        return "Application name: " + appInfo.name + "\n" +
                "Package: " + appInfo.packageName + "\n" +
                "Version: " + version + "\n" +
                "Version code: " + verCode + "\n" +
                "===========================================================\n"
                ;
    }

    private void zip(String file, String zipFileName, ZipListener listener) {
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName)));
            byte data[] = new byte[BUFFER_SIZE];
            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, BUFFER_SIZE);
            try {
                ZipEntry entry = new ZipEntry(fileName + '.' + extension);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
            } catch (Exception e) {
                listener.onZipError(zipFileName, e);
            } finally {
                origin.close();
            }
        } catch (Exception e) {
            listener.onZipError(zipFileName, e);
        } finally {
            try {
                out.close();
                Log.i(file + " zipped to " + zipFileName);
                listener.onZipCreated(zipFileName);
            } catch (IOException e) {
                listener.onZipError(zipFileName, e);
            }
        }
    }

    private void writeAsync(final String message) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                writeLog(message);
            }

        });
    }

    private File writeLog(String message) {
        BufferedWriter bw = null;
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            bw = new BufferedWriter(fileWriter);
            bw.write(message);
            bw.newLine();
            bw.flush();
            return file;
        } catch (IOException e) {
            Log.e(TAG, "WriteLog", e);
            return null;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.e(TAG, e);
                }
            }
        }
    }

    public interface ZipListener {

        public void onZipCreated(String zipFileName);

        public void onZipError(String zipFileName, Exception e);

    }

}
