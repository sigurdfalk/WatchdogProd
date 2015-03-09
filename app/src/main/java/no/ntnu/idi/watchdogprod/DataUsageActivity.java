package no.ntnu.idi.watchdogprod;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(appName);


        //GRAPH-PLOT

        DataUsageSource dataDBSource = new DataUsageSource(this);
        dataDBSource.open();
        ArrayList<DataLog> dataLogs = dataDBSource.getDataLogsForApp(packageName);

        for(DataLog dataLog : dataLogs) {
            downBackground.add(dataLog.getAmountDownBackground());
            downForeground.add(dataLog.getAmountDownForeground());
            upBackground.add(dataLog.getAmountUpBackground());
            upForeground.add(dataLog.getAmountUpForeground());
        }
        dataDBSource.close();

        Number[] days =   { 1  , 2   , 3   , 4   , 5   , 6   , 7 };
        Number[] values = { 3, 1, 4, 6, 2, 5, 1 };

        plotDown = (XYPlot) findViewById(R.id.mySimpleXYPlotDOWN);
        plotDown.getBackgroundPaint().setColor(Color.WHITE);
        plotDown.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plotDown.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plotDown.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(days),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Bakgrunn");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(values),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Forgrunn");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.CYAN, new PointLabelFormatter());
        plotDown.addSeries(series1, series1Format);

        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.rgb(0, 300, 0),Color.rgb(0, 100, 0),Color.RED, new PointLabelFormatter());
        plotDown.addSeries(series2, series2Format);

        plotDown.setTicksPerRangeLabel(3);
        plotDown.getGraphWidget().setDomainLabelOrientation(0);


        plotUp = (XYPlot) findViewById(R.id.mySimpleXYPlotUP);
        plotUp.getBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plotUp.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        XYSeries series1Up = new SimpleXYSeries(
                Arrays.asList(days),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Bakgrunn");
        XYSeries series2Up = new SimpleXYSeries(
                Arrays.asList(values),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Forgrunn");

        LineAndPointFormatter series1UpFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.CYAN, new PointLabelFormatter());
        plotUp.addSeries(series1Up, series1UpFormat);

        LineAndPointFormatter series2UpFormat = new LineAndPointFormatter(Color.rgb(0, 300, 0),Color.rgb(0, 100, 0),Color.RED, new PointLabelFormatter());
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
