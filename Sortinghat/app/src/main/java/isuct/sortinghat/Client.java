package isuct.sortinghat;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

public class Client extends AppCompatActivity {

    public final static String UUID = "d491d460-6ebd-11e6-bdf4-0800200c9a66";
    final static int REQUEST_ENABLE_BT = 1;
    final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    ArrayAdapter<BluetoothDevice> listAdapter;
    ListView myListView;
    BluetoothAdapter bluetoothAdapter = getDefaultAdapter();
    /* request BT enable */
    static final int REQUEST_ENABLE = 0x1;
    /* request BT discover */
    static final int REQUEST_DISCOVERABLE = 0x2;

    BroadcastReceiver discoverDevicesReceiver;
    BroadcastReceiver discoveryFinishedReceiver;
    ProgressDialog progressDialog;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton);
        Button btnOn = (Button) findViewById(R.id.btOn);
        Button btnOff = (Button) findViewById(R.id.btOff);
        Button searchButton = (Button) findViewById(R.id.btnSearch);
        Button btnAsClient = (Button) findViewById(R.id.btnAsClient);
        ListView myListView = (ListView) findViewById(R.id.listView);

        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
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

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), discoveredDevices.get(i).getAddress(), Toast.LENGTH_SHORT).show();
                if (clientThread != null) {
                    clientThread.cancel();
                }
                BluetoothDevice deviceSelected = discoveredDevices.get(i);
                clientThread = new ClientThread(deviceSelected, communicatorService);
                clientThread.start();
            }
        });

        myListView.setAdapter(listAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices(view);
            }
        });

        btnAsClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientThread != null) {
                    new WriteTask().execute("Test");
                } else {
                    Toast.makeText(getApplicationContext(), "Сначала выберите клиента", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class WriteTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args) {
            try {
                clientThread.getCommunicator().write(args[0]);
            } catch (Exception e) {
                Log.d("MainActivity", e.getClass().getSimpleName() + " " + e.getLocalizedMessage());
            }
            return null;
        }
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

