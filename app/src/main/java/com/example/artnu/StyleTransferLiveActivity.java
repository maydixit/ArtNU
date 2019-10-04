package com.example.artnu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.fritzvisionstylepaintings.PaintingStyles;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.ImageRotation;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;

public class StyleTransferLiveActivity extends CameraActivity  implements ImageReader.OnImageAvailableListener{
    private static final String TAG = StyleTransferLiveActivity.class.getSimpleName();

    private AtomicBoolean computing = new AtomicBoolean(false);
    private Bitmap resultImage;
    private FritzVisionStylePredictor predictor;
    private ImageRotation imageRotation;
    private Button chooseModelBtn;
    private ChooseModelDialog imageSegDialog;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 720);
    private AskCodeDialog codeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_transfer_camera);
        PaintingUtil.readConfig(getApplicationContext());
        imageSegDialog = new ChooseModelDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // unlocked
                        loadPredictor(which);
                        break;
                    case 1: // partially unlocked, needs code
                        // todo Note here we will know the item, use this to figure out the code.
                        codeDialog = new AskCodeDialog(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String code = codeDialog.getText();
                                // TODO match code here with the item!
                                // if code is correct, save to storage as unlocked and call loadpredictor()
                            }
                        });
                        codeDialog.show(getSupportFragmentManager(), AskCodeDialog.TAG);
                        break;
                    case 2: // locked
                        startActivity(new Intent(getApplicationContext(), QRScannerActivity.class));
                        break;
                }
                // Handle here what happens based on 'which'.
                // if which is in the file of available values , choose that and load selector ?
                // If not , go to QR reader ?
                // TODO

            }
        });
        if (getIntent().getBooleanExtra("Show_List", false)) {
            imageSegDialog.show(getSupportFragmentManager(), ChooseModelDialog.TAG);
        }
    }

    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    protected void onPreviewSizeChosen(final Size previewSize, final Size cameraViewSize, int rotation) {
        imageRotation = FritzVisionOrientation.getImageRotationFromCamera(this, getCameraId());
        loadPredictor(0);
        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        handleDrawingResult(canvas, cameraViewSize);
                    }
                });


        chooseModelBtn = (Button) findViewById(R.id.list_button);
        chooseModelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSegDialog.show(getSupportFragmentManager(), ChooseModelDialog.TAG);
            }
        });

    }

    private void loadPredictor(int choice) {
        FritzOnDeviceModel onDeviceModel = getModel(choice);
        FritzVisionStylePredictorOptions options = new FritzVisionStylePredictorOptions();
        predictor = FritzVision.StyleTransfer.getPredictor(onDeviceModel, options);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_stylize ;
    }

    private FritzOnDeviceModel getModel(int choice) {
        FritzOnDeviceModel[] styles = PaintingStyles.getAll();
        return styles[choice];
    }

    protected void handleDrawingResult(Canvas canvas, Size cameraSize) {
        Log.i(TAG, "In handle drawing result");
        if (resultImage != null) {
            canvas.drawBitmap(resultImage, null, new RectF(0, 0, cameraSize.getWidth(), cameraSize.getHeight()), null);
            Log.i(TAG, "Done drawing");
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }
        if (!computing.compareAndSet(false, true)) {
            image.close();
            return;
        }
        runInBackground(new Runnable() {
            @Override
            public void run() {
                FritzVisionStyleResult styleResult = predictor.predict(FritzVisionImage.fromMediaImage(image, imageRotation));
                resultImage = styleResult.toBitmap();

                requestRender();
                computing.set(false);
                image.close();
            }});
    }
}
