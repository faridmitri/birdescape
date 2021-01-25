package com.am.birdescape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class Store extends AppCompatActivity  {
    private SharedPreferences sharedPreferences;
    TextView coins,numheart;
    RadioGroup rg;
    int bird = R.drawable.bird0,storecoins,hearts=0,id;
    Button extraheart,ads,doublescore,doublecoins;
    AdRequest adRequest;
    RadioButton rb0,rb1,rb2,rb3,rb4;
   private RewardedAd mRewardedAd;
    private String TAG = "MainActivity";
    Boolean dscore = false,dcoins = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

    ////////////////////////////////////////////////////////////////////////////////
         adRequest = new AdRequest.Builder().build();

        loadad();

////////////////////////////////////////////////////////////////////////////////

        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        bird=(mSharedPreference.getInt("bird",R.id.rb0));
        id=(mSharedPreference.getInt("id",R.id.rb0));
        storecoins=(mSharedPreference.getInt("coinstore", 0));
        hearts=(mSharedPreference.getInt("heart", 0));
        dscore=(mSharedPreference.getBoolean("dscore", false));
        dscore=(mSharedPreference.getBoolean("dcoins", false));


        coins = findViewById(R.id.coins);
        coins.setText(""+storecoins);
        rg = findViewById(R.id.rg);


        rb0 = findViewById(R.id.rb0);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb3);
        rg.check(id);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                 id = rg.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.rb0:
                         bird= R.drawable.bird0;

                        break;
                    case R.id.rb1:
                         bird= R.drawable.bird1;

                        break;
                    case R.id.rb2:
                         bird= R.drawable.bird2;
                        rb2.setChecked(true);
                        break;
                    case R.id.rb3:
                         bird= R.drawable.bird3;

                        break;
                    case R.id.rb4:
                        bird= R.drawable.bird4;

                        break;
                    default:
                        bird= R.drawable.bird0;
                        break;
                }
            }
        });

        extraheart = findViewById(R.id.extraheart);
        numheart = findViewById(R.id.numheart);
        if (storecoins < 100000){extraheart.setEnabled(false);}
        if (hearts >0)
       {
            numheart.setVisibility(View.VISIBLE);
            numheart.setText("More "+hearts+" Life");
        }
        extraheart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storecoins = storecoins - 100000;
                coins.setText(""+storecoins);
                hearts = hearts +1;
                numheart.setText(""+hearts);
                if (storecoins < 10){extraheart.setEnabled(false);}
                if (hearts >0)
                {
                    numheart.setVisibility(View.VISIBLE);
                    numheart.setText("More "+hearts+" Life");
                }
                save();

            }
        });

        ads = findViewById(R.id.ads);
        ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });


        doublescore = findViewById(R.id.doublescore);
        if (storecoins < 200000 || dscore == true){doublescore.setEnabled(false);}
        doublescore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storecoins = storecoins - 200000;
                coins.setText(""+storecoins);
                dscore = true;
                if (storecoins < 200000 || dscore == true){doublescore.setEnabled(false);}
                save();
            }
        });

        doublecoins = findViewById(R.id.doublecoins);
        if (storecoins < 10 || dcoins == true){doublecoins.setEnabled(false);}
        doublecoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storecoins = storecoins - 10;
                coins.setText(""+storecoins);
                dcoins = true;
                if (storecoins < 10 || dcoins == true){doublecoins.setEnabled(false);}
                save();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                save();
                Intent intent = new Intent(Store.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void save(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Store.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coinstore", storecoins);
        editor.putInt("bird",bird);
        editor.putInt("id",id);
        editor.putInt("heart",hearts);
        editor.putBoolean("dscore",dscore);
        editor.putBoolean("dcoins",dcoins);
        editor.commit();

    }

public void loadad() {
    RewardedAd.load(this, "ca-app-pub-8469263715026322/3696450663",
            adRequest, new RewardedAdLoadCallback(){
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error.
                    Log.d(TAG, loadAdError.getMessage());
                    mRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad was shown.");
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.d(TAG, "Ad failed to show.");
                            Toast.makeText(getApplicationContext(),"Video failed to load.",Toast.LENGTH_SHORT).show();
                            loadad();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            Log.d(TAG, "Ad was dismissed.");
                            loadad();
                        }
                    });
                }
            });
}

public void show(){
    if (mRewardedAd != null) {
        Activity activityContext = Store.this;
        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                // Handle the reward.
                Log.d("TAG", "The user earned the reward.");
                storecoins = storecoins + 10;
                coins.setText(""+storecoins);
                save();
                loadad();
                if (storecoins < 100000){extraheart.setEnabled(false);} else{extraheart.setEnabled(true);}
                if (storecoins < 200000 || dscore == true){doublescore.setEnabled(false);}else {doublescore.setEnabled(true);}
                if (storecoins < 300000){doublecoins.setEnabled(false);} else{doublecoins.setEnabled(true);}
            }
        });
    } else {
        Toast.makeText(getApplicationContext(),"The rewarded ad wasn't ready yet.",Toast.LENGTH_SHORT).show();
        loadad();
    }
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
        Intent intent = new Intent(Store.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}