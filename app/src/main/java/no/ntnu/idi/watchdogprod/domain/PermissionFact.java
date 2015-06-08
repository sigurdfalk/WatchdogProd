package no.ntnu.idi.watchdogprod.domain;

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

        for (String permission : permissions) {
            boolean exist = false;

            for (String reqPerm : reqPermissions) {
                if (reqPerm.contains(permission)) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                return false;
            }
        }

        return true;
    }
}
