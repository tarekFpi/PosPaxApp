package com.paymentsave.paymentsave.coreapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//    EEE : Day ( Mon )
//    MMMM : Full month name ( December ) // MMMM February
//    MMM : Month in words ( Dec )
//    MM : Month ( 12 )
//    dd : Day in 2 chars ( 03 )
//    d: Day in 1 char (3)
//    HH : Hours ( 12 )
//    mm : Minutes ( 50 )
//    ss : Seconds ( 34 )
//    yyyy: Year ( 2020 ) //both yyyy and YYYY are same
//    YYYY: Year ( 2020 )
//    zzz : GMT+05:30
//    a : ( AM / PM )
//    aa : ( AM / PM )
//    aaa : ( AM / PM )
//    aaaa : ( AM / PM )

public class DateUtils {
    private static final String TAG = "DateUtils";

    public static String formatApiDateTime(String inputDateTime) {
        try {
            // Remove microseconds because SimpleDateFormat handles milliseconds better
            String cleanDateTime = inputDateTime;

            if (inputDateTime.contains(".")) {
                cleanDateTime = inputDateTime.substring(0, inputDateTime.indexOf("."));
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.ENGLISH
            );

            SimpleDateFormat outputFormat = new SimpleDateFormat(
                    "dd MMMM yyyy, hh:mma",
                    Locale.ENGLISH
            );

            Date date = inputFormat.parse(cleanDateTime);

            if (date != null) {
                return outputFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCurrentDateTime() {
        // Get the current time in the UK time zone (Europe/London)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm:ss");
            return ZonedDateTime.now().format(formatter);
        }
        return "";
    }

    public static String getFormattedDateTime(String created_at) {
        // Get the current time in the UK time zone (Europe/London)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (created_at != null) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(created_at);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy hh:mm:ss");
                return zonedDateTime.format(formatter);
            }
        }
        return "";
    }

    public static boolean isVoidAble(String createdAt) {
        // Define the formatter to parse the date-time string
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        // Parse the createdAt string to LocalDateTime assuming it's in local time
        LocalDateTime createdDateTime = LocalDateTime.parse(createdAt, formatter);

        // Get the current time in the UK time zone (Europe/London)
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Europe/London"));

        // Convert the createdDateTime to the UK time zone for accurate comparison
        ZonedDateTime createdZonedDateTime = createdDateTime.atZone(ZoneId.of("Europe/London"));

        // Calculate the duration between the current UK time and the created time
        Duration duration = Duration.between(createdZonedDateTime, currentDateTime);

        // Check if the duration is less than 2 minutes
        return duration.toMinutes() < 1;
    }

    public static String getTime(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        // Create a custom time format
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormatter.format(date);
    }

    public static long printDayDifference(String dateStart, String dateStop) {

        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }

        // Parsing the date strings
        LocalDate startDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startDate = LocalDate.parse(dateStart, formatter);
        }
        LocalDate endDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            endDate = LocalDate.parse(dateStop, formatter);
        }

        // Calculating the difference in days
        long daysBetween = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        }

        return daysBetween;
    }

    public static String getUSAFormattedDate(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        SimpleDateFormat customFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String getFormattedDate(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        Date currentDate = new Date();

        String formattedStartDate = getFormattedOnlyDate(inputDateString);
        String formattedEndDate = getFormattedOnlyDate(currentDate.toString());

        if (printDayDifference(formattedStartDate, formattedEndDate) == 0) {
            return "Today";
        } else if (printDayDifference(formattedStartDate, formattedEndDate) == 1) {
            return "Yesterday";
        } else {
            // SimpleDateFormat customFormatter = new SimpleDateFormat("EEE d 'of' MMM", Locale.getDefault());
            SimpleDateFormat customFormatter = new SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault());
            return customFormatter.format(date);
        }
    }

//    public static Date[] parseDateRangeFromDatePicker(String dateRange) throws ParseException {
//        // Split the range into start and end dates
//        String[] parts = dateRange.split(" – ");
//        String startDateStr = parts[0];
//        String endDateStr = parts[1];
//
//        // Get the current year
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//
//        // Assume the format "d MMM" for both dates
//        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
//
//        // Parse start and end dates
//        Date startDate = dateFormat.parse(startDateStr + " " + year);
//        Date endDate = dateFormat.parse(endDateStr + " " + year);
//
//        // Handle year transition
//        if (endDate.before(startDate)) {
//            // This means the end date is in the next year
//            calendar.setTime(endDate);
//            calendar.add(Calendar.YEAR, 1);
//            endDate = calendar.getTime();
//        }
//
//        return new Date[]{startDate, endDate};
//    }

    public static Date[] parseDateRangeFromDatePicker(String dateRange) throws ParseException {
        // Define date formats
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        SimpleDateFormat shortDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

        // Split the range by "–" (en dash)
        String[] parts = dateRange.split("–");
        String startDateStr = parts[0].trim();
        String endDateStr = parts[1].trim();

        // Calendar instance to determine the year
        Calendar calendar = Calendar.getInstance();

        // Parse the end date first to determine the year
        Date endDate;
        if (endDateStr.matches(".*\\d{4}$")) {
            endDate = fullDateFormat.parse(endDateStr);
        } else {
            endDate = shortDateFormat.parse(endDateStr + " " + calendar.get(Calendar.YEAR));
        }

        // Use the end date's year for the start date if the year is not provided
        calendar.setTime(endDate);
        int endYear = calendar.get(Calendar.YEAR);

        Date startDate;
        if (startDateStr.matches(".*\\d{4}$")) {
            startDate = fullDateFormat.parse(startDateStr);
        } else {
            startDate = shortDateFormat.parse(startDateStr + " " + endYear);
        }

        // Return the parsed date range as an array
        return new Date[]{startDate, endDate};
    }

    public static String getFormattedDateUk(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("d/MM/yyyy HH:mm:ss", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String getUKDate(Date date) {
        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("d/MM/yyyy HH:mm:ss", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String getFormattedDateUSA(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String formatSystemDate(String inputDateString) throws ParseException {
        Log.d(TAG, "formatSystemDate: " + inputDateString);
        Date date;
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.getDefault());
            date = inputFormatter.parse(inputDateString);
        } catch (Exception e) {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.getDefault());
            date = inputFormatter.parse(inputDateString);
        }

        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static Date parseDateString(String dateString) {
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
//        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.getDefault());
//        SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
        try {
            return inputFormatter.parse(dateString);
        } catch (ParseException e) {
            try {
                SimpleDateFormat inputFormatter2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.getDefault());
                return inputFormatter2.parse(dateString);
            } catch (Exception ex) {
                try {
                    SimpleDateFormat inputFormatter2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.getDefault());
                    return inputFormatter2.parse(dateString);
                } catch (Exception exception) {
                    return new Date();
                }
            }
        }
    }

    public static String parseSystemStringDate(String dateString) {
        Log.d(TAG, "parseSystemStringDate: " + dateString);
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
            Date date = inputFormatter.parse(dateString);

            // Create a custom date format
            SimpleDateFormat customFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Log.d(TAG, "parseSystemStringDate: " + customFormatter.format(date));
            return customFormatter.format(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat inputFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.ENGLISH);
                Date date = inputFormatter.parse(dateString);

                // Create a custom date format
                SimpleDateFormat customFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Log.d(TAG, "parseSystemStringDate: " + customFormatter.format(date));
                return customFormatter.format(date);
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public static String getFormattedOnlyDate(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String getNormalDateString(String inputDateString) {
        Date date = parseDateString(inputDateString);
        SimpleDateFormat customFormatter = new SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String getFormattedOnlyTime(String inputDateString) {
        // Parse the input string into a Date object
        Date date = parseDateString(inputDateString);
        // Create a custom date format
        SimpleDateFormat customFormatter = new SimpleDateFormat("hh:mm:ss aaaa", Locale.getDefault());
        return customFormatter.format(date);
    }

    public static String generateUniqueSplitBillId(String tid) {
        // Format: yyyyMMddHHmm ss
        String timestamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }

        // Combine as a long-like string
        String uniqueTid = tid + timestamp;

        return uniqueTid;
    }
}
