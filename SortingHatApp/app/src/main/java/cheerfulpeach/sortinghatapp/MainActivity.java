package cheerfulpeach.sortinghatapp;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText FIO;
    String result;

    public final static String Extra_Message = "cheerfulpeach.sortinghatapp.MESSAGE";

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button resultButton = (Button) findViewById(R.id.result_button);
        resultButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                result = FIO.getText().toString();
                Intent intent = new Intent(MainActivity.this, result.class);
                intent.putExtra(Extra_Message, result);
                new result().show(getSupportFragmentManager(), "resultText");
                startActivity(intent);
            }
        });
    }
}




