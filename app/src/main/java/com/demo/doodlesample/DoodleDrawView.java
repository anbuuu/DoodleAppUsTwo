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



public class DoodleDrawView extends View{


    //drawing path
    private Path drawPath;
    //drawing and canvas paint_canvas
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawingCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float currentBrushSize, previousBrushSize;
    private boolean eraseAction = false;
    private boolean isFilling = false;

    public DoodleDrawView(Context context, AttributeSet attributeSet ) {
        super(context, attributeSet);
        setupDrawingArea();
    }

    private void setupDrawingArea() {

        /*
        We will use the first variable for the brush size and the second to keep track of the
        last brush size used
        when the user switches to the eraser, so that we can revert back to the correct size
        when they decide to switch back to drawing
         */
        currentBrushSize = getResources().getInteger(R.integer.SIZE_MEDIUM);
        previousBrushSize = currentBrushSize;


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
        // Detecting User Touch
        /**
         * , we want user touches on it to register as drawing operations. To do this we need to listen for touch events
         */
        float touchX = event.getX();
        float touchY = event.getY();

        /*
        The MotionEvent parameter to the onTouchEvent method will let us respond to particular touch events.
        The actions we are interested in to implement drawing are down, move and up.
        */

        if ( isFilling) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //FloodFill(new Point((int) touchX, (int) touchY));

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
        //set color
        invalidate();
        // Next parse and set the color for drawing:
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

    /*

        We will be passing the value from the dimensions file when we call this
        method, so we have to calculate its dimension value. We update the variable
        and the Paint object to use the new size.
     */
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
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            this.setColor("#FFFFFFFF");

        } else {
            drawPaint.setXfermode(null);
        }
    }

    private synchronized void FloodFill(Point startPoint) {

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

    // Fill
    public void fillColour() {
        isFilling = true;

    }

    public void startNewDoodle(){
        drawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }


}
