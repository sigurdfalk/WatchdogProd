package no.ntnu.idi.watchdogprod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sigurdhf on 24.03.2015.
 */
public class QuestionDialogFragment extends DialogFragment {

    private String packageName;
    private ArrayList<PermissionFact> permissionFacts;
    private int n = 0;

    private TextView header;
    private TextView fact;

    public interface QuestionDialogListener {
        public void onQuestionnaireFinished();
    }

    QuestionDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (QuestionDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement QuestionDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getArguments().getString(ApplicationListActivity.PACKAGE_NAME);
        ExtendedPackageInfo packageInfo = ApplicationHelper.getExtendedPackageInfo(getActivity(), packageName);
        permissionFacts = packageInfo.getPermissionFacts();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_feeling_question, null);

        header = (TextView) dialog.findViewById(R.id.permission_fact_header);
        fact = (TextView) dialog.findViewById(R.id.permission_fact_fact);

        setHeaderAndFact(permissionFacts.get(n).getHeader(), permissionFacts.get(n).getFact());

        builder.setView(dialog);
        builder.setPositiveButton(R.string.show_more, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                n++;

                if (n < permissionFacts.size()) {
                    setHeaderAndFact(permissionFacts.get(n).getHeader(), permissionFacts.get(n).getFact());
                }
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // ToDo implement
                listener.onQuestionnaireFinished();
            }
        });

         return builder.create();
    }

    private void setHeaderAndFact(String header, String fact) {
        this.header.setText(header);
        this.fact.setText(fact);
    }
}
