package isuct.sortinghat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

public class Server extends AppCompatActivity {

    public final static String UUID = "d491d460-6ebd-11e6-bdf4-0800200c9a66";
    BluetoothAdapter bluetoothAdapter = getDefaultAdapter();
    /* request BT enable */
    static final int REQUEST_ENABLE = 0x1;
    /* request BT discover */
    static final int REQUEST_DISCOVERABLE = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        Button btnOn = (Button) findViewById(R.id.btOn);
        Button btnOff = (Button) findViewById(R.id.btOff);
        Button btnDiscoverable = (Button) findViewById(R.id.btDiscoverable);
        Button servStart = (Button) findViewById(R.id.servStart);
        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton);

        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerThread serverThread = new ServerThread(communicatorService);
                serverThread.start();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent enabler = new Intent(ACTION_REQUEST_ENABLE);
                startActivityForResult(enabler, REQUEST_ENABLE);
                bluetoothAdapter.enable();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.disable();
            }
        });

        btnDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ACTION_REQUEST_DISCOVERABLE);
                i.putExtra(EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(i);
            }
        });
    }

    public final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(BluetoothSocket socket) {
            return new CommunicatorImpl(socket, new CommunicatorImpl.CommunicationListener() {
                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    };
}
