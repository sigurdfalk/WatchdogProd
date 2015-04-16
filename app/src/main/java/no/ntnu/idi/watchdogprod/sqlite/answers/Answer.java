package no.ntnu.idi.watchdogprod.sqlite.answers;

/**
 * Created by sigurdhf on 16.04.2015.
 */
public class Answer {
    private long id;
    private int answerId;
    private long date;
    private String packageName;
    private int answer;

    public Answer(long id, int answerId, long date, String packageName, int answer) {
        this.id = id;
        this.answerId = answerId;
        this.date = date;
        this.packageName = packageName;
        this.answer = answer;
    }

    public long getId() {
        return id;
    }

    public int getAnswerId() {
        return answerId;
    }

    public long getDate() {
        return date;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getAnswer() {
        return answer;
    }
}
