package com.besa.barcodebug;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

class ImgBarcodeAnalyzer implements ImageAnalysis.Analyzer
{
    private final onDetectedCallback onDetectedCallback;
    private final BarcodeScanner barcodeScanner;
    private final String LOG_TAG = "Barcodebug";

    public ImgBarcodeAnalyzer(final ImgBarcodeAnalyzer.onDetectedCallback onDetectedCallback)
    {
        barcodeScanner = BarcodeScanning.getClient();
        this.onDetectedCallback = onDetectedCallback;
    }

    @Override
    @ExperimentalGetImage
    public void analyze(@NonNull final ImageProxy imageProxy)
    {
        Image hImage = imageProxy.getImage();
        if (hImage != null)
        {
            InputImage inputImage = InputImage.fromMediaImage(hImage, imageProxy.getImageInfo().getRotationDegrees());
            //imageProxy.close();

            barcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>()
            {
                @Override
                public void onSuccess(@NonNull List<Barcode> barcodes)
                {
                    imageProxy.close();
                    Log.i(LOG_TAG, "barcodeScanner onSuccess");

                    if(barcodes.isEmpty())
                    {
                        Log.i(LOG_TAG, "No any barcode has been detected");
                    }
                    else if(barcodes.size() > 1)
                    {
                        //TODO:prompt to select which
                    }
                    else
                    {
                        onDetectedCallback.onDetectedBarcode(barcodes);
                    }
                }
            });
        }
        else
        {
            Log.e(LOG_TAG, "Image returned NULL");
        }
    }

    public interface onDetectedCallback
    {
        void onDetectedBarcode(List<Barcode> barcode);
    }
}
