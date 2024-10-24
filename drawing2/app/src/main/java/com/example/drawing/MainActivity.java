package com.example.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private SeekBar brushSizeSeekBar;
    private Button colorPickerButton, clearButton;
    private int currentColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout drawingFrame = findViewById(R.id.drawing_frame);
        drawingView = new DrawingView(this, null);
        drawingFrame.addView(drawingView);

        brushSizeSeekBar = findViewById(R.id.brush_size_seekbar);
        colorPickerButton = findViewById(R.id.color_picker_button);
        clearButton = findViewById(R.id.clear_button); // Find the clear button

        drawingView.setBrushSize(brushSizeSeekBar.getProgress());

        brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        colorPickerButton.setOnClickListener(v -> openColorPicker());

        clearButton.setOnClickListener(v -> drawingView.clearCanvas());
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                drawingView.setColor(currentColor);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        colorPicker.show();
    }

    public class DrawingView extends View {

        private Path drawPath;
        private Paint drawPaint, canvasPaint;
        private Canvas drawCanvas;
        private Bitmap canvasBitmap;

        public DrawingView(MainActivity context, AttributeSet attrs) {
            super(context, attrs);
            setupDrawing();
        }

        private void setupDrawing() {
            drawPath = new Path();
            drawPaint = new Paint();
            drawPaint.setColor(currentColor);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(10);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
            canvasPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }

        public void setColor(int newColor) {
            drawPaint.setColor(newColor);
        }

        public void setBrushSize(float newSize) {
            drawPaint.setStrokeWidth(newSize);
        }

        public void clearCanvas() {
            drawCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
            invalidate(); // Refresh the view
        }
    }
}