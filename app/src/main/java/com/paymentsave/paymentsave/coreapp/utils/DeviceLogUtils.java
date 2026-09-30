package com.paymentsave.paymentsave.coreapp.utils;

import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DeviceLogUtils {

    public static File init(Context context) throws IOException {
        String fileName = getTodayLogFileName();
        File logFile = new File(context.getFilesDir(), fileName);

        // Create the file if it doesn't exist
        if (!logFile.exists()) {
            logFile.createNewFile(); // May throw IOException
        }
        // Start cleanup thread (non-blocking)
        new Thread(() -> cleanOldLogs(context)).start();

        return logFile;
    }

    private static String getTodayLogFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date());
        return "device_log_" + date + ".txt";
    }

    public static void writeSingleLineLog(Context context, String message) throws IOException {
        File logFile = init(context);
        // Append to the file
        try (FileWriter writer = new FileWriter(logFile, true)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            writer.append(timestamp).append(" - ").append(message).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readTodayLog(Context context) throws IOException {
        File logFile = init(context);
        StringBuilder logContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
        }

        return logContent.toString();
    }

    private static void cleanOldLogs(Context context) {
        File dir = context.getFilesDir();
        File[] logFiles = dir.listFiles((d, name) -> name.startsWith("device_log_") && name.endsWith(".txt"));

        if (logFiles != null && logFiles.length > 5) {
            // Sort files by last modified descending
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified).reversed());
            }

            // Delete files after the 5 most recent
            for (int i = 5; i < logFiles.length; i++) {
                logFiles[i].delete();
            }
        }
    }
}
