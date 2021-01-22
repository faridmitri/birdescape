package com.am.birdescape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AndroApp extends AppCompatActivity {

    Button support,moreapp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andro_app);

        support = findViewById(R.id.support);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"andro.app@yahoo.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Math Quiz");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AndroApp.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        moreapp = findViewById(R.id.moreapp);
        moreapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzc1NDk2NDgwODkwNDQ1MDU1MTIQCBgDEhkKEzc1NDk2NDgwODkwNDQ1MDU1MTIQCBgDGAA%3D:S:ANO1ljIK-nU&gsr=CjuKAzgKGQoTNzU0OTY0ODA4OTA0NDUwNTUxMhAIGAMSGQoTNzU0OTY0ODA4OTA0NDUwNTUxMhAIGAMYAA%3D%3D:S:ANO1ljJJ_JU"));
                startActivity(browserIntent);
            }
        });

    }
}