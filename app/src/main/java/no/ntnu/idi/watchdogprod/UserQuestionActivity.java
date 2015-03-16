package no.ntnu.idi.watchdogprod;

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


public class UserQuestionActivity extends ActionBarActivity {
    Button okAndSubmit;
    Button decline;
    TextView questionInfoTitle;
    TextView questionInfoText;
    ScrollView scrollView;


    SeekBar seekBarQ1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_question);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Spørsmål");

        questionInfoTitle = (TextView)findViewById(R.id.question_info_title);
        questionInfoText = (TextView)findViewById(R.id.question_info_text);

        seekBarQ1 = (SeekBar)findViewById(R.id.seekbar_q1);
        seekBarQ1.setProgress(3);
        seekBarQ1.incrementProgressBy(1);
        seekBarQ1.setMax(4);

        scrollView =(ScrollView)findViewById(R.id.question_scrollview);

        okAndSubmit = (Button)findViewById(R.id.question_next_btn);
        decline = (Button)findViewById(R.id.question_previous_btn);

        okAndSubmit.setOnClickListener(new QuestionButtonListener());
        decline.setOnClickListener(new QuestionButtonListener());
    }

    private class QuestionButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.question_next_btn) {
                showQuestions();
            } else if (v.getId() == R.id.question_previous_btn) {
                UserQuestionActivity.this.finish();

            }
        }
    }

    public void showQuestions() {
        questionInfoTitle.setVisibility(View.GONE);
        questionInfoText.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
        okAndSubmit.setText("Lagre");
        scrollView.setVisibility(View.VISIBLE);

    }
}
