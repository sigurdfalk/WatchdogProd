package no.ntnu.idi.watchdogprod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sigurdhf on 25.03.2015.
 */
public class PermissionFact {
    private String[] permissions;
    private String header;
    private String fact;

    public PermissionFact(String[] permissions, String header, String fact) {
        this.permissions = permissions;
        this.header = header;
        this.fact = fact;
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
