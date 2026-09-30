package com.paymentsave.paymentsave.coreapp.utils;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFUtils {

    public static Bitmap[] pdfToBitmap(File file) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer renderer = new PdfRenderer(parcelFileDescriptor);
        int pageCount = renderer.getPageCount();
        Bitmap[] bitmaps = new Bitmap[pageCount];

        for (int i = 0; i < pageCount; i++) {
            PdfRenderer.Page page = renderer.openPage(i);
            int width = page.getWidth();
            int height = page.getHeight();
            bitmaps[i] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmaps[i], null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
        }

        renderer.close();
        parcelFileDescriptor.close();

        return bitmaps;
    }

    public static void saveBitmapsToFile(Bitmap[] bitmaps, File outputDir) throws IOException {
        for (int i = 0; i < bitmaps.length; i++) {
            File outputFile = new File(outputDir, "page_" + i + ".png");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            bitmaps[i].compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        }
    }
}


