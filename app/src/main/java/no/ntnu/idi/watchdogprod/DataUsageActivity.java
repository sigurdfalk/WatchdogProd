package no.ntnu.idi.watchdogprod;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

import no.ntnu.idi.watchdogprod.sqlite.datausage.DataLog;
import no.ntnu.idi.watchdogprod.sqlite.datausage.DataUsageSource;


public class DataUsageActivity extends ActionBarActivity {
    String packageName;
    String appName;
    private XYPlot plotDown;
    private XYPlot plotUp;

    ArrayList<Long> downBackground;
    ArrayList<Long> downForeground;
    ArrayList<Long> upBackground;
    ArrayList<Long> upForeground;

    RadioGroup radioGroupDownload;
    RadioGroup radioGroupUpload;

    RadioButton radioButtonDownloadDay;
    RadioButton radioButtonDownloadWeek;
    RadioButton radioButtonUploadDay;
    RadioButton radioButtonUploadWeek;

    TextView textViewDataDown;
    TextView textViewDataUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(appName);

        radioGroupDownload = (RadioGroup) findViewById(R.id.radiogroup_download);
        radioGroupUpload = (RadioGroup) findViewById(R.id.radiogroup_upload);
        radioButtonDownloadDay = (RadioButton) findViewById(R.id.radiobutton_download_day);
        radioButtonDownloadWeek = (RadioButton) findViewById(R.id.radiobutton_download_week);
        radioButtonUploadDay = (RadioButton) findViewById(R.id.radiobutton_upload_day);
        radioButtonUploadWeek = (RadioButton) findViewById(R.id.radiobutton_upload_week);

        radioButtonDownloadDay.setChecked(true);
        radioButtonUploadDay.setChecked(true);

        textViewDataDown = (TextView)findViewById(R.id.textfield_data_down);
        textViewDataDown = (TextView)findViewById(R.id.textfield_data_down);

        //GRAPH-PLOT

        DataUsageSource dataDBSource = new DataUsageSource(this);
        dataDBSource.open();
        ArrayList<DataLog> dataLogs = dataDBSource.getDataLogsForApp(packageName);

        for (DataLog dataLog : dataLogs) {
            downBackground.add(dataLog.getAmountDownBackground());
            downForeground.add(dataLog.getAmountDownForeground());
            upBackground.add(dataLog.getAmountUpBackground());
            upForeground.add(dataLog.getAmountUpForeground());
        }

//        DataLog total = dataDBSource.getDataTotals(packageName);
//        textViewDataDown.setText("Nedlastet i bakgrunnen: ~" + total.getAmountDownBackground() + "\nNedlastet i forgrunnen: ~" + total.getAmountDownForeground());
//        textViewDataUp.setText("Opplastet i bakgrunnen: ~" + total.getAmountUpBackground() + "\nOpplastet i forgrunnen: ~" + total.getAmountUpForeground());

        dataDBSource.close();

        Number[] hoursValues = {1,2,3,4,5,6,7,8,9,10,11,12};
        Number[] daysValues = {1,2,3,4,5,6,7};

        plotDown = (XYPlot) findViewById(R.id.mySimpleXYPlotDOWN);
        plotDown.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        plotDown.setPlotMargins(0, 0, 0, 0);
        plotDown.setPlotPadding(0, 0, 0, 0);
        plotDown.setGridPadding(0, 10, 5, 0);

        plotDown.getBackgroundPaint().setColor(Color.WHITE);

        plotDown.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);

        plotDown.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        plotDown.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        plotDown.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plotDown.setDomainValueFormat(new DecimalFormat("0"));
        plotDown.setDomainStepValue(1);

        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(new Number []{2,3,8,1,2}),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
           "Bakgrunn");

        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), Color.CYAN, new PointLabelFormatter());
        series1Format.setFillPaint(lineFill);
        plotDown.addSeries(series1, series1Format);


        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(new Number []{2,3,8,1,2}),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Forgrunn");

        Paint lineFill2 = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.RED, Shader.TileMode.MIRROR));

        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.rgb(0, 300, 0), Color.rgb(0, 100, 0), Color.RED, new PointLabelFormatter());
        series2Format.setFillPaint(lineFill2);
//        plotDown.addSeries(series2, series2Format);

//        plotDown.setTicksPerRangeLabel(3);
//        plotDown.getGraphWidget().setDomainLabelOrientation(0);

        plotDown.setDomainBoundaries(1,13, BoundaryMode.FIXED);

        //WEEK
        plotDown.setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 1:
                        toAppendTo.append("Man");
                        break;
                    case 2:
                        toAppendTo.append("Tir");
                        break;
                    case 3:
                        toAppendTo.append("Ons");
                        break;
                    case 4:
                        toAppendTo.append("Tor");
                        break;
                    case 5:
                        toAppendTo.append("Fre");
                        break;
                    case 6:
                        toAppendTo.append("Lør");
                        break;
                    case 7:
                        toAppendTo.append("Søn");
                        break;
                    default:
                        toAppendTo.append("FEIL");
                        break;
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        //DAY
        plotDown.setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 1:
                        toAppendTo.append("00");
                        break;
                    case 2:
                        toAppendTo.append("02");
                        break;
                    case 3:
                        toAppendTo.append("04");
                        break;
                    case 4:
                        toAppendTo.append("06");
                        break;
                    case 5:
                        toAppendTo.append("08");
                        break;
                    case 6:
                        toAppendTo.append("10");
                        break;
                    case 7:
                        toAppendTo.append("12");
                        break;
                    case 8:
                        toAppendTo.append("14");
                        break;
                    case 9:
                        toAppendTo.append("16");
                        break;
                    case 10:
                        toAppendTo.append("18");
                        break;
                    case 11:
                        toAppendTo.append("20");
                        break;
                    case 12:
                        toAppendTo.append("22");
                        break;
                    case 13:
                        toAppendTo.append("24");
                        break;
                    default:
                        toAppendTo.append("FEIL");
                        break;
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plotUp = (XYPlot) findViewById(R.id.mySimpleXYPlotUP);
        plotUp.getBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        XYSeries series1Up = new SimpleXYSeries(
                Arrays.asList(daysValues),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Bakgrunn");
        XYSeries series2Up = new SimpleXYSeries(
                Arrays.asList(daysValues),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Forgrunn");

        LineAndPointFormatter series1UpFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), Color.CYAN, new PointLabelFormatter());
        plotUp.addSeries(series1Up, series1UpFormat);

        LineAndPointFormatter series2UpFormat = new LineAndPointFormatter(Color.rgb(0, 300, 0), Color.rgb(0, 100, 0), Color.RED, new PointLabelFormatter());
        plotUp.addSeries(series2Up, series2UpFormat);

        plotUp.setTicksPerRangeLabel(3);
        plotUp.getGraphWidget().setDomainLabelOrientation(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_usage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
