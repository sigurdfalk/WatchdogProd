package no.ntnu.idi.watchdogprod;

        import android.content.Context;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;

        import java.util.ArrayList;
        import java.util.List;

        import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;

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

    public static PackageInfo getPackageInfo(String packageName, Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_RECEIVERS | PackageManager.GET_PERMISSIONS);
    }

    public static String getApplicationName(PackageInfo packageInfo, Context context) {
        return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
    }

    public static AppInfo getAppInfo(String packageName, Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageInfo(packageName, context);

        return new AppInfo(packageInfo.packageName, packageInfo.requestedPermissions != null ? packageInfo.requestedPermissions : new String[]{}, packageInfo.versionCode, packageInfo.lastUpdateTime);
    }

    private static boolean isThirdPartyApplicationsPopulated() {
        return thirdPartyApplications != null && !thirdPartyApplications.isEmpty();
    }
}
