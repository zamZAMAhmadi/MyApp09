package com.zz.myapp09;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        drawView=findViewById(R.id.drawView);
        /**
         * Option way of getting fullscreen and no title
         * //Set fullscreen
         * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         *        WindowManager.LayoutParams.FLAG_FULLSCREEN);
         *
         * //Set No Title
         * this.requestWindowFeature(Window.FEATURE_NO_TITLE);
         **/
    }

    public void moveLeft(View view) {
        drawView.sprite.setdX(-3);//set horizontal speed to move left
        drawView.pause(); //pause when left button is clicked
    }

    public void moveRight(View view) {
        drawView.sprite.setdX(3);//set horizontal speed to move right
    }

    public void redCheckBoxClicked(View view) {
        setColor();
    }

    public void greenCheckBoxClicked(View view) {
        setColor();
    }
    public void setColor(){
        CheckBox greenCheckBox = findViewById(R.id.greenCheckBox);
        CheckBox redCheckBox = findViewById(R.id.redCheckBox);
        if(redCheckBox.isChecked()){
            if(greenCheckBox.isChecked())
                drawView.sprite.setColor(Color.YELLOW);
            else drawView.sprite.setColor(Color.RED);
        }else if(greenCheckBox.isChecked())
            drawView.sprite.setColor(Color.GREEN);
        else drawView.sprite.setColor(Color.BLUE);
    }
}