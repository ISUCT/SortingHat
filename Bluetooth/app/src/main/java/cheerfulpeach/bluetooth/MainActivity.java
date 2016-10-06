package cheerfulpeach.bluetooth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.bluetooth.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.*;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ArrayAdapter<BluetoothDevice> listAdapter;
    private ListView myListView;
    private BluetoothAdapter bluetoothAdapter;

    /* request BT enable */
    private static final int REQUEST_ENABLE = 0x1;
    /* request BT discover */
    private static final int REQUEST_DISCOVERABLE = 0x2;

    //public final static String SERVICE_UUID = "d491d460-6ebd-11e6-bdf4-0800200c9a66";


    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private ProgressDialog progressDialog;
    private Button searchButton;
    private Button btnOn;
    private Button btnOff;
    private Button client;
    private int clickNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = getDefaultAdapter();
        searchButton = (Button) findViewById(R.id.btnSearch);
        btnOn = (Button) findViewById(R.id.btOn);
        btnOff = (Button) findViewById(R.id.btOff);
        myListView = (ListView) findViewById(R.id.listView);
        client = (Button) findViewById(R.id.client);

        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), discoveredDevices.get(i).getAddress(), Toast.LENGTH_SHORT).show();
                String addr = discoveredDevices.get(i).getAddress();

                Bundle bundle = new Bundle();
                Intent intent = new Intent(getApplicationContext(), Client.class);
                intent.putExtra("device", addr);
                startActivity(intent);

            }
        });

//        client.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (clickNum == 1) {
//                    clickNum = 0;
//                    sendData(1);
//                } else {
//                    sendData(0);
//                    clickNum += 1;
//                }
//            }
//        });

        myListView.setAdapter(listAdapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices(view);
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

    }


    public void discoverDevices(View view) {
        discoveredDevices.clear();
        getPairedDevices();
        listAdapter.notifyDataSetChanged();
        if (discoverDevicesReceiver == null) {
            discoverDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (!discoveredDevices.contains(device)) {
                            discoveredDevices.add(device);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    myListView.setEnabled(true);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Поиск закончен. Выберите устройство для отправки  cообщения.", Toast.LENGTH_LONG).show();
                    unregisterReceiver(discoveryFinishedReceiver);
                    unregisterReceiver(discoverDevicesReceiver);
                }
            };
        }

        registerReceiver(discoverDevicesReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(ACTION_DISCOVERY_FINISHED));

        myListView.setEnabled(false);

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetoothAdapter.startDiscovery();
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                discoveredDevices.add(device);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();
        if (discoverDevicesReceiver != null) {
            try {
                unregisterReceiver(discoverDevicesReceiver);
            } catch (Exception e) {
                Log.d("MainActivity", "Не удалось отключить ресивер " + discoverDevicesReceiver);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
