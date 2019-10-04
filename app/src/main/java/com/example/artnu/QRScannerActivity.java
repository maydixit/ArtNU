package com.example.artnu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//
//public class QRScannerActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrscanner);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
//
//}

public class QRScannerActivity extends CameraActivity implements ImageReader.OnImageAvailableListener {
    private static final String TAG = QRScannerActivity.class.getSimpleName();
    private Bitmap resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_qrscanner);
        PaintingUtil.readConfig(getApplicationContext());
    }

    @Override
    protected void onPreviewSizeChosen(Size previewSize,final Size cameraViewSize, int rotation) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_qr;
    }

    protected Size getDesiredPreviewFrameSize() {
        return new Size(640, 480);
    }


    private void scanBarcodes(FirebaseVisionImage image) {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE,
                                FirebaseVisionBarcode.FORMAT_AZTEC)
                        .build();

         FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_TEXT:
                                    String displayValue = barcode.getDisplayValue();
                                    Toast.makeText(QRScannerActivity.this, displayValue, Toast.LENGTH_SHORT).show();

                                    Painting painting = PaintingUtil.getPaintingForQrValueOrNull(displayValue);
                                    if (painting != null) {
                                        PaintingUtil.setStatus(painting.getId(), PaintingUtil.STATUS.PARTIAL, getApplicationContext());
                                        Intent intent = new Intent(getApplicationContext(), StyleTransferLiveActivity.class);
                                        intent.putExtra("Show_List", true);
                                        startActivity(intent);
                                    }

                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QRScannerActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private AtomicBoolean computing = new AtomicBoolean(false);

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }
        final FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(image, 0);

        image.close();
        if (!computing.compareAndSet(false, true)) {
            return;
        }
        runInBackground(new Runnable() {
            @Override
            public void run() {
                scanBarcodes(visionImage);
                computing.set(false);
            }});

    }
}