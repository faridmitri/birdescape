package com.am.birdescape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class MainActivity extends AppCompatActivity {

    private ImageView bird,enemy1,enemy2,enemy3,coin,volume,leader,achiv,androapp,store;
    private Button buttonStart;
    private Animation animation;
    private MediaPlayer mediaPlayer;
    boolean status = false;
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    long installTimeInMilliseconds; // install time is conveniently provided in milliseconds
    private ReviewManager reviewManager;
    private ReviewInfo reviewInfo;
    int birdimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)).setViewForPopups(this.findViewById(android.R.id.content));
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        birdimg =(mSharedPreference.getInt("bird", R.drawable.bird0));

        reviewManager = ReviewManagerFactory.create(this);
        getInstallDate();

        long l =currentTimeMillis();
        if (  installTimeInMilliseconds + (86400000 * 3) < l)
        {
            Task<ReviewInfo> request = reviewManager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                    flow.addOnCompleteListener(taskdone -> {
                        // This is the next follow of your app
                    });
                }
            });
        }




       androapp = findViewById(R.id.androapp);
        store = findViewById(R.id.store);
        bird = findViewById(R.id.bird);
        enemy1 = findViewById(R.id.enemy1);
        enemy2 = findViewById(R.id.enemy2);
        enemy3 = findViewById(R.id.enemy3);
        coin = findViewById(R.id.coin);
        volume = findViewById(R.id.volume);
        buttonStart = findViewById(R.id.buttonStart);
        leader = findViewById(R.id.leaderboardimg);
        achiv = findViewById(R.id.achivmentimg);
        animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale_animation);
        bird.setImageResource(birdimg);
        bird.setAnimation(animation);
        enemy1.setAnimation(animation);
        enemy2.setAnimation(animation);
        enemy3.setAnimation(animation);
        coin.setAnimation(animation);

        mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.audio_for_game);
        volume.setImageResource(R.drawable.volume_up);
        mediaPlayer.start();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bird",birdimg);
        editor.commit();
    }



    @Override
    protected void onResume() {
        super.onResume();




        if ( GoogleSignIn.getLastSignedInAccount(this) == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            mediaPlayer.stop();
            finish();}

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!status)
                {
                    mediaPlayer.setVolume(0,0);
                    volume.setImageResource(R.drawable.volume_off);
                    status = true;
                }
                else
                {
                    mediaPlayer.setVolume(1,1);
                    volume.setImageResource(R.drawable.volume_up);
                    status = false;
                }

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                volume.setImageResource(R.drawable.volume_up);
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("status",status);
                startActivity(intent);
                finish();
            }
        });

        leader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLeaderboard();
              //  mediaPlayer.reset();

            }
        });

        achiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAchievements();
               // mediaPlayer.reset();

            }
        });

        androapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                volume.setImageResource(R.drawable.volume_up);
                Intent intent = new Intent(MainActivity.this,AndroApp.class);
                startActivity(intent);

            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                volume.setImageResource(R.drawable.volume_up);
                Intent intent = new Intent(MainActivity.this,Store.class);
                startActivity(intent);

            }
        });
}

    private void showLeaderboard() {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    private void showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }

    private String getInstallDate() {
        // get app installation date
        PackageManager packageManager =  this.getPackageManager();

        Date installDate = null;
        String installDateString = null;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            installTimeInMilliseconds = packageInfo.firstInstallTime;


        }
        catch (PackageManager.NameNotFoundException e) {
            // an error occurred, so display the Unix epoch
            installDate = new Date(0);
            installDateString = installDate.toString();
        }
        return installDateString;
    }
}

