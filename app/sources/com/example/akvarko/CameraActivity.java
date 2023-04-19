package com.example.akvarko;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    MaterialButton captureImage;
    private Executor executor = Executors.newSingleThreadExecutor();
    PreviewView mPreviewView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.camera_activity);
        this.mPreviewView = (PreviewView) findViewById(R.id.camera);
        this.captureImage = (MaterialButton) findViewById(R.id.captureImg);
        startCamera();
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            public void run() {
                try {
                    CameraActivity.this.bindPreview((ProcessCameraProvider) cameraProviderFuture.get());
                } catch (InterruptedException | ExecutionException e) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /* access modifiers changed from: package-private */
    public void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().setTargetAspectRatio(0).build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(1).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        ImageCapture imageCapture = new ImageCapture.Builder().setTargetRotation(3).build();
        preview.setSurfaceProvider(this.mPreviewView.getSurfaceProvider());
        Camera bindToLifecycle = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);
        this.captureImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File mImageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Akvarko");
                if (mImageDir.exists() || mImageDir.mkdirs()) {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Akvarko", "scan.jpg");
                    Bitmap xy = CameraActivity.this.mPreviewView.getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    xy.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    if (file.exists() && file.delete()) {
                        System.out.println("file Deleted :");
                    }
                    try {
                        file.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(file, true);
                        outputStream.write(byteArray);
                        outputStream.flush();
                        outputStream.close();
                        Intent intent = new Intent();
                        intent.putExtra("fpath", file.getPath().toString());
                        CameraActivity.this.setResult(2, intent);
                        CameraActivity.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
