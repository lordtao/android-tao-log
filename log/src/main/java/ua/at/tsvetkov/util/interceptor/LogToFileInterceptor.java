package ua.at.tsvetkov.util.interceptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ua.at.tsvetkov.util.Level;
import ua.at.tsvetkov.util.Log;

public class LogToFileInterceptor extends LogInterceptor {

    public static final String LOG_SEPARATOR = "\u2063";
    public static final String LOG_END_OF_MESSAGE = "\u200B";

    private static final String INDEX_FILE_EXT = "ids";
    private static final Executor executor = Executors.newFixedThreadPool(1);
    private static final int BUFFER_SIZE = 8192;
    private final Context context;
    private final boolean isIndexed;

    private String filePath;
    private String fileName;
    private String extension;
    private File file;
    private File indexFile;
    private long index;

    /**
     * Create the Log Interceptor which save a log messages to file (default dir - context.getCacheDir() file Log.txt).
     * You can share the zipped log with help of any external applications. For example by email, google drive and etc.
     *
     * @param context
     */
    public LogToFileInterceptor(Context context) {
        this(context, false);
    }

    /**
     * Create the Log Interceptor which save a log messages to file (default dir - context.getCacheDir() file Log.txt).
     * You can share the zipped log with help of any external applications. For example by email, google drive and etc.
     * Add index file with log line numbers
     *
     * @param context
     * @param isIndexed is need to create index file
     */
    public LogToFileInterceptor(Context context, boolean isIndexed) {
        this.context = context;
        this.isIndexed = isIndexed;
        setFileName(context.getCacheDir().getAbsolutePath(),
                "Log",
                "txt");
    }

    @Override
    public void log(Level level, String tag, String msg, Throwable th) {
        if (file == null) {
            Log.w(getClass().getName(), "Log file is not set");
            return;
        }
        final String message = new Date().toString() + " " + LOG_SEPARATOR + level + " " + LOG_SEPARATOR + tag + ": " + LOG_SEPARATOR + msg + LOG_END_OF_MESSAGE + '\n';
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

        index = 0;
        if (isIndexed) {
            if (this.indexFile != null) {
                this.indexFile.delete();
            }
            this.indexFile = new File(getIndexFileName());
            this.indexFile.delete();
            Log.i("Created new log indexes file " + indexFile.toString());
        }

        String header = createHeader();
        writeAsync(header);
    }

    public String getLogFileName() {
        return filePath + File.separator + fileName + '.' + extension;
    }

    public String getZipFileName() {
        return context.getExternalCacheDir() + File.separator + fileName + ".zip";
    }

    public String getIndexFileName() {
        return filePath + File.separator + fileName + '.' + INDEX_FILE_EXT;
    }

    /**
     * Clear current log file
     */
    public void clear() {
        if (file != null) {
            file.delete();
        }
        if (indexFile != null) {
            indexFile.delete();
        }
    }

    public void startRecord() {
        setEnabled();
    }

    public void stopRecord() {
        setDisabled();
    }

    /**
     * Zip the current log file and send it by email
     *
     * @param activity
     */
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

    public void logToZipFile(ZipListener listener) {
        zip(getLogFileName(), getZipFileName(), listener);
    }

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
                if (isIndexed) {
                    writeIndex();
                }
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
            bw.flush();
            index = message.length() * 2;
            return file;
        } catch (IOException e) {
            Log.e(getClass().getName(), "WriteLog", e);
            return null;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.e(getClass().getName(), e);
                }
            }
        }
    }

    private File writeIndex() {
        DataOutputStream ds = null;
        try {
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(indexFile, true);
            ds = new DataOutputStream(os);
            ds.writeLong(index);
            ds.flush();
            return indexFile;
        } catch (IOException e) {
            Log.e(getClass().getName(), "WriteIndex", e);
            return null;
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    Log.e(getClass().getName(), e);
                }
            }
        }
    }

    public interface ZipListener {

        public void onZipCreated(String zipFileName);

        public void onZipError(String zipFileName, Exception e);

    }

}
