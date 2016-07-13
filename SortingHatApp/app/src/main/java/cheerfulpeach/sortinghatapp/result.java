package cheerfulpeach.sortinghatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

public class result extends DialogFragment implements DialogInterface.OnClickListener {

    private View form=null;

    TextView resultText;

    String message;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        form = getActivity().getLayoutInflater().inflate(R.layout.result, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return (builder.setTitle("Отправка результата").setView(form)
                .setPositiveButton(android.R.string.ok, this).create());

        super.onCreate(savedInstanceState); {
            Intent intent = getIntent();
            message = intent.getStringExtra(MainActivity.Extra_Message);

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        resultText.setText(message);
    }
    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }
    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}