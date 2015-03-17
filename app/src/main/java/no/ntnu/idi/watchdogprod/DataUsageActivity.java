package no.ntnu.idi.watchdogprod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

import no.ntnu.idi.watchdogprod.sqlite.datausage.DataLog;
import no.ntnu.idi.watchdogprod.sqlite.datausage.DataUsageSource;


public class DataUsageActivity extends ActionBarActivity {
    Context context;
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

    public final String [] dataMeasures =  {"B","KB","MB","GB"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);

        context = getApplicationContext();

        packageName = getIntent().getExtras().getString("packageName");
        appName = getIntent().getExtras().getString("appName");

        downBackground = new ArrayList<>();
        downForeground = new ArrayList<>();
        upBackground = new ArrayList<>();
        upForeground = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(appName);

//        radioGroupDownload = (RadioGroup) findViewById(R.id.radiogroup_download);
//        radioGroupUpload = (RadioGroup) findViewById(R.id.radiogroup_upload);
//        radioButtonDownloadDay = (RadioButton) findViewById(R.id.radiobutton_download_day);
//        radioButtonDownloadWeek = (RadioButton) findViewById(R.id.radiobutton_download_week);
//        radioButtonUploadDay = (RadioButton) findViewById(R.id.radiobutton_upload_day);
//        radioButtonUploadWeek = (RadioButton) findViewById(R.id.radiobutton_upload_week);
//
//        radioButtonDownloadDay.setChecked(true);
//        radioButtonUploadDay.setChecked(true);

        textViewDataDown = (TextView) findViewById(R.id.textfield_data_down);
        textViewDataUp = (TextView) findViewById(R.id.textfield_data_up);

        //GRAPH-PLOT

        DataUsageSource dataDBSource = new DataUsageSource(this);
        dataDBSource.open();
        ArrayList<DataLog> dataLogs = null;
        dataLogs = dataDBSource.getDataLogsForApp(packageName);

        for (DataLog dataLog : dataLogs) {
            downBackground.add(dataLog.getAmountDownBackground());
            downForeground.add(dataLog.getAmountDownForeground());
            upBackground.add(dataLog.getAmountUpBackground());
            upForeground.add(dataLog.getAmountUpForeground());
        }

        DataLog total = dataDBSource.getDataTotals(packageName);
        textViewDataDown.setText("Nedlastet i bakgrunnen: ~" + DataUtils.humanReadableByteCount(total.getAmountDownBackground(), true) + "\nNedlastet i forgrunnen: ~" + DataUtils.humanReadableByteCount(total.getAmountDownForeground(), true));
        textViewDataUp.setText("Opplastet i bakgrunnen: ~" + DataUtils.humanReadableByteCount(total.getAmountUpBackground(), true) + "\nOpplastet i forgrunnen: ~" + DataUtils.humanReadableByteCount(total.getAmountUpForeground(), true));
        dataDBSource.close();

        Number[] hoursValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        Number[] daysValues = {1, 2, 3, 4, 5, 6, 7};
        Number[] randomLoL = {1, 5, 2, 6, 1, 3, 5};

//        plotDown = (XYPlot) findViewById(R.id.mySimpleXYPlotDOWN);
//        plotDown.setBorderStyle(Plot.BorderStyle.NONE, null, null);
//        plotDown.setPlotMargins(0, 0, 0, 0);
//        plotDown.setPlotPadding(0, 0, 0, 0);
//        plotDown.setGridPadding(0, 10, 5, 0);
//
//        plotDown.getBackgroundPaint().setColor(Color.WHITE);
//
//        plotDown.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
//
//        plotDown.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
//
//        plotDown.getGraphWidget().setSize(new SizeMetrics(
//                0, SizeLayoutType.FILL,
//                0, SizeLayoutType.FILL));
//
//        plotDown.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
//        plotDown.setDomainValueFormat(new DecimalFormat("0"));
//        plotDown.setDomainStepValue(1);
//
//        XYSeries series1 = new SimpleXYSeries(
////                Arrays.asList(new Number []{2,3,8,1,2}),
//                downBackground,
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
//           "Bakgrunn");
//
//        Paint lineFill = new Paint();
//        lineFill.setAlpha(200);
//        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
//
//        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), Color.CYAN, new PointLabelFormatter());
//        series1Format.setFillPaint(lineFill);
//        plotDown.addSeries(series1, series1Format);
//
//
//        XYSeries series2 = new SimpleXYSeries(
//                Arrays.asList(new Number []{2,3,8,1,2}),
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
//                "Forgrunn");
//
//        Paint lineFill2 = new Paint();
//        lineFill.setAlpha(200);
//        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.RED, Shader.TileMode.MIRROR));
//
//        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.rgb(0, 300, 0), Color.rgb(0, 100, 0), Color.RED, new PointLabelFormatter());
//        series2Format.setFillPaint(lineFill2);
////        plotDown.addSeries(series2, series2Format);
//
////        plotDown.setTicksPerRangeLabel(3);
////        plotDown.getGraphWidget().setDomainLabelOrientation(0);
//
//        plotDown.setDomainBoundaries(1,13, BoundaryMode.FIXED);
//
//        //WEEK
//        plotDown.setDomainValueFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                Number num = (Number) obj;
//                switch (num.intValue()) {
//                    case 1:
//                        toAppendTo.append("Man");
//                        break;
//                    case 2:
//                        toAppendTo.append("Tir");
//                        break;
//                    case 3:
//                        toAppendTo.append("Ons");
//                        break;
//                    case 4:
//                        toAppendTo.append("Tor");
//                        break;
//                    case 5:
//                        toAppendTo.append("Fre");
//                        break;
//                    case 6:
//                        toAppendTo.append("Lør");
//                        break;
//                    case 7:
//                        toAppendTo.append("Søn");
//                        break;
//                    default:
//                        toAppendTo.append("FEIL");
//                        break;
//                }
//                return toAppendTo;
//            }
//
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });
//
//        //DAY
//        plotDown.setDomainValueFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                Number num = (Number) obj;
//                switch (num.intValue()) {
//                    case 1:
//                        toAppendTo.append("00");
//                        break;
//                    case 2:
//                        toAppendTo.append("02");
//                        break;
//                    case 3:
//                        toAppendTo.append("04");
//                        break;
//                    case 4:
//                        toAppendTo.append("06");
//                        break;
//                    case 5:
//                        toAppendTo.append("08");
//                        break;
//                    case 6:
//                        toAppendTo.append("10");
//                        break;
//                    case 7:
//                        toAppendTo.append("12");
//                        break;
//                    case 8:
//                        toAppendTo.append("14");
//                        break;
//                    case 9:
//                        toAppendTo.append("16");
//                        break;
//                    case 10:
//                        toAppendTo.append("18");
//                        break;
//                    case 11:
//                        toAppendTo.append("20");
//                        break;
//                    case 12:
//                        toAppendTo.append("22");
//                        break;
//                    case 13:
//                        toAppendTo.append("24");
//                        break;
//                    default:
//                        toAppendTo.append("FEIL");
//                        break;
//                }
//                return toAppendTo;
//            }
//
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });
//
//        plotUp = (XYPlot) findViewById(R.id.mySimpleXYPlotUP);
//        plotUp.getBackgroundPaint().setColor(Color.WHITE);
//        plotUp.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
//        plotUp.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
//        plotUp.getGraphWidget().setSize(new SizeMetrics(
//                0, SizeLayoutType.FILL,
//                0, SizeLayoutType.FILL));
//
//        XYSeries series1Up = new SimpleXYSeries(
//                Arrays.asList(randomLoL),
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
//                "Bakgrunn");
////        XYSeries series2Up = new SimpleXYSeries(
////                Arrays.asList(daysValues),
////                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
////                "Forgrunn");
//
//        LineAndPointFormatter series1UpFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), Color.CYAN, new PointLabelFormatter());
//        plotUp.addSeries(series1Up, series1UpFormat);
//
//        LineAndPointFormatter series2UpFormat = new LineAndPointFormatter(Color.rgb(0, 300, 0), Color.rgb(0, 100, 0), Color.RED, new PointLabelFormatter());
////        plotUp.addSeries(series2Up, series2UpFormat);
//
//        plotUp.setTicksPerRangeLabel(3);
//        plotUp.getGraphWidget().setDomainLabelOrientation(0);

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
        if (id == R.id.action_datausage_readmore) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.Settings$DataUsageSummaryActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.action_settings) {
            DialogFragment newFragment = new FireMissilesDialogFragment();
            newFragment.show(getSupportFragmentManager(), "missiles");

        }

        return super.onOptionsItemSelected(item);
    }

    public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Varsler");
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View v = inflater.inflate(R.layout.data_notification_dialog_layout, null);

            Spinner spinner = (Spinner)v.findViewById(R.id.data_usage_spinner);
            spinner.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, dataMeasures));


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(v)
                    // Add action buttons
                    .setPositiveButton("Angi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    })
                    .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FireMissilesDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

}



