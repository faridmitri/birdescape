package com.am.birdescape;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ResultActivity extends AppCompatActivity {

    private TextView textViewResultInfo,textViewMyScore,textViewHighestScore;
    private Button buttonAgain,leader,achivments;
    private int score;

    private SharedPreferences sharedPreferences;
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        textViewHighestScore = findViewById(R.id.textViewHighestScore);
        textViewMyScore = findViewById(R.id.textViewMyScore);
        buttonAgain = findViewById(R.id.buttonAgain);
        achivments = findViewById(R.id.achivements);

        score = getIntent().getIntExtra("score",0);
        textViewMyScore.setText("Your score : "+score);

        sharedPreferences = this.getSharedPreferences("Score", Context.MODE_PRIVATE);
        int highestScore = sharedPreferences.getInt("highestScore",0);

        if (score >= 200)
        {

            textViewHighestScore.setText("Highest Score : "+score);
            sharedPreferences.edit().putInt("highestScore",score).apply();
        }
        else if (score >= highestScore)
        {
            textViewHighestScore.setText("Highest Score : "+score);
            sharedPreferences.edit().putInt("highestScore",score).apply();
        }
        else
        {
            textViewHighestScore.setText("Highest Score : "+highestScore);
        }

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
               .submitScore(getString(R.string.leaderboard_id), score);



        buttonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        leader = findViewById(R.id.leaderboard);
        leader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaderboard();
            }
        });


        achivments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAchievements();
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
}