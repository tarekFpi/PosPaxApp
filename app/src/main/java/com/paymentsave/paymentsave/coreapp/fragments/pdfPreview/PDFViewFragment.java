package com.paymentsave.paymentsave.coreapp.fragments.pdfPreview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.github.barteksc.pdfviewer.PDFView;
import com.paymentsave.paymentsave.R;

import java.io.File;

public class PDFViewFragment extends Fragment {

//    private PDFView pdfView;
    private File file;

    public PDFViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_p_d_f_view, container, false);
//        pdfView = view.findViewById(R.id.pdfView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        String dest = getActivity().getExternalFilesDir(null) + "/";
        String dest = "/storage/emulated/0/Android/data/com.app.smartpos/files/";
        file = new File(dest, "order_receipt.jpg");
//        pdfView.fromFile(file)
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .enableAntialiasing(true)
//                .load();

    }
}