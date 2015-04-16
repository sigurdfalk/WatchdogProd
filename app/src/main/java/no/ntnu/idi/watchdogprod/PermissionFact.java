package no.ntnu.idi.watchdogprod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sigurdhf on 25.03.2015.
 */
public class PermissionFact {
    private int id;
    private String[] permissions;
    private String header;
    private String fact;

    public PermissionFact(int id, String[] permissions, String header, String fact) {
        this.id = id;
        this.permissions = permissions;
        this.header = header;
        this.fact = fact;
    }

    public int getId() {
        return id;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String getHeader() {
        return header;
    }

    public String getFact() {
        return fact;
    }

    public boolean matchesApp(String[] reqPermissions) {
        List<String> reqPermList = Arrays.asList(reqPermissions);

        for (String permission : permissions) {
            if (!reqPermList.contains(permission)) {
                return false;
            }
        }

        return true;
    }
}
