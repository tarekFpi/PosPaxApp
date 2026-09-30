package com.paymentsave.paymentsave.coreapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.google.zxing.WriterException;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkItem;
import com.paymentsave.paymentsave.responses.PBLResponse.PBLCreateResponse;

import es.dmoral.toasty.Toasty;

public class DialogHelper {
    public static void showTransactionDialog(Context context, LinkItem linkItem) {

        // 2. Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_transaction_details, null);

        // 3. Bind views
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
        TextView tvAmount = dialogView.findViewById(R.id.tvAmount);
        TextView tvMid = dialogView.findViewById(R.id.tvMid);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);
        TextView tvCreatedBy = dialogView.findViewById(R.id.tvCreatedBy);
        TextView tvExpiredAt = dialogView.findViewById(R.id.tvExpiredAt);
        Button viewQrBtn = dialogView.findViewById(R.id.view_qr_btn);
        Button sendEmailBtn = dialogView.findViewById(R.id.send_email_btn);
        ImageButton closeImageButton = dialogView.findViewById(R.id.close_back_btn);

        // 4. Set data to views
        if ("expired".equalsIgnoreCase(linkItem.getStatus())) {
            tvStatus.setTextColor(context.getColor(R.color.red));
        } else if ("success".equalsIgnoreCase(linkItem.getStatus())) {
            tvStatus.setTextColor(context.getColor(R.color.green_text_color));
        } else if ("pending".equalsIgnoreCase(linkItem.getStatus())) {
            tvStatus.setTextColor(context.getColor(R.color.yellow));
        } else {
            tvStatus.setTextColor(context.getColor(R.color.red));
        }
        tvStatus.setText(linkItem.getStatus().toUpperCase());
        tvAmount.setText(String.format("%s %s", linkItem.getAmount(), linkItem.getCurrency()));
        tvMid.setText(linkItem.getBusinessMid());
        tvDescription.setText(linkItem.getDescription());
        tvCreatedBy.setText(linkItem.getCreatedAt());
        tvExpiredAt.setText(linkItem.getExpiredAt());

        // Dynamically change status color if it's expired
        if ("expired".equalsIgnoreCase(linkItem.getStatus())) {
            tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light, null));
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.white, null));
        }

        // 5. Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(dialogView);
//                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        viewQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlPath = linkItem.getPaymentLink();
                try {
                    Bitmap bitmap = QrUtils.generateQrCode(urlPath, 800);
                    showQrPopup(context, bitmap);
                    dialog.dismiss();
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static void showQrPopup(Context context, Bitmap qr) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.qr_receipt_layout, null);
        dialog.setContentView(view);

        ImageView qrImage = view.findViewById(R.id.dialogQrImage);
        qrImage.setImageBitmap(qr);
        dialog.show();
    }

    public static void showSuccessPBLDialog(Context context, FragmentActivity fragmentActivity, PBLCreateResponse pblCreateResponse, TransactionViewModel viewModel) throws WriterException {
        String urlPath = pblCreateResponse.getData().getLinkUrl();
        Bitmap bitmap = QrUtils.generateQrCode(urlPath, 800);

        // 2. Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.link_success_popup, null);

        // 3. Bind views
        ImageView qrLink = dialogView.findViewById(R.id.qr_link);
        ImageButton closeBtn = dialogView.findViewById(R.id.back_btn);
        qrLink.setImageBitmap(bitmap);
        Button button = dialogView.findViewById(R.id.send_email_btn);

        // 5. Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showEmailSendDialog(context, fragmentActivity, pblCreateResponse, viewModel, dialog);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        dialog.show();
    }


    public static void showEmailSendDialog(Context context, FragmentActivity fragmentActivity, PBLCreateResponse pblCreateResponse, TransactionViewModel viewModel, AlertDialog successDialog) throws WriterException {
        String urlPath = pblCreateResponse.getData().getLinkUrl();

        // 2. Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.email_sending_dialouge, null);

        // 3. Bind views
        EditText emailAddressEt = dialogView.findViewById(R.id.email_address_et);
        Button sendMailBtn = dialogView.findViewById(R.id.send_mail_btn);
        emailAddressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    sendMailBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.radius_btn_bg));
                } else {
                    sendMailBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.radius_gray));
                    emailAddressEt.setError(context.getString(R.string.please_enter_a_valid_email_address));
                    emailAddressEt.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // 5. Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(emailAddressEt.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(emailAddressEt.getText().toString()).matches()) {
                    String token = SharedHelper.getToken(context);
                    viewModel.sendEmailPaymentLink(token, emailAddressEt.getText().toString(), pblCreateResponse.getData().getPid()).observe(fragmentActivity, new Observer<Object>() {
                        @Override
                        public void onChanged(Object o) {
                            if (o != null) {
                                try {
                                    dialog.dismiss();
                                    successDialog.dismiss();
                                    Toasty.success(context, "Email Send Successful.", Toasty.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else {
                    emailAddressEt.setError(context.getString(R.string.please_enter_a_valid_email_address));
                    emailAddressEt.requestFocus();
                }
            }
        });
        dialog.show();
    }
}