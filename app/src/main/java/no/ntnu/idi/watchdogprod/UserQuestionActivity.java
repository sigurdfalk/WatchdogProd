package no.ntnu.idi.watchdogprod;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class UserQuestionActivity extends ActionBarActivity {
    Button next;
    Button previous;
    TextView questionTitle;
    TextView questionText;
    private int questionCounter;
    ArrayList<UserQuestion> questions;
    UserQuestion current;
    boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_question);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Spørsmål");
        actionBar.setDisplayHomeAsUpEnabled(true);

        next = (Button)findViewById(R.id.question_next_btn);
        previous = (Button)findViewById(R.id.question_previous_btn);
        questionCounter = 0;

        questionTitle = (TextView)findViewById(R.id.question_title);
        questionText = (TextView)findViewById(R.id.question_text);

        first = true;

        next.setOnClickListener(new QuestionButtonListener());
        previous.setOnClickListener(new QuestionButtonListener());

        UserQuestion q1 = new UserQuestion("Sp1","HEi1","lol");
        UserQuestion q2 = new UserQuestion("Sp2","HEi2","lol");
        UserQuestion q3 = new UserQuestion("Sp3","HEi3","lol");
        UserQuestion q4 = new UserQuestion("Sp4","HEi4","lol");

        questions = new ArrayList<>();
        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        questions.add(q4);

    }

    private class QuestionButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.question_next_btn) {
                showQuestions();
            } else if (v.getId() == R.id.question_previous_btn) {

            }
        }
    }

    public void showQuestions() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
