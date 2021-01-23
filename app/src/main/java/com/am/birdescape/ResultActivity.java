package com.am.birdescape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;


public class ResultActivity extends AppCompatActivity {

    private TextView textViewResultInfo,textViewMyScore,textViewHighestScore;
    private Button buttonAgain,leader,achivments;
    private int score,storecoins ;

    private SharedPreferences sharedPreferences;
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private InterstitialAd mInterstitialAd;
    private String TAG ="";
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
/////////////////////////////////////////////////////////////////////////////////

      loadad();


///////////////////////////////////////////////////////////////////////////////
        textViewHighestScore = findViewById(R.id.textViewHighestScore);
        textViewMyScore = findViewById(R.id.textViewMyScore);
        buttonAgain = findViewById(R.id.buttonAgain);
        achivments = findViewById(R.id.achivements);

        score = getIntent().getIntExtra("score",0);
        textViewMyScore.setText("Your score : "+score);

        sharedPreferences = this.getSharedPreferences("Score", Context.MODE_PRIVATE);
        int highestScore = sharedPreferences.getInt("highestScore",0);


   /*     if (score >= 200)
        {

            textViewHighestScore.setText("Highest Score : "+score);
            sharedPreferences.edit().putInt("highestScore",score).apply();
        }
        else */
        if (score >= highestScore)

        {
            textViewHighestScore.setText("Highest Score : "+score);
            sharedPreferences.edit().putInt("highestScore",score).apply();
        }
        else
        {
            textViewHighestScore.setText("Highest Score : "+highestScore);
        }

        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        storecoins=(mSharedPreference.getInt("coinstore", 0));
        storecoins += score;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ResultActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coinstore", storecoins);
        editor.commit();


        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
               .submitScore(getString(R.string.leaderboard_id), score);



        buttonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ResultActivity.this);
                   flag = true;
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        leader = findViewById(R.id.leaderboard);
        leader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ResultActivity.this);
                    showLeaderboard();
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    showLeaderboard();
                }

                loadad();
            }
        });


        achivments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ResultActivity.this);
                    showAchievements();
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    showAchievements();
                }

                loadad();
            }

        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("Help The Innocent Bird");
        builder.setMessage("Are you sure you want to quit game?");
        builder.setCancelable(false);
        builder.setNegativeButton("Quit game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.create().show();
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

    @Override
    protected void onResume() {
        super.onResume();
             if ( GoogleSignIn.getLastSignedInAccount(this) == null) {
            Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();}

        if (flag) {
            Intent intent = new Intent(ResultActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void loadad() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-8469263715026322/7540364897", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }
        });
    }
}