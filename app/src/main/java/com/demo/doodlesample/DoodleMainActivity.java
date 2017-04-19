package com.demo.doodlesample;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class DoodleMainActivity extends AppCompatActivity implements View.OnClickListener {

    private DoodleDrawView doodleDrawView;
    private ImageButton currPaint;
    private float smallBrush, mediumBrush, largeBrush;
    private static final String TAG = DoodleMainActivity.class.getSimpleName();
    private Toast userInfoToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        doodleDrawView = (DoodleDrawView) findViewById(R.id.doodle_drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);

        // We will use a different drawable image on the button to show that it is currently selected:
        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.drawing_pressed));

        smallBrush = getResources().getInteger(R.integer.SIZE_SMALL);
        mediumBrush = getResources().getInteger(R.integer.SIZE_MEDIUM);
        largeBrush = getResources().getInteger(R.integer.SIZE_LARGE);

        ImageButton drawButton = (ImageButton) findViewById(R.id.img_button_brush);
        drawButton.setOnClickListener(this);
        doodleDrawView.setCurrentBrushSize(mediumBrush);

        // Erase Button
        ImageButton eraseButton = (ImageButton) findViewById(R.id.img_button_eraser);
        eraseButton.setOnClickListener(this);

        // Fill Button
        ImageButton fillButton = (ImageButton) findViewById(R.id.img_button_fill_bg);
        fillButton.setOnClickListener(this);
        //initialiseViews();

        // New Image
        ImageButton newDoodleButton = (ImageButton) findViewById(R.id.img_button_new_doodle);
        newDoodleButton.setOnClickListener(this);

        // Save
        ImageButton saveDoodleButton = (ImageButton) findViewById(R.id.img_button_save_doodle);
        saveDoodleButton.setOnClickListener(this);

        // Set Wallpaper
        ImageButton setWallPaperButton = (ImageButton) findViewById(R.id.img_set_wallpaper);
        setWallPaperButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Build.VERSION.SDK_INT);
            }
        }
    }

    // we can let the user choose colors
    public void paintClicked(View view){
        //use chosen color
        doodleDrawView.setEraseAction(false);
        doodleDrawView.setCurrentBrushSize(doodleDrawView.getPreviousBrushSize());
        //first check that the user has clicked a paint_canvas color that is not the currently selected one:
        if ( view != currPaint) {
            // Update Colour
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            doodleDrawView.setColor(color);

            imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.drawing_pressed));
            currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_canvas));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View v) {

        // Check the on Click for Draw Events
        if (v.getId() == R.id.img_button_brush) {

            // Brush Sizes
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_options);

            /* Identify the Brush Size */
            ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setCurrentBrushSize(smallBrush);
                    doodleDrawView.setPreviousBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setCurrentBrushSize(mediumBrush);
                    doodleDrawView.setPreviousBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setCurrentBrushSize(largeBrush);
                    doodleDrawView.setPreviousBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();

        } else if ( v.getId() == R.id.img_button_eraser) {
            // Choose the eraser Size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_options);

            ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setEraseAction(true);
                    doodleDrawView.setCurrentBrushSize(smallBrush);
                    doodleDrawView.setEraseAction(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setEraseAction(true);
                    doodleDrawView.setCurrentBrushSize(mediumBrush);
                    doodleDrawView.setEraseAction(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doodleDrawView.setEraseAction(true);
                    doodleDrawView.setCurrentBrushSize(largeBrush);
                    /*
                    When the user clicks the draw button and chooses a brush size, we need to set back to drawing in
                     case they have previously been erasing.
                     */
                    doodleDrawView.setEraseAction(false);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if ( v.getId() == R.id.img_button_fill_bg) {

            // Fill background
            doodleDrawView.fillColour();

        } else if ( v.getId() == R.id.img_button_new_doodle) {


            // Create New Doodle
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Doodle");
            newDialog.setMessage("Start new Doodle ? (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    doodleDrawView.startNewDoodle();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();

        } else if ( v.getId() == R.id.img_button_save_doodle) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save doodle");
            saveDialog.setMessage("Save Doodle to Device ? ");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    doodleDrawView.setDrawingCacheEnabled(true);


                    /*
                    We use the insertImage method to attempt to write the image to the media
                    store for images on the device, which should save it to the user gallery.
                    We pass the content resolver, drawing cache for the displayed View, a
                    randomly generated UUID string for the filename with PNG extension
                    and a short description
                     */
                    String saveImageToDevice = MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            doodleDrawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "Doodle");

                    if(saveImageToDevice!=null){
                        userInfoToast = Toast.makeText(getApplicationContext(),
                                "Doodle saved to Images", Toast.LENGTH_SHORT);
                        userInfoToast.show();
                    }
                    else{
                        userInfoToast = Toast.makeText(getApplicationContext(),
                                "Sorry, Doodle Cannot be saved", Toast.LENGTH_SHORT);
                        userInfoToast.show();
                    }
                    doodleDrawView.destroyDrawingCache();

                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        } else if ( v.getId() == R.id.img_set_wallpaper) {


            ///
            AlertDialog.Builder setAsWallPaperDialog = new AlertDialog.Builder(this);
            setAsWallPaperDialog.setTitle("Set Wall Paper");
            setAsWallPaperDialog.setMessage("Set Doodle as Wallpaper ? ");
            setAsWallPaperDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    doodleDrawView.setDrawingCacheEnabled(true);

                    /*
                    We use the insertImage method to attempt to write the image to the media
                    store for images on the device, which should save it to the user gallery.
                    We pass the content resolver, drawing cache for the displayed View, a
                    randomly generated UUID string for the filename with PNG extension
                    and a short description
                     */

                    WallpaperManager customWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        customWallpaperManager.setBitmap(doodleDrawView.getDrawingCache());
                        userInfoToast = Toast.makeText(getApplicationContext(),
                                "Doodle set as WallPaper", Toast.LENGTH_SHORT);
                        userInfoToast.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in Saving as Wall Paper");
                        userInfoToast = Toast.makeText(getApplicationContext(),
                                "Sorry, Doodle cannot be saved as Wallpaper.", Toast.LENGTH_SHORT);
                        userInfoToast.show();

                    }
                    doodleDrawView.destroyDrawingCache();

                }
            });
            setAsWallPaperDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            setAsWallPaperDialog.show();

        }

    }



}
