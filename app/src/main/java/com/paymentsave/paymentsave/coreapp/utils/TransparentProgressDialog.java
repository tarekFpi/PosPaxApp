package com.paymentsave.paymentsave.coreapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.paymentsave.paymentsave.R;

import pl.droidsonroids.gif.GifImageView;

public class TransparentProgressDialog extends Dialog {

    private ImageView iv;
    private static TransparentProgressDialog progressDialog;

    public static TransparentProgressDialog getInstance(Context context) {
//        if (progressDialog == null) {
//            synchronized (TransparentProgressDialog.class) {
//                if (progressDialog == null) {
//                    progressDialog = new TransparentProgressDialog(context,0);
//                }
//            }
//        }
        progressDialog = new TransparentProgressDialog(context,0);
        return progressDialog;
    }

    public TransparentProgressDialog(Context context, int resourceIdOfImage) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleInverse);
        iv = new ImageView(context);
//        iv.setImageResource(resourceIdOfImage);
        iv.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.loading));

        LinearLayout layout2 = new LinearLayout(context);
        layout2.setOrientation(LinearLayout.VERTICAL);
        layout2.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout2.addView(progressBar, params2);

        GifImageView iv2 = new GifImageView(context);

//        iv2.setImageResource(R.drawable.loading);


//        layout.addView(layout2, params);
//        layout.addView(iv2, params);
        Glide.with(context).load(R.drawable.loading).into(iv);
        layout.addView(iv, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(3000);
//        iv.setAnimation(anim);
//        iv.startAnimation(anim);
    }
}
