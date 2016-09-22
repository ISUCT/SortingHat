package isuct.sortinghat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class Mark extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        Button result = (Button) findViewById(R.id.result);

        result.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText lang = (EditText) findViewById(R.id.language);
                EditText math = (EditText) findViewById(R.id.math);
                EditText sci = (EditText) findViewById(R.id.science);
                EditText chem = (EditText) findViewById(R.id.chemestry);

                int langNum = Integer.parseInt(lang.getText().toString());
                int mathNum = Integer.parseInt(math.getText().toString());
                int sciNum = Integer.parseInt(sci.getText().toString());
                int chemNum = Integer.parseInt(chem.getText().toString());

                ArrayList<Integer> mark = new ArrayList<Integer>();
                mark.add(langNum);
                mark.add(mathNum);
                mark.add(sciNum);
                mark.add(chemNum);

                Intent i = new Intent(Mark.this, Client.class);
                i.putIntegerArrayListExtra("mark", mark);
                startActivity(i);

//                AlertDialog.Builder builder = new AlertDialog.Builder(Mark.this);
//                builder.setTitle("Результат")
//                        .setMessage("")
//                        .setCancelable(true)
//                        .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
            }
        });

        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton);
        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });


        Button player = (Button) findViewById(R.id.player);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Mark.this, Player.class);
                startActivity(intent);
            }
        });
    }

}

