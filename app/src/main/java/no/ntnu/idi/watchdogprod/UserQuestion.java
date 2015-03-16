package no.ntnu.idi.watchdogprod;

/**
 * Created by fredsten on 16.03.2015.
 */
public class UserQuestion {
    private String title;
    private String text;
    private String answer;

    public UserQuestion(String title, String text, String answer) {
        this.title = title;
        this.text = text;
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }
}
