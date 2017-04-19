package com.demo.doodlesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Draw View for the Application for the user to draw on canvas
 */

public class DoodleDrawView extends View{


    // Drawing Path, Paint Canvas, Initial Paint Color and Bitmap Declarations
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawingCanvas;
    private Bitmap canvasBitmap;

    // State for current and previous brush sizes
    private float currentBrushSize, previousBrushSize;

    // Checking Erase Action
    private boolean eraseAction = false;

    private boolean isFilling = false;

    // Initialise Constructor and setup Drawing Area
    public DoodleDrawView(Context context, AttributeSet attributeSet ) {
        super(context, attributeSet);
        setupDrawingArea();
    }

    private void setupDrawingArea() {


        // Default is Medium Brush
        currentBrushSize = getResources().getInteger(R.integer.SIZE_MEDIUM);
        previousBrushSize = currentBrushSize;

        // Initialise Draw Path and set Stroke width and style
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         *  User Touch as Drawing Operations, listening for touch events
         */

        float touchX = event.getX();
        float touchY = event.getY();

        /*
            Listening for Actions DOWN, MOVE and UP
        */

        if ( isFilling) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    FillBackground(new Point((int) touchX, (int) touchY));
                    invalidate();
                    break;
                default:
                    return true;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawingCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
        }

        // Calling invalidate method will cause the  OnDraw method to execute
        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        // Parse and set the Color for Drawing
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setCurrentBrushSize(float newSize) {
        // Update the Brush Size with the passed Value
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        currentBrushSize =pixelAmount;
        drawPaint.setStrokeWidth(currentBrushSize);

    }

    // Method to calculate its dimension value and update to its new size
    public void setPreviousBrushSize(float lastSize) {
        previousBrushSize = lastSize;
    }

    public float getPreviousBrushSize() {
        return previousBrushSize;
    }

    public void setEraseAction(boolean isErase){
        //set eraseAction true or false .. We assume user is not erasing first
        eraseAction = isErase;

        //Now alter the Paint object to eraseAction or switch back to drawing
        if (eraseAction) {
            this.setColor("#FFFFFFFF");
        } else {
            drawPaint.setXfermode(null);
        }
    }

    /*
        Background Fill for the View
     */
    private synchronized void FillBackground(Point startPoint) {

        Queue<Point> queue = new LinkedList<>();
        queue.add(startPoint);

        int targetColor = canvasBitmap.getPixel(startPoint.x, startPoint.y);

        while (queue.size() > 0) {
            Point nextPoint = queue.poll();
            if (canvasBitmap.getPixel(nextPoint.x, nextPoint.y) != targetColor)
                continue;

            Point point = new Point(nextPoint.x + 1, nextPoint.y);

            while ((nextPoint.x > 0) && (canvasBitmap.getPixel(nextPoint.x, nextPoint.y) == targetColor)) {
                canvasBitmap.setPixel(nextPoint.x, nextPoint.y, paintColor);
                if ((nextPoint.y > 0) && (canvasBitmap.getPixel(nextPoint.x, nextPoint.y - 1) == targetColor))
                    queue.add(new Point(nextPoint.x, nextPoint.y - 1));
                if ((nextPoint.y < canvasBitmap.getHeight() - 1)
                        && (canvasBitmap.getPixel(nextPoint.x, nextPoint.y + 1) == targetColor))
                    queue.add(new Point(nextPoint.x, nextPoint.y + 1));
                nextPoint.x--;
            }

            while ((point.x < canvasBitmap.getWidth() - 1)
                    && (canvasBitmap.getPixel(point.x, point.y) == targetColor)) {
                canvasBitmap.setPixel(point.x, point.y, paintColor);

                if ((point.y > 0) && (canvasBitmap.getPixel(point.x, point.y - 1) == targetColor))
                    queue.add(new Point(point.x, point.y - 1));
                if ((point.y < canvasBitmap.getHeight() - 1)
                        && (canvasBitmap.getPixel(point.x, point.y + 1) == targetColor))
                    queue.add(new Point(point.x, point.y + 1));
                point.x++;
            }
        }

        isFilling = false;
    }

    // Checking the Mode of Operation
    public void fillColour() {
        isFilling = true;

    }

    // Create new Drawing
    public void startNewDoodle(){
        drawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
