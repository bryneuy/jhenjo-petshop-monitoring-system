package com.example.finaltest_version_one;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finaltest_version_one.Main.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {
    //Time allotted for Splash Screen ---> 1000millis = 1s
    private static int SPLASH_SCREEN = 3500;

    //Splash Screen
    Animation sTopAnim,sBottomAnim;
    ImageView sLogo;
    TextView sTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        //For Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Animation
        sTopAnim = AnimationUtils.loadAnimation(this, R.anim.splash_top_anim);
        sBottomAnim = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_anim);

        //Hooks for Splash Screen
        sLogo = findViewById(R.id.logo);
        sTagline = findViewById(R.id.tagline);

        //Applying Animation
        sLogo.setAnimation(sTopAnim);
        sTagline.setAnimation(sBottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();



            }
        },SPLASH_SCREEN);


    }
}
