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
        if (isThirdPartyApplicationsPopulated()) {
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

    public static PackageInfo getPackageInfo(String packageName, Context context) {
        if (!isThirdPartyApplicationsPopulated()) {
            thirdPartyApplications = getThirdPartyApplications(context);
        }

        for (PackageInfo packageInfo : thirdPartyApplications) {
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo;
            }
        }

        throw new IllegalArgumentException("Package not found.");
    }

    public static String getApplicationName(PackageInfo packageInfo, Context context) {
        return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
    }

    private static boolean isThirdPartyApplicationsPopulated() {
        return thirdPartyApplications != null && !thirdPartyApplications.isEmpty();
    }
}
