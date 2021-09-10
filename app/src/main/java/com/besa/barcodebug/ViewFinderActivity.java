package com.besa.barcodebug;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewFinderActivity extends AppCompatActivity implements ImgBarcodeAnalyzer.onDetectedCallback
{
    private PreviewView pWviewFinder;
    private ExecutorService camExecutor;
    private final String LOG_TAG = "Barcodebug";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_finder);
        pWviewFinder = findViewById(R.id.view_finder);

        camExecutor = Executors.newSingleThreadExecutor();

        //check permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)
        {
            startCamera();
        }
        else
        {
            requestPermission.launch(Manifest.permission.CAMERA);
        }
    }//end onCreate

    private void startCamera()
    {
        //Request a CameraProvider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        //Check for CameraProvider availability
        cameraProviderFuture.addListener(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Camera provider is now guaranteed to be available
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                }
                catch (ExecutionException | InterruptedException e)
                {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }//end catch
            }//end run()
        }, ContextCompat.getMainExecutor(this));
    }//end startCamera()

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider)
    {
        // Set up the view finder use case to display camera preview
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)//select back camera
                .build();

        // Connect the preview use case to the previewView
        preview.setSurfaceProvider(pWviewFinder.getSurfaceProvider());

        ImgBarcodeAnalyzer imgBarcodeAnalyzer = new ImgBarcodeAnalyzer(this);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                //.setBackgroundExecutor()
                .build();
        imageAnalysis.setAnalyzer(camExecutor, imgBarcodeAnalyzer);
        cameraProvider.unbindAll();

        try
        {

            // Attach use cases to the camera with the same lifecycle owner
            Camera cam = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);
            //cam.getCameraInfo().hasFlashUnit();
            //cam.getCameraControl().enableTorch(true);
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Use case binding failed", e);
        }
    }

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>()
            {
                @Override
                public void onActivityResult(final Boolean isGranted)
                {
                    if(isGranted)
                    {
                        startCamera();
                    }
                    else
                    {
                        Log.e(LOG_TAG, "Camera Permission Denied...");
                    }
                }
            });

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        camExecutor.shutdown();
    }

    private BarcodePar getBarcodeFields(final List<Barcode> barcodes)
    {
        BarcodePar barcodePar = new BarcodePar();
        barcodePar.setFormat(BarcodeFormat.getFormat(barcodes.get(0).getFormat()).name());//enum
        barcodePar.setType(BarcodeDataType.getType(barcodes.get(0).getValueType()).name());//enum
        barcodePar.setDisplayValue(barcodes.get(0).getDisplayValue());
        barcodePar.setRawValue(barcodes.get(0).getRawValue());
        barcodePar.setRawBytes(barcodes.get(0).getRawBytes());
        return barcodePar;
    }

    @Override
    public void onDetectedBarcode(final List<Barcode> barcodes)
    {
        Intent resultReturnIntent = new Intent();
        resultReturnIntent.putExtra("BarcodeData", getBarcodeFields(barcodes));
        setResult(CommonStatusCodes.SUCCESS, resultReturnIntent);
        finish();
    }
}//end activity