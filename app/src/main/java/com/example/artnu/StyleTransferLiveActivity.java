package com.example.artnu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private FloatingActionButton chooseModelBtn;
    private ChooseModelDialog imageSegDialog;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 720);
    private boolean threed = false;
    private int choice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_transfer_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        PaintingUtil.readConfig(getApplicationContext());
        Log.e("Exception", "GET HERE 1");
        imageSegDialog = new ChooseModelDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (PaintingUtil.isUnlocked(imageSegDialog.getPaintingId(which))) {
                    choice = imageSegDialog.getPaintingId(which);
                    loadPredictor(imageSegDialog.getPaintingId(which));
                } else {
                    Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
                    startActivity(intent);
                }
            }
        });
        Log.e("Exception", "GET HERE 2");
        if (getIntent().getBooleanExtra("Show_List", false)) {
            imageSegDialog.show(getSupportFragmentManager(), ChooseModelDialog.TAG);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        threed = prefs.getBoolean("threed_choice", false);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.style_transfer_layout);
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageUtil.SaveImage(getApplicationContext(), resultImage);
                Toast.makeText(getApplicationContext(), "Picture Captured :)", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        Log.e("Exception", "GET HERE 3");

    }

    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    protected void onPreviewSizeChosen(final Size previewSize, final Size cameraViewSize, int rotation) {
        imageRotation = FritzVisionOrientation.getImageRotationFromCamera(this, getCameraId());
        if (choice == -1) {
            choice = PaintingUtil.readChoice(getApplicationContext());
        }
        loadPredictor(choice);
        Log.e("Exception", "after loading predictor ");
        final Size drawSize = threed ? new Size(cameraViewSize.getWidth(), cameraViewSize.getHeight()/2) : cameraViewSize;
        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        handleDrawingResult(canvas, drawSize);
                    }
                });


        Log.e("Exception", "before listener");
        chooseModelBtn = (FloatingActionButton) findViewById(R.id.list_button);
        chooseModelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSegDialog.show(getSupportFragmentManager(), ChooseModelDialog.TAG);
            }
        });
        Log.e("Exception", "after listener");

    }

    private void loadPredictor(int choice) {
        FritzOnDeviceModel onDeviceModel = getModel(choice);
        FritzVisionStylePredictorOptions options = new FritzVisionStylePredictorOptions();
        Log.e("Exception", "before predictor ");
        predictor = FritzVision.StyleTransfer.getPredictor(onDeviceModel, options);
        Log.e("Exception", "after predictor ");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_stylize ;
    }

    private FritzOnDeviceModel getModel(int choice) {
        Log.e("Exception", "choice " + choice);
        FritzOnDeviceModel[] styles = PaintingStyles.getAll();
        return styles[choice];
    }

    protected void handleDrawingResult(Canvas canvas, Size cameraSize) {
        if (resultImage != null) {
            if (threed) {
                canvas.drawBitmap(resultImage, null, new RectF(0, 0, cameraSize.getWidth(), cameraSize.getHeight()), null);
                canvas.drawBitmap(resultImage, null, new RectF(0, cameraSize.getHeight(), cameraSize.getWidth(), cameraSize.getHeight()*2), null);
            } else {
                canvas.drawBitmap(resultImage, null, new RectF(0, 0, cameraSize.getWidth(), cameraSize.getHeight()), null);
            }

        }
    }

    @Override
    public void onPause() {
        PaintingUtil.writeChoice(getApplicationContext(), choice);
        super.onPause();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = reader.acquireNextImage();

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
