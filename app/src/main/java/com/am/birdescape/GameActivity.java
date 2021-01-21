package com.am.birdescape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.ads.rewarded.RewardedAd;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "";
    private ImageView bird,enemy1,enemy2,enemy3,coin1,coin2,right1,right2,right3;
    private TextView textViewScore,textViewStartInfo;
    private ConstraintLayout constraintLayout;

    private boolean touchControl = false;
    private boolean beginControl = false;

    private Runnable runnable,runnable2;
    private Handler handler,handler2;
    boolean adsflag = false;
    boolean aflag = false;
    boolean bflag = false;
    boolean cflag = false;
    boolean dflag = false;
    boolean eflag = false;
    boolean fflag = false;

    private RewardedAd mRewardedAd;

    //Positions
    int birdX,enemy1X,enemy2X,enemy3X,coin1X,coin2X;
    int birdY,enemy1Y,enemy2Y,enemy3Y,coin1Y,coin2Y;

    //dimensions of screen
    int screenWidth;
    int screenHeight;

    //remaining right
    int right = 3;

    //points
    int score = 0;
    int i;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
///////////////////////////////////////////////////////reward ads////////////////////////////////////




///////////////////////////////////////////////////////////////////////////////////
        bird = findViewById(R.id.imageViewBird);
        enemy1 = findViewById(R.id.imageViewEnemy1);
        enemy2 = findViewById(R.id.imageViewEnemy2);
        enemy3 = findViewById(R.id.imageViewEnemy3);
        coin1 = findViewById(R.id.imageViewCoin);
        coin2 = findViewById(R.id.imageViewCoin2);
        right1 = findViewById(R.id.right1);
        right2 = findViewById(R.id.right2);
        right3 = findViewById(R.id.right3);
        textViewScore = findViewById(R.id.textViewScore);
        textViewStartInfo = findViewById(R.id.textViewStartInfo);
        constraintLayout = findViewById(R.id.constraintLayout);

        GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
        gamesClient.setViewForPopups(findViewById(android.R.id.content));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textViewStartInfo.setVisibility(View.INVISIBLE);

                if (!beginControl)
                {
                    beginControl = true;

                    screenWidth = (int) constraintLayout.getWidth();
                    screenHeight = (int) constraintLayout.getHeight();

                    birdX = (int) bird.getX();
                    birdY = (int) bird.getY();

                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            moveToBird();
                            enemyControl();
                            collisionControl();
                            achivmentsControl();
                        }
                    };
                    handler.post(runnable);
                }
                else
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        touchControl = true;
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        touchControl = false;
                    }
                }
                return true;
            }
        });
    }

    public void moveToBird()
    {
        if (touchControl)
        {
            birdY = birdY - (screenHeight / 50);
        }
        else
        {
            birdY = birdY + (screenHeight / 50);
        }

        if (birdY <= 0)
        {
            birdY = 0;
        }
        if (birdY >= (screenHeight - bird.getHeight()))
        {
            birdY = (screenHeight - bird.getHeight());
        }

        bird.setY(birdY);
    }


    public void achivmentsControl() {
        /////////////////achivments increments/////////////////////////////////////////
        if (score == 200 &&  aflag ==false)
        {

            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_the_starter), 1);
            aflag = true;
        }
        if (score == 400 &&  bflag ==false)
        {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_the_adventurer), 1);
            bflag = true;
        }
        if (score == 700 &&  cflag ==false)
        {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_the_gamer), 1);
            cflag = true;
        }
        if (score == 900 &&  dflag ==false)
        {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_the_best), 1);
            dflag = true;
        }

        if (score == 1100 &&  eflag ==false)
        {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_the_king), 1);
            eflag = true;
        }

        if (score == 50 &&  fflag ==false)
        {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .increment(getString(R.string.achievement_big_heart), 1);
            fflag = true;
        }


/////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void enemyControl() {
        enemy1.setVisibility(View.VISIBLE);
        enemy2.setVisibility(View.VISIBLE);
        enemy3.setVisibility(View.VISIBLE);
        coin1.setVisibility(View.VISIBLE);
        coin2.setVisibility(View.VISIBLE);


        //
        enemy1X = enemy1X - (screenWidth / 150);
        enemy2X = enemy2X - (screenWidth / 140);
        enemy3X = enemy3X - (screenWidth / 120);
        coin1X = coin1X - (screenWidth / 120);
        coin2X = coin2X - (screenWidth / 110);


        if (score >= 50 && score < 100) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg1));
            enemy1X = enemy1X - (screenWidth / 145);
            enemy2X = enemy2X - (screenWidth / 140);
            enemy3X = enemy3X - (screenWidth / 120);

        }
        if (score >= 100 && score < 200) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg2));
            enemy1X = enemy1X - (screenWidth / 130);
            enemy2X = enemy2X - (screenWidth / 130);
            enemy3X = enemy3X - (screenWidth / 120);
        }
        if (score >= 200 && score < 300) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg3));
            enemy1X = enemy1X - (screenWidth / 120);
            enemy2X = enemy2X - (screenWidth / 120);
            enemy3X = enemy3X - (screenWidth / 120);
            coin1X = coin1X - (screenWidth / 100);
            coin2X = coin2X - (screenWidth / 100);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_1));


        }

        if (score >= 300 && score < 400) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg4));
            enemy1X = enemy1X - (screenWidth / 110);
            enemy2X = enemy2X - (screenWidth / 110);
            enemy3X = enemy3X - (screenWidth / 120);
            coin1X = coin1X - (screenWidth / 100);
            coin2X = coin2X - (screenWidth / 100);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_2));


        }

        if (score >= 400 && score < 500) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg5));
            enemy1X = enemy1X - (screenWidth / 90);
            enemy2X = enemy2X - (screenWidth / 100);
            enemy3X = enemy3X - (screenWidth / 120);
            coin1X = coin1X - (screenWidth / 90);
            coin2X = coin2X - (screenWidth / 90);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_3));

        }

        if (score >= 500 && score < 600) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg6));
            enemy1X = enemy1X - (screenWidth / 80);
            enemy2X = enemy2X - (screenWidth / 90);
            enemy3X = enemy3X - (screenWidth / 100);
            coin1X = coin1X - (screenWidth / 90);
            coin2X = coin2X - (screenWidth / 90);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_4));
        }

        if (score >= 600 && score < 700) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg7));
            enemy1X = enemy1X - (screenWidth / 70);
            enemy2X = enemy2X - (screenWidth / 80);
            enemy3X = enemy3X - (screenWidth / 70);
            coin1X = coin1X - (screenWidth / 90);
            coin2X = coin2X - (screenWidth / 80);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_5));
        }
        if (score >= 700 && score < 800) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg8));
            enemy1X = enemy1X - (screenWidth / 65);
            enemy2X = enemy2X - (screenWidth / 75);
            enemy3X = enemy3X - (screenWidth / 65);
            coin1X = coin1X - (screenWidth / 85);
            coin2X = coin2X - (screenWidth / 90);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_6));

        }
        if (score >= 800 && score < 900) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg9));
            enemy1X = enemy1X - (screenWidth / 60);
            enemy2X = enemy2X - (screenWidth / 70);
            enemy3X = enemy3X - (screenWidth / 60);
            coin1X = coin1X - (screenWidth / 80);
            coin2X = coin2X - (screenWidth / 85);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_7));
        }
        if (score >= 900 && score < 1000) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg10));
            enemy1X = enemy1X - (screenWidth / 50);
            enemy2X = enemy2X - (screenWidth / 60);
            enemy3X = enemy3X - (screenWidth / 50);
            coin1X = coin1X - (screenWidth / 70);
            coin2X = coin2X - (screenWidth / 75);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_8));

        }

        if (score >= 1000 && score < 1100) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg10));
            enemy1X = enemy1X - (screenWidth / 45);
            enemy2X = enemy2X - (screenWidth / 55);
            enemy3X = enemy3X - (screenWidth / 45);
            coin1X = coin1X - (screenWidth / 60);
            coin2X = coin2X - (screenWidth / 65);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_level_9));
        }

        if (score >= 1100) {
            constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg10s));
            enemy1X = enemy1X - (screenWidth / 40);
            enemy2X = enemy2X - (screenWidth / 40);
            enemy3X = enemy3X - (screenWidth / 40);
            coin1X = coin1X - (screenWidth / 50);
            coin2X = coin2X - (screenWidth / 65);
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_bird_helper));

        }

        if (enemy1X < 0) {
            enemy1X = screenWidth + 200;
            enemy1Y = (int) Math.floor(Math.random() * screenHeight);

            if (enemy1Y <= 0) {
                enemy1Y = 0;
            }
            if (enemy1Y >= (screenHeight - enemy1.getHeight())) {
                enemy1Y = (screenHeight - enemy1.getHeight());
            }
        }

        enemy1.setX(enemy1X);
        enemy1.setY(enemy1Y);

        // enemy2

        if (enemy2X < 0) {
            enemy2X = screenWidth + 200;
            enemy2Y = (int) Math.floor(Math.random() * screenHeight);

            if (enemy2Y <= 0) {
                enemy2Y = 0;
            }
            if (enemy2Y >= (screenHeight - enemy2.getHeight())) {
                enemy2Y = (screenHeight - enemy2.getHeight());
            }
        }

        enemy2.setX(enemy2X);
        enemy2.setY(enemy2Y);

        //enemy3


        if (enemy3X < 0) {
            enemy3X = screenWidth + 200;
            enemy3Y = (int) Math.floor(Math.random() * screenHeight);

            if (enemy3Y <= 0) {
                enemy3Y = 0;
            }
            if (enemy3Y >= (screenHeight - enemy3.getHeight())) {
                enemy3Y = (screenHeight - enemy3.getHeight());
            }
        }

        enemy3.setX(enemy3X);
        enemy3.setY(enemy3Y);


        if (coin1X < 0) {
            coin1X = screenWidth + 200;
            coin1Y = (int) Math.floor(Math.random() * screenHeight);

            if (coin1Y <= 0) {
                coin1Y = 0;
            }
            if (coin1Y >= (screenHeight - coin1.getHeight())) {
                coin1Y = (screenHeight - coin1.getHeight());
            }
        }

        coin1.setX(coin1X);
        coin1.setY(coin1Y);


        if (coin2X < 0) {
            coin2X = screenWidth + 200;
            coin2Y = (int) Math.floor(Math.random() * screenHeight);

            if (coin2Y <= 0) {
                coin2Y = 0;
            }
            if (coin2Y >= (screenHeight - coin2.getHeight())) {
                coin2Y = (screenHeight - coin2.getHeight());
            }
        }
        coin2.setX(coin2X);
        coin2.setY(coin2Y);


    }

    public void collisionControl()
    {
        int centerEnemy1X = enemy1X + enemy1.getWidth() / 2;
        int centerEnemy1Y = enemy1Y + enemy1.getHeight() / 2;

        if (centerEnemy1X >= birdX
                && centerEnemy1X <= (birdX + bird.getWidth())
                && centerEnemy1Y >= birdY
                && centerEnemy1Y <= (birdY + bird.getHeight())
        )
        {
            enemy1X = screenWidth + 200;
            right--;
        }

        int centerEnemy2X = enemy2X + enemy2.getWidth() / 2;
        int centerEnemy2Y = enemy2Y + enemy2.getHeight() / 2;

        if (centerEnemy2X >= birdX
                && centerEnemy2X <= (birdX + bird.getWidth())
                && centerEnemy2Y >= birdY
                && centerEnemy2Y <= (birdY + bird.getHeight())
        )
        {
            enemy2X = screenWidth + 200;
            right--;
        }

        int centerEnemy3X = enemy3X + enemy3.getWidth() / 2;
        int centerEnemy3Y = enemy3Y + enemy3.getHeight() / 2;

        if (centerEnemy3X >= birdX
                && centerEnemy3X <= (birdX + bird.getWidth())
                && centerEnemy3Y >= birdY
                && centerEnemy3Y <= (birdY + bird.getHeight())
        )
        {
            enemy3X = screenWidth + 200;
            right--;
        }

        int centerCoin1X = coin1X + coin1.getWidth() / 2;
        int centerCoin1Y = coin1Y + coin1.getHeight() / 2;

        if (centerCoin1X >= birdX
                && centerCoin1X <= (birdX + bird.getWidth())
                && centerCoin1Y >= birdY
                && centerCoin1Y <= (birdY + bird.getHeight())
        )
        {
            coin1X = screenWidth + 200;
            score = score + 10;
            textViewScore.setText(""+score);
        }

        int centerCoin2X = coin2X + coin2.getWidth() / 2;
        int centerCoin2Y = coin2Y + coin2.getHeight() / 2;

        if (centerCoin2X >= birdX
                && centerCoin2X <= (birdX + bird.getWidth())
                && centerCoin2Y >= birdY
                && centerCoin2Y <= (birdY + bird.getHeight())
        )
        {
            coin2X = screenWidth + 200;
            score = score + 10;
            textViewScore.setText(""+score);
        }



        if (right > 0 )
        {
            if (right == 2)
            {
                right1.setImageResource(R.drawable.favorite_grey);
            }
            if (right == 1)
            {
                right2.setImageResource(R.drawable.favorite_grey);
            }
            handler.postDelayed(runnable,20);
        }

        else if (right == 0)
        {   gameover();

        }
    }

    public void gameover(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("Help The Innocent Bird");
        builder.setMessage("Watch ad to get one more life");
        builder.setCancelable(false);
        builder.setNegativeButton("Watch ad", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                right = 1;
                handler.post(runnable);

            }
        });
        builder.setPositiveButton("Game Over", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                handler.removeCallbacks(runnable);
                right3.setImageResource(R.drawable.favorite_grey);
                Intent intent = new Intent(GameActivity.this,ResultActivity.class);
                intent.putExtra("score",score);
                startActivity(intent);
                finish();

            }
        });

        builder.create().show();
    }




}