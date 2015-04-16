package no.ntnu.idi.watchdogprod.domain;

import java.util.Date;

/**
 * Created by sigurdhf on 16.04.2015.
 */
public class Answer implements Comparable<Answer> {
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

    @Override
    public int compareTo(Answer another) {
        Date thisDate = new Date(this.getDate());
        Date anotherDate = new Date(another.getDate());

        if (thisDate.before(anotherDate)) {
            return 1;
        } else if (thisDate.after(anotherDate)) {
            return -1;
        }

        return 0;
    }
}
