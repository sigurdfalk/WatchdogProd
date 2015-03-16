package no.ntnu.idi.watchdogprod;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sigurdhf on 16.03.2015.
 */
public class PrivacyNoticeActivity extends Activity {
    public static final String FILE_PRIVACY_NOTICE = "privacynotice.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_notice);

        final Button accept = (Button) findViewById(R.id.privacy_notice_accept);
        accept.setEnabled(false);
        Button decline = (Button) findViewById(R.id.privacy_notice_decline);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PrivacyNoticeActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyNoticeActivity.this.finish();
            }
        });

        TextView privacyNotice = (TextView) findViewById(R.id.privacy_notice_text);
        privacyNotice.setText(getPrivacyNotice());

        InteractiveScrollView scrollView = (InteractiveScrollView) findViewById(R.id.privacy_notice_scrollView);
        scrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                accept.setEnabled(true);
            }
        });
    }

    private String getPrivacyNotice() {
        StringBuilder text = new StringBuilder();

        try {
            AssetManager assetManager = this.getAssets();
            InputStream is = assetManager.open(FILE_PRIVACY_NOTICE);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //ToDo implement error handling
        }

        return text.toString();
    }
}
