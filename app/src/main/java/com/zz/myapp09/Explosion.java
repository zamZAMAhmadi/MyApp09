package com.zz.myapp09;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

public class Explosion extends Sprite {
    private int duration=100;
    private ArrayList explosions;
    private Bitmap bitmap;
    private static final int BMP_COLUMNS = 5;
    private static final int BMP_ROWS = 5;
    private int currentFrame=0, iconWidth, iconHeight, animationDelay=20;
    public Explosion(RectF r, ArrayList<Explosion> list, Bitmap i){
        super(r);
        explosions=list;
        explosions.add(this);//adds itself to list of explosions
        bitmap=i;
    }
    public void update(){
        //duration and self removal
        duration--;
        if(duration<=0) {
            explosions.remove(this);//self removal from list
        }
        if(animationDelay--<0) {//increment to next sprite image after delay
            currentFrame = ++currentFrame % BMP_COLUMNS;//cycles current image with boundary proteciton
            animationDelay=20;//arbitrary delay before cycling to next image
        }
    }
    public void draw(Canvas canvas){
        iconWidth = bitmap.getWidth() / BMP_COLUMNS;//calculate width of 1 image
        iconHeight = bitmap.getHeight() / BMP_ROWS; //calculate height of 1 image
        int srcX = currentFrame * iconWidth;       //set x of source rectangle inside of bitmap based on current frame
        int srcY = getAnimationRow() * iconHeight; //set y to row of bitmap based on direction
        Rect src = new Rect(srcX, srcY, srcX + iconWidth, srcY + iconHeight);  //defines the rectangle inside of heroBmp to displayed
        canvas.drawBitmap(bitmap,src, this,null); //draw an image
    }
    private int getAnimationRow() {
        return currentFrame/BMP_ROWS;//int division to get current row
    }
}
