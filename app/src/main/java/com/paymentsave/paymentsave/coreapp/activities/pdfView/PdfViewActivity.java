package com.paymentsave.paymentsave.coreapp.activities.pdfView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//import com.github.barteksc.pdfviewer.PDFView;
import com.paymentsave.paymentsave.R;

public class PdfViewActivity extends AppCompatActivity {
//    private PDFView pdfView;
//    private File file;
//    private Context primaryBaseActivity;
//    private LinearLayout backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
//
//        pdfView = findViewById(R.id.pdfView);
//        backBtn = findViewById(R.id.pdf_view_back_btn);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        Bundle bundle = getIntent().getExtras();
//        String dest = this.getExternalFilesDir(null) + "/";
//        if (bundle != null) {
//            file = new File(dest, "order_receipt.pdf");
//        }
//
//        pdfView.fromFile(file)
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .enableAntialiasing(true)
//                .load();
    }
//
//    public void openWithExternalPdfApp() {
//        Uri uri = FileProvider.getUriForFile(PdfViewActivity.this, PdfViewActivity.this.getPackageName() + ".provider", file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/pdf");
//        intent.putExtra(Intent.EXTRA_STREAM, uri);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(intent);
//    }

//    protected void attachBaseContext(Context base) {
//        primaryBaseActivity = base;
//        super.attachBaseContext(base);
//    }

}

