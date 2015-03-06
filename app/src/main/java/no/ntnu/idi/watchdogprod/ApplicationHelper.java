package no.ntnu.idi.watchdogprod;

        import android.content.Context;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationHelper {
    private static ArrayList<PackageInfo> thirdPartyApplications;

    public static ArrayList<PackageInfo> getThirdPartyApplications(Context context) {
        if (thirdPartyApplications != null && !thirdPartyApplications.isEmpty()) {
            return thirdPartyApplications;
        }

        thirdPartyApplications = new ArrayList<>();
        List<PackageInfo> applications = context.getPackageManager().getInstalledPackages(0);

        for (PackageInfo packageInfo : applications) {
            if (!((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) != 0)) {
                thirdPartyApplications.add(packageInfo);
            }
        }

        return thirdPartyApplications;
    }
}
