package no.ntnu.idi.watchdogprod.recommender;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import no.ntnu.idi.watchdogprod.recommender.Permissions;
import org.apache.mahout.math.SequentialAccessSparseVector;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.idi.watchdogprod.InteractiveScrollView;

/**
 * Created by Wschive on 16/04/15.
 */
public class AppVector extends RandomAccessSparseVector{
    private RandomAccessSparseVector appVector;
    private DenseVector profileVector;



    public AppVector() {
        this.appVector = new RandomAccessSparseVector();
        this.profileVector = new DenseVector();
        profileVector.set(Permissions.ACCESS_COARSE_LOCATION.index(),1);
    }
    private void putOnPermission(String permission){
        switch (permission){
            case "ACCESS_COARSE_LOCATION":
                super.set(0,1);
                break;
        }
        this.permissions.put("ACCESS_COARSE_LOCATION",0);
        this.permissions.put("ACCESS_FINE_LOCATION",0);
        this.permissions.put("ACCESS_LOCATION_EXTRA_COMMANDS",0);
        this.permissions.put("ACCESS_MOCK_LOCATION",0);
        this.permissions.put("ACCESS_NETWORK_STATE",0);
        this.permissions.put("ACCESS_WIFI_STATE",0);
        this.permissions.put("ACCESS_WIMAX_STATE",0);
        this.permissions.put("ADD_VOICEMAIL",0);
        this.permissions.put("AUTHENTICATE_ACCOUNTS",0);
        this.permissions.put("BLUETOOTH",0);
        this.permissions.put("BLUETOOTH_ADMIN",0);
        this.permissions.put("BODY_SENSORS",0);
        this.permissions.put("CALL_PHONE",0);
        this.permissions.put("CAMERA",0);
        this.permissions.put("CHANGE_CONFIGURATION",0);
        this.permissions.put("CHANGE_NETWORK_STATE",0);
        this.permissions.put("CHANGE_WIFI_MULTICAST_STATE",0);
        this.permissions.put("CHANGE_WIFI_STATE",0);
        this.permissions.put("CHANGE_WIMAX_STATE",0);
        this.permissions.put("CLEAR_APP_CACHE",0);
        this.permissions.put("DISABLE_KEYGUARD",0);
        this.permissions.put("EXPAND_STATUS_BAR",0);
        this.permissions.put("FLASHLIGHT",0);
        this.permissions.put("GET_ACCOUNTS",0);
        this.permissions.put("GET_PACKAGE_SIZE",0);
        this.permissions.put("GET_TASKS",0);
        this.permissions.put("INSTALL_SHORTCUT",0);
        this.permissions.put("INTERNET",0);
        this.permissions.put("KILL_BACKGROUND_PROCESSES",0);
        this.permissions.put("MANAGE_ACCOUNTS",0);
        this.permissions.put("MODIFY_AUDIO_SETTINGS",0);
        this.permissions.put("NFC",0);
        this.permissions.put("PROCESS_OUTGOING_CALLS",0);
        this.permissions.put("READ_CALENDAR",0);
        this.permissions.put("READ_CALL_LOG",0);
        this.permissions.put("READ_CONTACTS",0);
        this.permissions.put("READ_EXTERNAL_STORAGE",0);
        this.permissions.put("READ_HISTORY_BOOKMARKS",0);
        this.permissions.put("READ_PHONE_STATE",0);
        this.permissions.put("READ_PROFILE",0);
        this.permissions.put("READ_SMS",0);
        this.permissions.put("READ_SOCIAL_STREAM",0);
        this.permissions.put("READ_SYNC_SETTINGS",0);
        this.permissions.put("READ_SYNC_STATS",0);
        this.permissions.put("READ_USER_DICTIONARY",0);
        this.permissions.put("RECEIVE_BOOT_COMPLETED",0);
        this.permissions.put("RECEIVE_MMS",0);
        this.permissions.put("RECEIVE_SMS",0);
        this.permissions.put("RECEIVE_WAP_PUSH",0);
        this.permissions.put("RECORD_AUDIO",0);
        this.permissions.put("REORDER_TASKS",0);
        this.permissions.put("SEND_SMS",0);
        this.permissions.put("SET_ALARM",0);
        this.permissions.put("SET_TIME_ZONE",0);
        this.permissions.put("SET_WALLPAPER",0);
        this.permissions.put("SUBSCRIBED_FEEDS_READ",0);
        this.permissions.put("SUBSCRIBED_FEEDS_WRITE",0);
        this.permissions.put("SYSTEM_ALERT_WINDOW",0);
        this.permissions.put("TRANSMIT_IR",0);
        this.permissions.put("UNINSTALL_SHORTCUT",0);
        this.permissions.put("USE_CREDENTIALS",0);
        this.permissions.put("USE_SIP",0);
        this.permissions.put("VIBRATE",0);
        this.permissions.put("WAKE_LOCK",0);
        this.permissions.put("WRITE_CALENDAR",0);
        this.permissions.put("WRITE_CALL_LOG",0);
        this.permissions.put("WRITE_CONTACTS",0);
        this.permissions.put("WRITE_EXTERNAL_STORAGE",0);
        this.permissions.put("WRITE_HISTORY_BOOKMARKS",0);
        this.permissions.put("WRITE_PROFILE",0);
        this.permissions.put("WRITE_SMS",0);
        this.permissions.put("WRITE_SOCIAL_STREAM",0);
        this.permissions.put("WRITE_SYNC_SETTINGS",0);
        this.permissions.put("WRITE_USER_DICTIONARY",0);
        this.permissions.put("WRITE_VOICEMAIL",0);

    }
}
