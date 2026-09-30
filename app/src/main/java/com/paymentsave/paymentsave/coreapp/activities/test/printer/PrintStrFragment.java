package com.paymentsave.paymentsave.coreapp.activities.test.printer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.paymentsave.paymentsave.R;

public class PrintStrFragment extends Fragment implements View.OnClickListener {

    private Spinner asciiSpinner, extCodeSpinner;
    private Button addBitMapBt, doubleWidthBt, doubleHeightBt, invertBt, getStatusBt, getDotLineBt, startBt;
    private EditText wordSpaceEt, lineSpaceEt, leftIndentEt, setGrayEt, stepEt, printStrEt;
    private TextView statusTv, getDotLineTv;

    private boolean containBitmap = false, doubleWidth = false, doubleHeight = false, invert = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_str, container, false);
        initView(view);

        ArrayAdapter<EFontTypeAscii> asciiAdapter = new ArrayAdapter<EFontTypeAscii>(getActivity(),
                android.R.layout.simple_spinner_item, EFontTypeAscii.values());
        asciiSpinner.setAdapter(asciiAdapter);
        ArrayAdapter<EFontTypeExtCode> extCodeAdapter = new ArrayAdapter<EFontTypeExtCode>(getActivity(),
                android.R.layout.simple_spinner_item, EFontTypeExtCode.values());
        extCodeSpinner.setAdapter(extCodeAdapter);

        return view;
    }

    private void initView(View view) {
        asciiSpinner = (Spinner) view.findViewById(R.id.fragment_printer_spinner_ascii);
        extCodeSpinner = (Spinner) view.findViewById(R.id.fragment_printer_spinner_extcode);

        addBitMapBt = (Button) view.findViewById(R.id.fragment_printer_bitmap);
        doubleWidthBt = (Button) view.findViewById(R.id.fragment_printer_doublewidth_bt);
        doubleHeightBt = (Button) view.findViewById(R.id.fragment_printer_doubleheight_bt);
        invertBt = (Button) view.findViewById(R.id.fragment_printer_invert_bt);
        getStatusBt = (Button) view.findViewById(R.id.fragment_printer_status_bt);
        getDotLineBt = (Button) view.findViewById(R.id.fragment_printer_getdotline_bt);
        startBt = (Button) view.findViewById(R.id.fragment_printer_start);

        addBitMapBt.setOnClickListener(this);
        doubleWidthBt.setOnClickListener(this);
        doubleHeightBt.setOnClickListener(this);
        invertBt.setOnClickListener(this);
        getStatusBt.setOnClickListener(this);
        getDotLineBt.setOnClickListener(this);
        startBt.setOnClickListener(this);

        wordSpaceEt = (EditText) view.findViewById(R.id.fragment_printer_spaceset_word_et);
        lineSpaceEt = (EditText) view.findViewById(R.id.fragment_printer_spaceset_line_et);
        leftIndentEt = (EditText) view.findViewById(R.id.fragment_printer_leftindent_et);
        setGrayEt = (EditText) view.findViewById(R.id.fragment_printer_setgray_et);
        stepEt = (EditText) view.findViewById(R.id.fragment_printer_step_et);
        printStrEt = (EditText) view.findViewById(R.id.fragment_printer_prnstr_et);
        statusTv = (TextView) view.findViewById(R.id.fragment_printer_status_tv);
        getDotLineTv = (TextView) view.findViewById(R.id.fragment_printer_getdotline_tv);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_printer_bitmap:
                if (containBitmap) {
//                    addBitMapBt.setText(getResources().getString(R.string.printer_bitmap_no));
                    containBitmap = false;
                } else {
//                    addBitMapBt.setText(getResources().getString(R.string.printer_bitmap_contain));
                    containBitmap = true;
                }
                break;
            case R.id.fragment_printer_doublewidth_bt:
                if (doubleWidth) {
//                    doubleWidthBt.setText(getResources().getString(R.string.printer_normal_width));
                    doubleWidth = false;
                } else {
//                    doubleWidthBt.setText(getResources().getString(R.string.printer_double_width));
                    doubleWidth = true;
                }
                break;
            case R.id.fragment_printer_doubleheight_bt:
                if (doubleHeight) {
//                    doubleHeightBt.setText(getResources().getString(R.string.printer_normal_height));
                    doubleHeight = false;
                } else {
//                    doubleHeightBt.setText(getResources().getString(R.string.printer_double_height));
                    doubleHeight = true;
                }
                break;
            case R.id.fragment_printer_invert_bt:
                if (invert) {
//                    invertBt.setText(getResources().getString(R.string.printer_normal));
                    invert = false;
                } else {
//                    invertBt.setText(getResources().getString(R.string.printer_invert));
                    invert = true;
                }
                break;
            case R.id.fragment_printer_status_bt:
                statusTv.setText(PrinterTester.getInstance().getStatus());
                break;
            case R.id.fragment_printer_getdotline_bt:
                getDotLineTv.setText(PrinterTester.getInstance().getDotLine() + "");
                break;
            case R.id.fragment_printer_start:
                new Thread(new Runnable() {
                    public void run() {
                        PrinterTester.getInstance().init();
//                        PrinterTester.getInstance().fontSet((EFontTypeAscii) asciiSpinner.getSelectedItem(),
//                                (EFontTypeExtCode) extCodeSpinner.getSelectedItem());
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inScaled = false;
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rect,options);
////                        Log.i("Test", "width:"+bitmap.getWidth()+"height:"+bitmap.getHeight());
//                        if (containBitmap) {
//                            PrinterTester.getInstance().printBitmap(bitmap);
//                        }
//                        PrinterTester.getInstance().spaceSet(Byte.parseByte(wordSpaceEt.getText().toString()),
//                                Byte.parseByte(lineSpaceEt.getText().toString()));
//                        PrinterTester.getInstance().leftIndents(Short.parseShort(leftIndentEt.getText().toString()));
//                        PrinterTester.getInstance().setGray(Integer.parseInt(setGrayEt.getText().toString()));
//                        if (doubleWidth) {
//                            PrinterTester.getInstance().setDoubleWidth(doubleWidth, doubleWidth);
//                        }
//                        if (doubleHeight) {
//                            PrinterTester.getInstance().setDoubleHeight(doubleHeight, doubleHeight);
//                        }
//                        PrinterTester.getInstance().setInvert(invert);
//                        String str = printStrEt.getText().toString();
//                        if (str != null && str.length() > 0)
//                            PrinterTester.getInstance().printStr("Hi buddy", null);
                            PrinterTester.getInstance().printStr("--------------------------------\n" +
                                    "      [B]Your Report Name[/B]\n" +
                                    "       123 Main Street\n" +
                                    "     City, State, Zip Code\n" +
                                    "   Phone: (123) 456-7890\n" +
                                    "   Date: 2024-01-24 12:34 PM\n" +
                                    "--------------------------------\n" +
                                    "Item         Qty    Price   Total\n" +
                                    "--------------------------------\n" +
                                    "Product 1     2      $10.00  $20.00\n" +
                                    "Product 2     1      $15.00  $15.00\n" +
                                    "--------------------------------\n" +
                                    "Subtotal                      $35.00\n" +
                                    "Tax (7%)                      $2.45\n" +
                                    "--------------------------------\n" +
                                    "Total                         $37.45\n" +
                                    "--------------------------------\n" +
                                    "Thank you for your purchase!\n" +
                                    "--------------------------------", null);
//                            PrinterTester.getInstance().printStr(str, null);
//                        PrinterTester.getInstance().step(Integer.parseInt(stepEt.getText().toString().trim()));

                        getDotLineTv.post(new Runnable() {
                            public void run() {
                                getDotLineTv.setText(PrinterTester.getInstance().getDotLine() + "");
                            }
                        });
                        final String status = PrinterTester.getInstance().start();
                        statusTv.post(new Runnable() {
                            public void run() {
                                statusTv.setText(status);
                            }
                        });
                    }
                }).start();

                break;

            default:
                break;
        }
    }
}
