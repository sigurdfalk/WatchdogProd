package no.ntnu.idi.watchdogprod.domain;

/**
 * Created by fredsten on 20.03.2015.
 */
public class ProfileEvent {
    private long id;
    private String timestamp;
    private String event;
    private String value;
    private String packageName;

    public ProfileEvent(long id, String timestamp, String event, String value, String packageName) {
        this.id = id;
        this.timestamp = timestamp;
        this.event = event;
        this.value = value;
        this.packageName = packageName;
    }

    public String getEvent() {
        return event;
    }

    public String getValue() {
        return value;
    }

    public String getPackageName() {
        return packageName;
    }
}
