package com.paymentsave.paymentsave.coreapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.test.printer.PrinterTester;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PrintUtils {
    private static final String TAG = "PrintUtils";

    public static void playAudio(Context context, boolean isSuccess) {
        AppConstants.executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.approved);
                    mediaPlayer.start();
                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.decline_alert);
                    mediaPlayer.start();
                }

            }
        });
    }

    public static void printReportCopy(Context context, Report report) {
        new Thread(new Runnable() {
            public void run() {
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.reciept_bw);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().printBitmap(bitmap);
                PrinterTester.getInstance().setGray(3);
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_NAME) + "\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_ADDRESS) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("THANK YOU\n\n\n", "UTF-8");

                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                        Byte.parseByte("6"));
                if (report.getReportType().equals("ZReport"))
                    PrinterTester.getInstance().printStr("       Z REPORT\n", "UTF-8");
                else
                    PrinterTester.getInstance().printStr("       X REPORT\n", "UTF-8");

                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                        Byte.parseByte("6"));
                PrinterTester.getInstance().printStr("              S/N: " + MainUtils.getDeviceSerial(context) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("            " + DateUtils.getFormattedDateUk(report.getCreatedAt()) + "\n\n\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("CURRENCY GBP\n\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_16);

                double saleTotal = Double.parseDouble(report.getSaleAmount()) / 100.0;
                double refundTotal = Double.parseDouble(report.getRefundAmount()) / 100.0;
                double gratuityTotal = Double.parseDouble(report.getGratuityAmount()) / 100.0;
                double completionTotal = Double.parseDouble(report.getCompletionAmount()) / 100.0;
                double cashbackTotal = Double.parseDouble(report.getCashbackAmount()) / 100.0;
//                String totalAmt = String.format("%.2f", (saleTotal - refundTotal + gratuityTotal));
                String totalAmt = String.format("%.2f", (saleTotal - refundTotal + completionTotal));

                PrinterTester.getInstance().printStr(formatString("SALE        x" + report.getSaleCount(), "£" + String.format("%.2f", saleTotal) + "\n", 30), "UTF-8");
                PrinterTester.getInstance().printStr(formatString("REFUND      x" + report.getRefundCount(), "£" + String.format("%.2f", refundTotal) + "\n", 30), "UTF-8");
                PrinterTester.getInstance().printStr(formatString("COMPLETION  x" + report.getCompletionCount(), "£" + String.format("%.2f", completionTotal) + "\n", 30), "UTF-8");
                PrinterTester.getInstance().printStr(formatString("NET TOTAL   x" + (report.getSaleCount() + report.getRefundCount() + report.getCompletionCount()), "£" + totalAmt + "\n\n", 31), "UTF-8");

                PrinterTester.getInstance().printStr("Inclusive Of\n", "UTF-8");
                PrinterTester.getInstance().printStr(formatString("CASHBACK    x" + report.getCashbackCount(), "£" + String.format("%.2f", cashbackTotal) + "\n", 30), "UTF-8");
                PrinterTester.getInstance().printStr(formatString("GRATUITY    x" + report.getGratuityCount(), "£" + String.format("%.2f", gratuityTotal) + "\n\n\n", 32), "UTF-8");


                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                        Byte.parseByte("6"));
                if (report.getReportError() == null || report.getReportError().equals("null")) {
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                            EFontTypeExtCode.FONT_16_32);
                    PrinterTester.getInstance().printStr("    REPORT COMPLETE\n\n\n\n\n\n", "UTF-8");
                } else if (report.getReportError().equals("REPORT_SAVED_BUT_NOT_SUBMITTED")) {
                    PrinterTester.getInstance().printStr(context.getString(R.string.report_couldn_t_be_sumitted_due_to_connection_problem_report_will_be_submitted_next_time_a_successful_connection_is_made_to_the_host_this_report_will_not_be_reprinted) + "\n\n\n\n\n\n", "UTF-8");
                } else {
                    PrinterTester.getInstance().printStr(report.getReportError() + "\n\n\n\n\n\n", "UTF-8");
                }

                PrinterTester.getInstance().start();
            }
        }).start();
    }

    public static void printReceiptCopy(Context context, TransactionRequest transaction, String owner) {
        String transactionType = transaction.getTransactionType();
        new Thread(new Runnable() {
            public void run() {
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.reciept_bw);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().printBitmap(bitmap);
                PrinterTester.getInstance().setGray(3);
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_NAME) + "\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_ADDRESS) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("THANK YOU\n\n\n", "UTF-8");
                PrinterTester.getInstance().printStr(DateUtils.getUKDate(new Date()) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("MID: ***" + transaction.getMerchantUid().substring(transaction.getMerchantUid().length() - 5) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("TID: *****" + transaction.getTerminalId().substring(transaction.getTerminalId().length() - 4) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("RECEIPT NO.: " + transaction.getReceiptId() + "\n", "UTF-8");
                if (transaction.getTnxNote() != null) {
                    PrinterTester.getInstance().printStr(formatString("TNX. NOTE: ", transaction.getTnxNote() + "\n", 30), "UTF-8");
                }

                PrinterTester.getInstance().printStr("APP SEQ: " + BuildConfig.VERSION_CODE + "\n\n\n", "UTF-8");
                if (transaction.getCardType() != null && !transaction.getCardType().toString().isEmpty()) {
                    PrinterTester.getInstance().printStr(transaction.getCardType() + "\n", "UTF-8");
                }
                if (transaction.getPan() != null && !transaction.getPan().isEmpty()) {
                    PrinterTester.getInstance().printStr(transaction.getPan() + "\n", "UTF-8");
                }
                if (transactionType.equals("REFUND_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("REFUND AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                } else if (transactionType.equals("CASHBACK_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("BASE AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())) + "\n", 30), "UTF-8");
                    PrinterTester.getInstance().printStr(formatString("CASHBACK AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getCashbackAmount())), 30), "UTF-8");
                } else if (transactionType.equals("SALE_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("SALE AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                } else if (transactionType.equals("MANUAL_REVERSAL_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("VOIDED AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                }

                double amount_total = Double.parseDouble(transaction.getAmount());
                double total_gratuity = Double.parseDouble(transaction.getGratuityAmount());
                double cashback_amt = Double.parseDouble(transaction.getCashbackAmount());
                if (("SALE_AUTO".equals(transactionType) || "CASHBACK_AUTO".equals(transactionType))
                        && total_gratuity > 0) {
                    amount_total = (amount_total + total_gratuity);
                    PrinterTester.getInstance().printStr("\n", "UTF-8");
                    PrinterTester.getInstance().printStr(formatString("Gratuity", "£" + String.format("%.2f", total_gratuity), 30), "UTF-8");
                }

                if (transactionType.equals("CASHBACK_AUTO")) {
                    amount_total = (amount_total + cashback_amt);
                }

                PrinterTester.getInstance().printStr("\n--------------------------------\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr(formatString("TOTAL", "£" + String.format("%.2f", amount_total), 23), "UTF-8");

                if (transaction.getAuthCode() != null) {
                    PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                    PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                            Byte.parseByte("8"));
                    if (Objects.equals(transaction.getResponseCode(), "00") && transaction.isApproved()) {
                        PrinterTester.getInstance().printStr("     AUTHORISED\n", "UTF-8");
                    } else {
                        PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                    }
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("     RESPONSE CODE : " + transaction.getResponseCode() + "\n", "UTF-8");
                    if (transaction.isPinVerified()) {
                        PrinterTester.getInstance().printStr("       VERIFIED BY PIN\n", "UTF-8");
                    }
                    PrinterTester.getInstance().printStr("      AUTH CODE : " + transaction.getAuthCode() + "\n", "UTF-8");

                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                            EFontTypeExtCode.FONT_16_16);
                    if (transaction.getErrorText() != null) {
                        PrinterTester.getInstance().printStr(String.format("(%s)\n", transaction.getErrorText()), "UTF-8");
                    }
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("\nUTI : " + transaction.getUti() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("  **" + owner + " COPY**\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                    PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                } else {
                    PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                    PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                            Byte.parseByte("8"));
                    PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                            EFontTypeExtCode.FONT_16_16);

                    if (transaction.getErrorText() != null) {
                        PrinterTester.getInstance().printStr(String.format("(%s)\n", transaction.getErrorText()), "UTF-8");
                    }
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("\nUTI : " + transaction.getUti() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("  **" + owner + " COPY**\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                    PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                }
                PrinterTester.getInstance().start();
            }
        }).start();
    }

    public static void printReceiptCopy2(Context context, TransactionRequest transaction, String owner) {
        String transactionType = transaction.getTransactionType();
        new Thread(new Runnable() {
            public void run() {
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.reciept_bw);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().printBitmap(bitmap);
                PrinterTester.getInstance().setGray(3);
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_NAME) + "\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr(SharedHelper.getStringData(context, AppConstants.SharedPref.TRADING_ADDRESS) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("THANK YOU\n\n\n", "UTF-8");
                PrinterTester.getInstance().printStr(DateUtils.getUKDate(new Date()) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("MID: ***" + transaction.getMerchantUid().substring(transaction.getMerchantUid().length() - 5) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("TID: *****" + transaction.getTerminalId().substring(transaction.getTerminalId().length() - 4) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("RECEIPT NO.: " + transaction.getReceiptId() + "\n", "UTF-8");

                if (transaction.getTnxNote() != null) {
                    PrinterTester.getInstance().printStr(formatString("TNX. NOTE: ", transaction.getTnxNote() + "\n", 30), "UTF-8");
                }

                PrinterTester.getInstance().printStr("APP SEQ: " + BuildConfig.VERSION_CODE + "\n\n\n", "UTF-8");
                if (transaction.getCardType() != null && !transaction.getCardType().toString().isEmpty()) {
                    PrinterTester.getInstance().printStr(transaction.getCardType() + "\n", "UTF-8");
                }
                if (transaction.getPan() != null && !transaction.getPan().isEmpty()) {
                    PrinterTester.getInstance().printStr(transaction.getPan() + "\n", "UTF-8");
                }
                if (transactionType.equals("REFUND_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("REFUND AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                } else if (transactionType.equals("CASHBACK_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("BASE AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())) + "\n", 30), "UTF-8");
                    PrinterTester.getInstance().printStr(formatString("CASHBACK AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getCashbackAmount())), 30), "UTF-8");
                } else if (transactionType.equals("SALE_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("SALE AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                } else if (transactionType.equals("MANUAL_REVERSAL_AUTO")) {
                    PrinterTester.getInstance().printStr(formatString("VOIDED AMOUNT", "£" + String.format("%.2f", Double.parseDouble(transaction.getAmount())), 30), "UTF-8");
                }

                double amount_total = Double.parseDouble(transaction.getAmount());
                double total_gratuity = Double.parseDouble(transaction.getGratuityAmount());
                if (transactionType.equals("SALE_AUTO") && total_gratuity > 0) {
                    amount_total = (amount_total + total_gratuity);
                    PrinterTester.getInstance().printStr("\n", "UTF-8");
                    PrinterTester.getInstance().printStr(formatString("Gratuity", "£" + String.format("%.2f", total_gratuity), 30), "UTF-8");
                }

                PrinterTester.getInstance().printStr("\n--------------------------------\n", "UTF-8");
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr(formatString("TOTAL", "£" + String.format("%.2f", amount_total), 23), "UTF-8");

                if (transaction.getAuthCode() != null) {
                    PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                    PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                            Byte.parseByte("8"));
                    if (Objects.equals(transaction.getResponseCode(), "00") && transaction.isApproved()) {
                        PrinterTester.getInstance().printStr("     AUTHORISED\n", "UTF-8");
                    } else {
                        PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                    }
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("     RESPONSE CODE : " + transaction.getResponseCode() + "\n", "UTF-8");
                    if (transaction.isPinVerified()) {
                        PrinterTester.getInstance().printStr("       VERIFIED BY PIN\n", "UTF-8");
                    }
                    PrinterTester.getInstance().printStr("      AUTH CODE : " + transaction.getAuthCode() + "\n\n", "UTF-8");

                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("UTI : " + transaction.getUti() + "\n\n", "UTF-8");
                    PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("  **" + owner + " COPY**\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                    PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                } else {
                    PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                    PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                            Byte.parseByte("8"));
                    PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("UTI : " + transaction.getUti() + "\n\n", "UTF-8");
                    PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                    PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("  **" + owner + " COPY**\n", "UTF-8");
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                            EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                    PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                }
                PrinterTester.getInstance().start();
            }
        }).start();
    }

    public static void rePrintCardHolderReceipt(Context context, List<String> stringList) {
        new Thread(new Runnable() {
            public void run() {
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.reciept_bw);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().printBitmap(bitmap);
                PrinterTester.getInstance().setGray(3);
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                        EFontTypeExtCode.FONT_16_16);
                for (String line : stringList) {
                    PrinterTester.getInstance().printStr(line + "\n", "UTF-8");
                }
                PrinterTester.getInstance().start();
            }
        }).start();
    }

    public static void printSystemInfo(Context context) {
        new Thread(new Runnable() {
            public void run() {
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.reciept_bw);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().printBitmap(bitmap);
                PrinterTester.getInstance().setGray(3);
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                        EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr("\n" + DateUtils.getUKDate(new Date()) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("MID: " + SharedHelper.getStringData(context, AppConstants.SharedPref.MID) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("TID: " + SharedHelper.getStringData(context, AppConstants.SharedPref.TID) + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("VERSION: paymentsavepax_v" + BuildConfig.VERSION_NAME + "\n", "UTF-8");
                PrinterTester.getInstance().printStr("APP SEQ: " + BuildConfig.VERSION_CODE + "\n\n\n\n\n\n", "UTF-8");
                PrinterTester.getInstance().start();
            }
        }).start();
    }

    public static String formatString(String left, String right, int width) {
        // Calculate the space needed to pad between the two strings
        int spaceWidth = width - left.length() - right.length();

        // Create a string of spaces
        String spaces = new String(new char[spaceWidth]).replace('\0', ' ');

        // Concatenate left string, spaces, and right string
        return left + spaces + right;
    }

    public static void openDialog(Context context, Report report) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.report_success_layout);
        TextView subText = dialog.findViewById(R.id.sub_text);
        TextView dateTv = dialog.findViewById(R.id.date_item_tv);
        TextView amountTv = dialog.findViewById(R.id.amount_item_tv);
        double saleAmount = 0.00;
        double refundAmount = 0.00;
        double gratuityAmount = 0.00;
        double completionAmount = 0.00;
        double net_total = 0.00;
        double sub_total = 0.00;
        try {
            saleAmount = Double.parseDouble(report.getSaleAmount()) / 100.0;
            refundAmount = Double.parseDouble(report.getRefundAmount()) / 100.0;
            gratuityAmount = Double.parseDouble(report.getGratuityAmount()) / 100.0;
            completionAmount = Double.parseDouble(report.getCompletionAmount()) / 100.0;
            net_total = saleAmount - refundAmount + completionAmount;
            sub_total = saleAmount - refundAmount + gratuityAmount + completionAmount;
        } catch (Exception e) {
            e.printStackTrace();
        }

        dateTv.setText(String.format("Date: %s", DateUtils.getFormattedDateTime(report.getCreatedAt())));
        if (report.getReportType().equals("ZReport")) {
            subText.setText(context.getString(R.string.your_have_successfully_requested_for_z_report_reconciliation));
            amountTv.setText(String.format("Batch Amount: £%.2f", net_total));
        } else {
            subText.setText(context.getString(R.string.your_have_successfully_requested_for_x_report_reconciliation));
            amountTv.setText(String.format("Shift End: £%.2f", net_total));
        }

        Button settingBtn = dialog.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button exitBtn = dialog.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printReportCopy(context, report);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setTitle(R.string.app_name);
        dialog.show();
    }

    public static void openFailedDialog(Context context, String msg) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.report_failed_layout);
        Button exitBtn = dialog.findViewById(R.id.exit_btn);
        TextView responseText = dialog.findViewById(R.id.sub_text);
//        responseText.setText(msg);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setTitle(R.string.app_name);
        dialog.show();
    }
}
