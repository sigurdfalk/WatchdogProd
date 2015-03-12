package no.ntnu.idi.watchdogprod.sqlite.datausage;

/**
 * Created by fredsten on 09.03.2015.
 */
public class DataLog{
    private long id;
    private String timestamp;
    private String dataInfo;
    private Long amountDownForeground;
    private Long amountDownBackground;
    private Long amountUpForeground;
    private Long amountUpBackground;

    public DataLog(long id, String timestamp, String dataInfo, Long amountDownForeground, Long amountDownBackground, Long amountUpForeground, Long amountUpBackground) {
        this.id = id;
        this.timestamp = timestamp;
        this.dataInfo = dataInfo;
        this.amountDownForeground = amountDownForeground;
        this.amountDownBackground = amountDownBackground;
        this.amountUpForeground = amountUpForeground;
        this.amountUpBackground = amountUpBackground;
    }

    public long getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDataInfo() {
        return dataInfo;
    }

    public Long getAmountDownForeground() {
        return amountDownForeground;
    }

    public Long getAmountDownBackground() {
        return amountDownBackground;
    }

    public Long getAmountUpForeground() {
        return amountUpForeground;
    }

    public Long getAmountUpBackground() {
        return amountUpBackground;
    }
}
