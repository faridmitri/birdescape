package com.am.birdescape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.am.birdescape.play.Play_GameActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG ="" ;
    Button login,play;
    private FirebaseAuth mAuth;
    private Animation animation;
    private ImageView bird;
    private InterstitialAd mInterstitialAd;
    ProgressBar progressBar;
Boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

///////////////////////////////////////////////////////////////////////////////////////
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();


        InterstitialAd.load(this,"ca-app-pub-8469263715026322/2652437089", adRequest, new InterstitialAdLoadCallback() {
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
            }
        });


 /////////////////////////////////////////////////////////////////////////////////////////
 progressBar = findViewById(R.id.progressBar);
        bird = findViewById(R.id.imageView);
        animation = AnimationUtils.loadAnimation(LoginActivity.this,R.anim.scale_animation);
        bird.setAnimation(animation);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .requestServerAuthCode(getString(R.string.default_web_client_id)).build());

          /*      signInOptions  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build(); */



        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInIntent();
            }
        });

        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(LoginActivity.this);
                    flag = true;
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    Intent intent = new Intent(LoginActivity.this, Play_GameActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && GoogleSignIn.getLastSignedInAccount(this) != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            signInSilently();}

        if (flag) {
            Intent intent = new Intent(LoginActivity.this, Play_GameActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void startSignInIntent() {

        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //  onConnected(account);
                firebaseAuthWithPlayGames(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage() + " make sure you have the latest version of Google Play Games";
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                // onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

    private void signInSilently() {
        Log.d(TAG, "signInSilently()");
        login.setEnabled(false);
        play.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);


        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            //  onConnected(task.getResult());
                            firebaseAuthWithPlayGames(task.getResult());
                            login.setEnabled(true);
                            play.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            // onDisconnected();
                            // startSignInIntent();
                            login.setEnabled(true);
                            play.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    private void firebaseAuthWithPlayGames(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithPlayGames:" + acct.getId());

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = PlayGamesAuthProvider.getCredential(acct.getServerAuthCode());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }


}