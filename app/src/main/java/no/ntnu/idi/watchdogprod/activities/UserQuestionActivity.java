package no.ntnu.idi.watchdogprod.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;

import no.ntnu.idi.watchdogprod.R;


public class UserQuestionActivity extends ActionBarActivity {
    Button agree;
    Button decline;
    Button submit;
    TextView questionInfoTitle;
    TextView questionInfoText;
    ScrollView scrollView;

    SeekBar seekBarQ1;
    SeekBar seekBarQ2;
    SeekBar seekBarQ3;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_question);

        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Spørsmål");

        questionInfoText = (TextView) findViewById(R.id.question_info_text);

        seekBarQ1 = (SeekBar) findViewById(R.id.seekbar_q1);
        seekBarQ1.setProgress(1);
        seekBarQ1.incrementProgressBy(1);
        seekBarQ1.setMax(4);

        seekBarQ2 = (SeekBar) findViewById(R.id.seekbar_q2);
        seekBarQ2.setProgress(1);
        seekBarQ2.incrementProgressBy(1);
        seekBarQ2.setMax(4);

        seekBarQ3 = (SeekBar) findViewById(R.id.seekbar_q3);
        seekBarQ3.setProgress(1);
        seekBarQ3.incrementProgressBy(1);
        seekBarQ3.setMax(4);

        scrollView = (ScrollView) findViewById(R.id.question_scrollview);

        agree = (Button) findViewById(R.id.question_agree_btn);
        decline = (Button) findViewById(R.id.question_cancel_btn);
        submit = (Button) findViewById(R.id.questions_submit_btn);

        agree.setOnClickListener(new QuestionButtonListener());
        decline.setOnClickListener(new QuestionButtonListener());
        submit.setOnClickListener(new QuestionButtonListener());
    }

    private class QuestionButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.question_agree_btn) {
                showQuestions();
            } else if (v.getId() == R.id.question_cancel_btn) {
                finish();
            } else if (v.getId() == R.id.questions_submit_btn) {
                submitAnswers();
                storeAnsweredState();
                finish();
            }
        }
    }

    public void storeAnsweredState() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = settings.edit();
        edit.putBoolean("answeredQuestions", true);
        edit.apply();
    }

    public void showQuestions() {
        questionInfoText.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    public void submitAnswers() {
        ProfileDataSource profileDataSource = new ProfileDataSource(this);
        int q1 = seekBarQ1.getProgress();
        int q2 = seekBarQ2.getProgress();
        int q3 = seekBarQ3.getProgress();
        profileDataSource.open();
        long insert =  profileDataSource.insertUserQuestions(q1,q2,q3);
        profileDataSource.close();
        if(insert == -1) {
            Toast.makeText(this, "DB-FEIL", Toast.LENGTH_LONG).show();
        }

    }
}
