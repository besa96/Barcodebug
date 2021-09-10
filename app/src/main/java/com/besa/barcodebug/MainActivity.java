package com.besa.barcodebug;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity
{
    private TextView mResultTextView;
    private TextView tVFormat;
    private TextView tVVType;
    private TextView tVRawStr;
    private TextView tVRawByte;
    private ImageButton iBCopyDVal;
    private ImageButton iBCopyRStr;
    private ImageButton iBCopyRByte;
    private final String LOG_TAG = "Barcodebug";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = findViewById(R.id.result_textview);
        tVFormat = findViewById(R.id.tVFormat);
        tVVType = findViewById(R.id.tVValueType);
        tVRawStr = findViewById(R.id.tVRawString);
        tVRawByte = findViewById(R.id.tVRawBytes);
        iBCopyDVal = findViewById(R.id.iBCopyDVal);
        iBCopyRStr = findViewById(R.id.iBCopyRStr);
        iBCopyRByte = findViewById(R.id.iBCopyRByte);

        findViewById(R.id.scan_barcode_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //live camera activity to read barcode
                activityResultLauncher.launch(new Intent(MainActivity.this, ViewFinderActivity.class));
            }
        });

        findViewById(R.id.tVRawBytesC).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tVRawByte.setVisibility(tVRawByte.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }//end onCreate

    private void copyText(final String label, final CharSequence text, final String message)
    {
        ClipboardManager clipboard =  (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == CommonStatusCodes.SUCCESS)
                    {
                        Intent resultIntent = result.getData();
                        if (resultIntent != null)
                        {
                            BarcodePar barcode = resultIntent.getParcelableExtra("BarcodeData");

                            tVFormat.setText(barcode.getFormat());
                            tVVType.setText(barcode.getType());
                            mResultTextView.setText(barcode.getDisplayValue());
                            tVRawStr.setText(barcode.getRawValue());
                            tVRawByte.setText(encode(barcode.getRawBytes(), false, ByteOrder.nativeOrder()));

                            //copy buttons
                            iBCopyDVal.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    copyText("Barcode_Result", mResultTextView.getText(), "Display Value Copied");
                                }
                            });

                            iBCopyRStr.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    copyText("Barcode_Value", tVRawStr.getText(), "Raw String Copied");
                                }
                            });

                            iBCopyRByte.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    copyText("Barcode_Data", tVRawByte.getText(), "Raw Bytes Copied");
                                }
                            });
                        }
                    }
                    else
                    {
                        Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format), CommonStatusCodes.getStatusCodeString(result.getResultCode())));
                    }
                }//end onActivityResult
            });//end callback


    private String encode(final byte[] byteArray, final boolean upperCase, final ByteOrder byteOrder)
    {
        final char[] LOOKUP_TABLE_LOWER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};
        final char[] LOOKUP_TABLE_UPPER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46};

        // our output size will be exactly 2x byte-array length
        final char[] buffer = new char[byteArray.length * 2];

        // choose lower or uppercase lookup table
        final char[] lookup = upperCase ? LOOKUP_TABLE_UPPER : LOOKUP_TABLE_LOWER;

        for (int i = 0; i < byteArray.length; i++)
        {
            // for little endian we count from last to first
            int index = (byteOrder == ByteOrder.BIG_ENDIAN) ? i : byteArray.length - i - 1;

            // extract the upper 4 bit and look up char (0-A)
            buffer[i << 1] = lookup[(byteArray[index] >> 4) & 0xF];
            // extract the lower 4 bit and look up char (0-A)
            buffer[(i << 1) + 1] = lookup[(byteArray[index] & 0xF)];
        }
        return new String(buffer);
    }
}//end activity