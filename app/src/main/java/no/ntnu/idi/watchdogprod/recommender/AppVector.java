package no.ntnu.idi.watchdogprod.recommender;

import android.util.Log;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;

import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.recommender.Permissions;
import org.apache.mahout.math.SequentialAccessSparseVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Wschive on 16/04/15.
 */
public class AppVector extends RandomAccessSparseVector{
    //private RandomAccessSparseVector appVector;
    private static final String TAG = "recommender.AppVector";

    public AppVector(){
        super();
    }

    public AppVector(ArrayList<PermissionDescription> permissionDescriptions){
        super(74);
        for (PermissionDescription permissionDescription : permissionDescriptions) {
            putPermission(permissionDescription.getName());
        }
    }
    public AppVector(String[] permissions){
        super(74);
        for (String permission : permissions) {
            putPermission(permission);
        }
        int i = 0;
    }

    /**
     * Puts the risk value of a permission in a set place based on the
     * alphabetical list of permissions.'
     * @param permission - string with the name of the permission. I.e. "ACCESS_COARSE_LOCATION"
     */
    private void putPermission(String permission) {
        if(permission.contains("ACCESS_COARSE_LOCATION")){
            super.set(0,2);
        } else
        if(permission.contains("ACCESS_FINE_LOCATION")){
            super.set(1,2);
        } else
        if(permission.contains("ACCESS_LOCATION_EXTRA_COMMANDS")){
            super.set(2,2);
        } else
        if(permission.contains("ACCESS_MOCK_LOCATION")){
            super.set(3,2);
        } else
        if(permission.contains("ACCESS_NETWORK_STATE")){
            super.set(4,2);
        } else
        if(permission.contains("ACCESS_WIFI_STATE")){
            super.set(5,2);
        } else
        if(permission.contains("ACCESS_WIMAX_STATE")){
            super.set(6,1);
        } else
        if(permission.contains("ADD_VOICEMAIL")){
            super.set(7,2);
        } else
        if(permission.contains("AUTHENTICATE_ACCOUNTS")){
            super.set(8,4);
        } else
        if(permission.contains("BLUETOOTH")){
            super.set(9,2);
        } else
        if(permission.contains("BLUETOOTH_ADMIN")){
            super.set(10,2);
        } else
        if(permission.contains("BODY_SENSORS")){
            super.set(11,1);
        } else
        if(permission.contains("CALL_PHONE")){
            super.set(12,4);
        } else
        if(permission.contains("CAMERA")){
            super.set(13,1);
        } else
        if(permission.contains("CHANGE_CONFIGURATION")){
            super.set(14,2);
        } else
        if(permission.contains("CHANGE_NETWORK_STATE")){
            super.set(15,2);
        } else
        if(permission.contains("CHANGE_WIFI_MULTICAST_STATE")){
            super.set(16,1);
        } else
        if(permission.contains("CHANGE_WIFI_STATE")){
            super.set(17,2);
        } else
        if(permission.contains("CHANGE_WIMAX_STATE")){
            super.set(18,1);
        } else
        if(permission.contains("CLEAR_APP_CACHE")){
            super.set(19,4);
        } else
        if(permission.contains("DISABLE_KEYGUARD")){
            super.set(20,2);
        } else
        if(permission.contains("EXPAND_STATUS_BAR")){
            super.set(21,1);
        } else
        if(permission.contains("FLASHLIGHT")){
            super.set(22,1);
        } else
        if(permission.contains("GET_ACCOUNTS")){
            super.set(23,2);
        } else
        if(permission.contains("GET_PACKAGE_SIZE")){
            super.set(24,1);
        } else
        if(permission.contains("GET_TASKS")){
            super.set(25,2);
        } else
        if(permission.contains("INSTALL_SHORTCUT")){
            super.set(26,1);
        } else
        if(permission.contains("INTERNET")){
            super.set(27,1);
        } else
        if(permission.contains("KILL_BACKGROUND_PROCESSES")){
            super.set(28,4);
        } else
        if(permission.contains("MANAGE_ACCOUNTS")){
            super.set(29,2);
        } else
        if(permission.contains("MODIFY_AUDIO_SETTINGS")){
            super.set(30,2);
        } else
        if(permission.contains("NFC")){
            super.set(31,2);
        } else
        if(permission.contains("PROCESS_OUTGOING_CALLS")){
            super.set(32,4);
        } else
        if(permission.contains("READ_CALENDAR")){
            super.set(33,2);
        } else
        if(permission.contains("READ_CALL_LOG")){
            super.set(34,2);
        } else
        if(permission.contains("READ_CONTACTS")){
            super.set(35,2);
        } else
        if(permission.contains("READ_EXTERNAL_STORAGE")){
            super.set(36,1);
        } else
        if(permission.contains("READ_HISTORY_BOOKMARKS")){
            super.set(37,2);
        } else
        if(permission.contains("READ_PHONE_STATE")){
            super.set(38,1);
        } else
        if(permission.contains("READ_PROFILE")){
            super.set(39,1);
        } else
        if(permission.contains("READ_SMS")){
            super.set(40,4);
        } else
        if(permission.contains("READ_SOCIAL_STREAM")){
            super.set(41,4);
        } else
        if(permission.contains("READ_SYNC_SETTINGS")){
            super.set(42,1);
        } else
        if(permission.contains("READ_SYNC_STATS")){
            super.set(43,2);
        } else
        if(permission.contains("READ_USER_DICTIONARY")){
            super.set(44,1);
        } else
        if(permission.contains("RECEIVE_BOOT_COMPLETED")){
            super.set(45,1);
        } else
        if(permission.contains("RECEIVE_MMS")){
            super.set(46,4);
        } else
        if(permission.contains("RECEIVE_SMS")){
            super.set(47,4);
        } else
        if(permission.contains("RECEIVE_WAP_PUSH")){
            super.set(48,1);
        } else
        if(permission.contains("RECORD_AUDIO")){
            super.set(49,4);
        } else
        if(permission.contains("REORDER_TASKS")){
            super.set(50,2);
        } else
        if(permission.contains("SEND_SMS")){
            super.set(51,2);
        } else
        if(permission.contains("SET_ALARM")){
            super.set(52,1);
        } else
        if(permission.contains("SET_TIME_ZONE")){
            super.set(53,1);
        } else
        if(permission.contains("SET_WALLPAPER")){
            super.set(54,1);
        } else
        if(permission.contains("SUBSCRIBED_FEEDS_READ")){
            super.set(55,2);
        } else
        if(permission.contains("SUBSCRIBED_FEEDS_WRITE")){
            super.set(56,2);
        } else
        if(permission.contains("SYSTEM_ALERT_WINDOW")){
            super.set(57,4);
        } else
        if(permission.contains("TRANSMIT_IR")){
            super.set(58,1);
        } else
        if(permission.contains("UNINSTALL_SHORTCUT")){
            super.set(59,1);
        } else
        if(permission.contains("USE_CREDENTIALS")){
            super.set(60,2);
        } else
        if(permission.contains("USE_SIP")){
            super.set(61,2);
        } else
        if(permission.contains("VIBRATE")){
            super.set(62,1);
        } else
        if(permission.contains("WAKE_LOCK")){
            super.set(63,1);
        } else
        if(permission.contains("WRITE_CALENDAR")){
            super.set(64,2);
        } else
        if(permission.contains("WRITE_CALL_LOG")){
            super.set(65,2);
        } else
        if(permission.contains("WRITE_CONTACTS")){
            super.set(66,2);
        } else
        if(permission.contains("WRITE_EXTERNAL_STORAGE")){
            super.set(67,2);
        } else
        if(permission.contains("WRITE_HISTORY_BOOKMARKS")){
            super.set(68,2);
        } else
        if(permission.contains("WRITE_PROFILE")){
            super.set(69,2);
        } else
        if(permission.contains("WRITE_SMS")){
            super.set(70,4);
        } else
        if(permission.contains("WRITE_SOCIAL_STREAM")){
            super.set(71,4);
        } else
        if(permission.contains("WRITE_SYNC_SETTINGS")){
            super.set(72,2);
        } else
        if(permission.contains("WRITE_USER_DICTIONARY")){
            super.set(73,1);
        } else
        if(permission.contains("WRITE_VOICEMAIL")){
            super.set(74,1);
        }
        /*
        switch (permission) {
            case "ACCESS_COARSE_LOCATION":
                super.set(0, 2);
                break;
            case "ACCESS_FINE_LOCATION":
                super.set(1, 2);
                break;
            case "ACCESS_LOCATION_EXTRA_COMMANDS":
                super.set(2, 2);
                break;
            case "ACCESS_MOCK_LOCATION":
                super.set(3, 2);
                break;
            case "ACCESS_NETWORK_STATE":
                super.set(4, 2);
                break;
            case "ACCESS_WIFI_STATE":
                super.set(5, 2);
                break;
            case "ACCESS_WIMAX_STATE":
                super.set(6, 1);
                break;
            case "ADD_VOICEMAIL":
                super.set(7, 2);
                break;
            case "AUTHENTICATE_ACCOUNTS":
                super.set(8, 4);
                break;
            case "BLUETOOTH":
                super.set(9, 2);
                break;
            case "BLUETOOTH_ADMIN":
                super.set(10, 2);
                break;
            case "BODY_SENSORS":
                super.set(11, 1);
                break;
            case "CALL_PHONE":
                super.set(12, 4);
                break;
            case "CAMERA":
                super.set(13, 1);
                break;
            case "CHANGE_CONFIGURATION":
                super.set(14, 2);
                break;
            case "CHANGE_NETWORK_STATE":
                super.set(15, 2);
                break;
            case "CHANGE_WIFI_MULTICAST_STATE":
                super.set(16, 1);
                break;
            case "CHANGE_WIFI_STATE":
                super.set(17, 2);
                break;
            case "CHANGE_WIMAX_STATE":
                super.set(18, 1);
                break;
            case "CLEAR_APP_CACHE":
                super.set(19, 4);
                break;
            case "DISABLE_KEYGUARD":
                super.set(20, 2);
                break;
            case "EXPAND_STATUS_BAR":
                super.set(21, 1);
                break;
            case "FLASHLIGHT":
                super.set(22, 1);
                break;
            case "GET_ACCOUNTS":
                super.set(23, 2);
                break;
            case "GET_PACKAGE_SIZE":
                super.set(24, 1);
                break;
            case "GET_TASKS":
                super.set(25, 2);
                break;
            case "INSTALL_SHORTCUT":
                super.set(26, 1);
                break;
            case "INTERNET":
                super.set(27, 1);
                break;
            case "KILL_BACKGROUND_PROCESSES":
                super.set(28, 4);
                break;
            case "MANAGE_ACCOUNTS":
                super.set(29, 2);
                break;
            case "MODIFY_AUDIO_SETTINGS":
                super.set(30, 2);
                break;
            case "NFC":
                super.set(31, 2);
                break;
            case "PROCESS_OUTGOING_CALLS":
                super.set(32, 4);
                break;
            case "READ_CALENDAR":
                super.set(33, 2);
                break;
            case "READ_CALL_LOG":
                super.set(34, 2);
                break;
            case "READ_CONTACTS":
                super.set(35, 2);
                break;
            case "READ_EXTERNAL_STORAGE":
                super.set(36, 1);
                break;
            case "READ_HISTORY_BOOKMARKS":
                super.set(37, 2);
                break;
            case "READ_PHONE_STATE":
                super.set(38, 1);
                break;
            case "READ_PROFILE":
                super.set(39, 1);
                break;
            case "READ_SMS":
                super.set(40, 4);
                break;
            case "READ_SOCIAL_STREAM":
                super.set(41, 4);
                break;
            case "READ_SYNC_SETTINGS":
                super.set(42, 1);
                break;
            case "READ_SYNC_STATS":
                super.set(43, 2);
                break;
            case "READ_USER_DICTIONARY":
                super.set(44, 1);
                break;
            case "RECEIVE_BOOT_COMPLETED":
                super.set(45, 1);
                break;
            case "RECEIVE_MMS":
                super.set(46, 4);
                break;
            case "RECEIVE_SMS":
                super.set(47, 4);
                break;
            case "RECEIVE_WAP_PUSH":
                super.set(48, 1);
                break;
            case "RECORD_AUDIO":
                super.set(49, 4);
                break;
            case "REORDER_TASKS":
                super.set(50, 2);
                break;
            case "SEND_SMS":
                super.set(51, 2);
                break;
            case "SET_ALARM":
                super.set(52, 1);
                break;
            case "SET_TIME_ZONE":
                super.set(53, 1);
                break;
            case "SET_WALLPAPER":
                super.set(54, 1);
                break;
            case "SUBSCRIBED_FEEDS_READ":
                super.set(55, 2);
                break;
            case "SUBSCRIBED_FEEDS_WRITE":
                super.set(56, 2);
                break;
            case "SYSTEM_ALERT_WINDOW":
                super.set(57, 4);
                break;
            case "TRANSMIT_IR":
                super.set(58, 1);
                break;
            case "UNINSTALL_SHORTCUT":
                super.set(59, 1);
                break;
            case "USE_CREDENTIALS":
                super.set(60, 2);
                break;
            case "USE_SIP":
                super.set(61, 2);
                break;
            case "VIBRATE":
                super.set(62, 1);
                break;
            case "WAKE_LOCK":
                super.set(63, 1);
                break;
            case "WRITE_CALENDAR":
                super.set(64, 2);
                break;
            case "WRITE_CALL_LOG":
                super.set(65, 2);
                break;
            case "WRITE_CONTACTS":
                super.set(66, 2);
                break;
            case "WRITE_EXTERNAL_STORAGE":
                super.set(67, 2);
                break;
            case "WRITE_HISTORY_BOOKMARKS":
                super.set(68, 2);
                break;
            case "WRITE_PROFILE":
                super.set(69, 2);
                break;
            case "WRITE_SMS":
                super.set(70, 4);
                break;
            case "WRITE_SOCIAL_STREAM":
                super.set(71, 4);
                break;
            case "WRITE_SYNC_SETTINGS":
                super.set(72, 2);
                break;
            case "WRITE_USER_DICTIONARY":
                super.set(73, 1);
                break;
            case "WRITE_VOICEMAIL":
                super.set(74, 1);
                break;
            default:
                Log.e(TAG, "permission does not exist");
                break;

        }
        */


    }

}

