package com.am.birdescape;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Store extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    TextView coins;
    RadioGroup rg;
    int bird,storecoins;
    Button select;
   // RadioButton rb0,rb1,rb2,rb3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        storecoins=(mSharedPreference.getInt("coinstore", 0));

        coins = findViewById(R.id.coins);
        coins.setText(""+storecoins);
        select = findViewById(R.id.select);
        rg = findViewById(R.id.rg);
      /*  rb0 = findViewById(R.id.rb0);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);*/

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = rg.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.rb0:
                         bird= R.drawable.bird0;
                        break;
                    case R.id.rb1:
                         bird= R.drawable.bird1;
                        break;
                    case R.id.rb2:
                         bird= R.drawable.bird2;
                        break;
                    case R.id.rb3:
                         bird= R.drawable.bird3;
                        break;
                    default:
                        // Your code
                        break;
                }
            }
        });



        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Intent intent = new Intent(Store.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void save(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Store.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coinstore", storecoins);
        editor.putInt("bird",bird);
        editor.commit();

    }
}