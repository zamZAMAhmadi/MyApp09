package com.zz.myapp09;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawView extends SurfaceView {
    SurfaceHolder surface;
    Paint paint=new Paint();
    Sprite sprite = new Sprite();
    Sprite foodSprite, badSprite;
    ArrayList<Explosion> explosions = new ArrayList<>();
    Bitmap explosionBMP = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
    Canvas canvas;
    Bitmap bgBMP = BitmapFactory.decodeResource(getResources(), R.drawable.tree2);
    boolean isRunning=true;
    int frames=0;
    private static final int MAX_STREAMS=100;
    private int soundIdExplosion;
    private int soundIdBackground;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;
    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        surface=getHolder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long lastTime = System.nanoTime(); // get current time to the nanosecond
                double amountOfTicks = 60; // set the number of updates per second
                double ns = 1000000000 / amountOfTicks; // this determines how many times we can devide 60 into 1e9 of nano seconds or about 1 second
                long timer = System.currentTimeMillis(); // get current time
                int updates = 0; // set frame variable
                while(true){
                    long now = System.nanoTime(); // get current time in nonoseconds durring current loop
                    if(now - lastTime<ns){//if running fast
                        try{
                            Thread.sleep((long)((ns - (now-lastTime)))/1000000);//pause until time for next update
                        }catch(Exception e){}
                    }
                    lastTime = System.nanoTime();  // set lastTime to current time to mark beginning of next loop
                    if(isRunning){
                        if(!surface.getSurface().isValid())continue;
                        canvas = surface.lockCanvas();//lock canvas
                        synchronized (getHolder()){
                            update(canvas);
                            onDraw(canvas);
                        }
                        surface.unlockCanvasAndPost(canvas);//unlock the canvas
                    }
                    updates++; // note that a frame has passed
                    if(System.currentTimeMillis() - timer > 1000 ){ // if one second has passed
                        timer+= 1000; // add a thousand to our timer for next time
                        System.out.println("UPS: " +updates +" FPS: "+frames); // print out how many frames have happend in the last second
                        updates = 0; // reset the update count for the next second
                        frames = 0;// reset the frame count for the next second
                    }
                }
            }
        }).start();
        sprite.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mycharacter));
        sprite.grow(100);
        initSoundPool();//pre-load sounds
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //note, at this point, getWidth() and getHeight() will have access the the dimensions
        foodSprite = generateSprite();
        badSprite = generateSprite();
        badSprite.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GRAY);//set paint to gray
        canvas.drawBitmap(bgBMP,null, new Rect(getLeft(),0,getRight(),getBottom()),paint);//paint background gray
        paint.setColor(Color.RED);//set paint to red
        //sprite draws itself
        sprite.draw(canvas);
        foodSprite.draw(canvas);
        badSprite.draw(canvas);
        for(Explosion e: explosions){//draw each explosion
            e.draw(canvas);
        }
        frames++;
    }
    public void update(Canvas canvas){
        if(canvas==null)return;
        //sprite updates itself
        sprite.update(canvas);
        foodSprite.update(canvas);
        badSprite.update(canvas);
        for(int i=explosions.size()-1;i>=0;i--){
            explosions.get(i).update();
        }
        if(RectF.intersects(sprite, foodSprite)){
            //on collision generate an explosion and add itself to list
            new Explosion(foodSprite,explosions,explosionBMP);
            playSoundExplosion();
            foodSprite=generateSprite();
            sprite.grow(10);
        }
        if(RectF.intersects(sprite, badSprite)){
            new Explosion(badSprite,explosions,explosionBMP);
            playSoundExplosion();
            badSprite=generateSprite();
            badSprite.setColor(Color.RED);
            sprite.grow(-5);
        }
        if(RectF.intersects(foodSprite, badSprite)){
            foodSprite.grow((int)(-foodSprite.width()*.1));//shrink food
            badSprite=generateSprite();//recreate badSprite
            badSprite.setColor(Color.RED);
        }
    }
    private Sprite generateSprite(){
        float x = (float)(Math.random()*(getWidth()-.1*getWidth()));
        float y = (float)(Math.random()*(getHeight()-.1*getHeight()));
        int dX = (int)(Math.random()*11-5);
        int dY = (int)(Math.random()*11-5);
        return new Sprite(x,y,x+.1f*getWidth(),y+.1f*getWidth(),dX,dY,Color.RED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(badSprite.contains(event.getX(),event.getY())){
                badSprite=generateSprite();
                badSprite.setColor(Color.RED);
            }
        }
        return true;
    }

    public void pause() {//pause-resume
        isRunning=!isRunning;
    }
    private void initSoundPool()  {
        // With Android API >= 21.
        if (Build.VERSION.SDK_INT >= 21 ) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);
            this.soundPool = builder.build();
        }
        // With Android API < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        // When SoundPool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
                // Playing background sound.
                playSoundBackground();
            }
        });
        // Load the sound background.mp3 into SoundPool
        soundIdBackground= soundPool.load(this.getContext(), R.raw.background,1);
        // Load the sound explosion.wav into SoundPool
        soundIdExplosion = soundPool.load(this.getContext(), R.raw.explosion,1);
    }
    public void playSoundExplosion()  {
        if(soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            // Play sound explosion.wav
            int streamId = this.soundPool.play(this.soundIdExplosion,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
    public void playSoundBackground()  {
        if(soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            // Play sound background.mp3
            int streamId = this.soundPool.play(this.soundIdBackground,leftVolumn, rightVolumn, 1, -1, 1f);
        }
    }
}