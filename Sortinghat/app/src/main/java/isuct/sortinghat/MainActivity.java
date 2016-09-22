package isuct.sortinghat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button client = (Button) findViewById(R.id.btnAsClient);
        Button serv = (Button) findViewById(R.id.btnAsServer);
        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton);

        imgBut = (ImageButton) findViewById(R.id.imageButton);
        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Mark.class);
                startActivity(intent);
            }
        });

        serv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Server.class);
                startActivity(intent);
            }
        });
    }
}

