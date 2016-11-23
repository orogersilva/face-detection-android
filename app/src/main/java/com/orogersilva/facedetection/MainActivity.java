package com.orogersilva.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.selfieImageView)
    ImageView selfieImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.processButton)
    public void onProcess() {

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;

        Bitmap defaultBitmap = BitmapFactory.decodeResource(
                getResources(), R.drawable.selfie, bitmapOptions);

        Paint rectPaint = new Paint();

        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.CYAN);
        rectPaint.setStyle(Paint.Style.STROKE);

        Bitmap temporaryBitmap = Bitmap.createBitmap(
                defaultBitmap.getWidth(), defaultBitmap.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(temporaryBitmap);
        canvas.drawBitmap(defaultBitmap, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if (!faceDetector.isOperational()) {

            new AlertDialog.Builder(this)
                    .setMessage("Face Detector could not be set up on your device!")
                    .show();

            return;
        }

        Frame frame = new Frame.Builder()
                .setBitmap(defaultBitmap)
                .build();
        SparseArray<Face> facesSparseArray = faceDetector.detect(frame);

        for (int i = 0; i < facesSparseArray.size(); i++) {

            Face face = facesSparseArray.valueAt(i);

            float left = face.getPosition().x;
            float top = face.getPosition().y;
            float right = left + face.getWidth();
            float bottom = right + face.getHeight();

            float cornerRadius = 2.0f;

            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);

            for (Landmark landmark : face.getLandmarks()) {

                int x = (int) landmark.getPosition().x;
                int y = (int) landmark.getPosition().y;

                float radius = 10.0f;

                canvas.drawCircle(x, y, radius, rectPaint);
            }
        }

        selfieImageView.setImageDrawable(new BitmapDrawable(getResources(), temporaryBitmap));

        faceDetector.release();
    }
}
