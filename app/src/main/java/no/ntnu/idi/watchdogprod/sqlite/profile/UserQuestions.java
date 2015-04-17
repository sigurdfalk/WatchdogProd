package no.ntnu.idi.watchdogprod.sqlite.profile;

/**
 * Created by fredsten on 14.04.2015.
 */
public class UserQuestions {

    private long id;
    private String timestamp;
    private String question;
    private String answer;

    public UserQuestions(long id, String timestamp, String event, String value, String packageName) {
        this.id = id;
        this.timestamp = timestamp;
        this.question = event;
        this.answer = value;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

}
