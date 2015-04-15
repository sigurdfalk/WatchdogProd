package no.ntnu.idi.watchdogprod.domain;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by fredsten on 27.03.2015.
 */
public class ProfileBehavior {
    final int STATUS_GOOD = 3;
    final int STATUS_NEUTRAL = 2;
    final int STATUS_NEGATIV = 1;
    private Image icon;
    private String title;
    private String text;
    private int status;

    public ProfileBehavior(Image icon, String title, String text, int status) {
        this.icon = icon;
        this.title = title;
        this.text = text;
        this.status = status;
    }

    public Image getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getStatus() {
        return status;
    }

}
