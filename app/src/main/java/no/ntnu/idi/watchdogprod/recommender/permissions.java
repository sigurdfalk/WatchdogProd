package no.ntnu.idi.watchdogprod.recommender;

/**
 * Created by Wschive on 20/04/15.
 */
public enum Permissions {
    ACCESS_COARSE_LOCATION(0,"ACCESS_COARSE_LOCATION");
    ACCESS_FINE_LOCATION(1,"ACCESS_FINE_LOCATION");
    ACCESS_LOCATION_EXTRA_COMMANDS(2,"ACCESS_LOCATION_EXTRA_COMMANDS");
    ACCESS_MOCK_LOCATION(3,"ACCESS_MOCK_LOCATION");
    ACCESS_NETWORK_STATE(4,"ACCESS_NETWORK_STATE");
    ACCESS_WIFI_STATE(5,"ACCESS_WIFI_STATE");
    ACCESS_WIMAX_STATE(6,"ACCESS_WIMAX_STATE");
    ADD_VOICEMAIL(7,"ADD_VOICEMAIL");
    AUTHENTICATE_ACCOUNTS(8,"AUTHENTICATE_ACCOUNTS");
    BLUETOOTH(9,"BLUETOOTH");
    BLUETOOTH_ADMIN(10,"BLUETOOTH_ADMIN");
    BODY_SENSORS(11,"BODY_SENSORS");
    CALL_PHONE(12,"CALL_PHONE");
    CAMERA(13,"CAMERA");
    CHANGE_CONFIGURATION(14,"CHANGE_CONFIGURATION");
    CHANGE_NETWORK_STATE(15,"CHANGE_NETWORK_STATE");
    CHANGE_WIFI_MULTICAST_STATE(16,"CHANGE_WIFI_MULTICAST_STATE");
    CHANGE_WIFI_STATE(17,"CHANGE_WIFI_STATE");
    CHANGE_WIMAX_STATE(18,"CHANGE_WIMAX_STATE");
    CLEAR_APP_CACHE(19,"CLEAR_APP_CACHE");
    DISABLE_KEYGUARD(20,"DISABLE_KEYGUARD");
    EXPAND_STATUS_BAR(21,"EXPAND_STATUS_BAR");
    FLASHLIGHT(22,"FLASHLIGHT");
    GET_ACCOUNTS(23,"GET_ACCOUNTS");
    GET_PACKAGE_SIZE(24,"GET_PACKAGE_SIZE");
    GET_TASKS(25,"GET_TASKS");
    INSTALL_SHORTCUT(26,"INSTALL_SHORTCUT");
    INTERNET(27,"INTERNET");
    KILL_BACKGROUND_PROCESSES(28,"KILL_BACKGROUND_PROCESSES");
    MANAGE_ACCOUNTS(29,"MANAGE_ACCOUNTS");
    MODIFY_AUDIO_SETTINGS(30,"MODIFY_AUDIO_SETTINGS");
    NFC(31,"NFC");
    PROCESS_OUTGOING_CALLS(32,"PROCESS_OUTGOING_CALLS");
    READ_CALENDAR(33,"READ_CALENDAR");
    READ_CALL_LOG(34,"READ_CALL_LOG");
    READ_CONTACTS(35,"READ_CONTACTS");
    READ_EXTERNAL_STORAGE(36,"READ_EXTERNAL_STORAGE");
    READ_HISTORY_BOOKMARKS(37,"READ_HISTORY_BOOKMARKS");
    READ_PHONE_STATE(38,"READ_PHONE_STATE");
    READ_PROFILE(39,"READ_PROFILE");
    READ_SMS(40,"READ_SMS");
    READ_SOCIAL_STREAM(41,"READ_SOCIAL_STREAM");
    READ_SYNC_SETTINGS(42,"READ_SYNC_SETTINGS");
    READ_SYNC_STATS(43,"READ_SYNC_STATS");
    READ_USER_DICTIONARY(44,"READ_USER_DICTIONARY");
    RECEIVE_BOOT_COMPLETED(45,"RECEIVE_BOOT_COMPLETED");
    RECEIVE_MMS(46,"RECEIVE_MMS");
    RECEIVE_SMS(47,"RECEIVE_SMS");
    RECEIVE_WAP_PUSH(48,"RECEIVE_WAP_PUSH");
    RECORD_AUDIO(49,"RECORD_AUDIO");
    REORDER_TASKS(50,"REORDER_TASKS");
    SEND_SMS(51,"SEND_SMS");
    SET_ALARM(52,"SET_ALARM");
    SET_TIME_ZONE(53,"SET_TIME_ZONE");
    SET_WALLPAPER(54,"SET_WALLPAPER");
    SUBSCRIBED_FEEDS_READ(55,"SUBSCRIBED_FEEDS_READ");
    SUBSCRIBED_FEEDS_WRITE(56,"SUBSCRIBED_FEEDS_WRITE");
    SYSTEM_ALERT_WINDOW(57,"SYSTEM_ALERT_WINDOW");
    TRANSMIT_IR(58,"TRANSMIT_IR");
    UNINSTALL_SHORTCUT(59,"UNINSTALL_SHORTCUT");
    USE_CREDENTIALS(60,"USE_CREDENTIALS");
    USE_SIP(61,"USE_SIP");
    VIBRATE(62,"VIBRATE");
    WAKE_LOCK(63,"WAKE_LOCK");
    WRITE_CALENDAR(64,"WRITE_CALENDAR");
    WRITE_CALL_LOG(65,"WRITE_CALL_LOG");
    WRITE_CONTACTS(66,"WRITE_CONTACTS");
    WRITE_EXTERNAL_STORAGE(67,"WRITE_EXTERNAL_STORAGE");
    WRITE_HISTORY_BOOKMARKS(68,"WRITE_HISTORY_BOOKMARKS");
    WRITE_PROFILE(69,"WRITE_PROFILE");
    WRITE_SMS(70,"WRITE_SMS");
    WRITE_SOCIAL_STREAM(71,"WRITE_SOCIAL_STREAM");
    WRITE_SYNC_SETTINGS(72,"WRITE_SYNC_SETTINGS");
    WRITE_USER_DICTIONARY(73,"WRITE_USER_DICTIONARY");
    WRITE_VOICEMAIL(74,"WRITE_VOICEMAIL");


    private final int index;
    private final String name;
    Permissions(int index, String name) {
        this.index = index;
        this.name = name;
    }
    int index(){return index;}
    String name(){return name;}
}