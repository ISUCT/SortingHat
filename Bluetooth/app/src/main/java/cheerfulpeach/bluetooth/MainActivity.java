package cheerfulpeach.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.bluetooth.*;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ArrayAdapter<BluetoothDevice> listAdapter;
    private ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myListView = (ListView)findViewById(R.id.listView);
    myListView.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          
                                      }
                                  }
    );
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter bluetooth= BluetoothAdapter.getDefaultAdapter();

                // Проверка поддержки Bluetooth
                if(bluetooth!=null)
                {
                    //С Bluetooth все в порядке
                }

                // Проверяем включен ли Bluetooth
                if (bluetooth.isEnabled()) {
                    //Bluetooth включен
                }
                else
                {
                    // Bluetooth выключен. Предложим пользователю включить его.
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                String status;
                if(bluetooth.isEnabled()){
                    String mydeviceaddress= bluetooth.getAddress();
                    String mydevicename= bluetooth.getName();
                    status= mydevicename+" : "+ mydeviceaddress;
                }
                else
                {
                    status="Bluetooth выключен";
                }

                Toast.makeText(MainActivity.this, status, Toast.LENGTH_LONG).show();
            }
        });

        ImageButton quitButton = (ImageButton) findViewById(R.id.imageButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
