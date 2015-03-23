package no.ntnu.idi.watchdogprod;

        import android.content.Context;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

        import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationHelper {
    private static ArrayList<ExtendedPackageInfo> thirdPartyApplications;

    public static ArrayList<ExtendedPackageInfo> getThirdPartyApplications(Context context) {
        if (isThirdPartyApplicationsPopulated()) {
            return thirdPartyApplications;
        }

        thirdPartyApplications = new ArrayList<>();
        List<PackageInfo> applications = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : applications) {
            if (!((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) != 0)) {
                ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(packageInfo.requestedPermissions, context);
                ArrayList<Rule> violatedRules = RuleHelper.getViolatedRules(packageInfo.requestedPermissions, context);
                thirdPartyApplications.add(new ExtendedPackageInfo(packageInfo, permissionDescriptions, violatedRules));
            }
        }

        Collections.sort(thirdPartyApplications);
        return thirdPartyApplications;
    }

    public static ExtendedPackageInfo getThirdPartyApplication(Context context, String packageName) {
        List<PackageInfo> applications = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
        ExtendedPackageInfo app = null;

        System.out.println("INN " + packageName);

        for (PackageInfo packageInfo : applications) {
            if (!((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) != 0)) {
                System.out.println("LETER " + packageInfo.packageName);
                if(packageInfo.packageName.equals(packageName)) {
                    ArrayList<PermissionDescription> permissionDescriptions = PermissionHelper.getApplicationPermissionDescriptions(packageInfo.requestedPermissions, context);
                    ArrayList<Rule> violatedRules = RuleHelper.getViolatedRules(packageInfo.requestedPermissions, context);
                    app = new ExtendedPackageInfo(packageInfo,permissionDescriptions,violatedRules);
                    System.out.println("FANT  OG SATTE APPEN");
                    break;
                }
            }
        }
        return app;
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
