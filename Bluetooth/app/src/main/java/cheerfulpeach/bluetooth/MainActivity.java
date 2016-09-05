package cheerfulpeach.bluetooth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.bluetooth.*;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.*;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ArrayAdapter<BluetoothDevice> listAdapter;
    private ListView myListView;
    private BluetoothAdapter bluetoothAdapter;
     /* request BT enable */
    private static final int  REQUEST_ENABLE      = 0x1;
    /* request BT discover */
    private static final int  REQUEST_DISCOVERABLE  = 0x2;

    public final static String UUID = "d491d460-6ebd-11e6-bdf4-0800200c9a66";

    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private ProgressDialog progressDialog;
    private Button searchButton;
    private Button btnOn;
    private Button btnOff;
    private Button btnDiscoverable;
    private Button btnAsServer;
    private Button btnAsClient;

    private ServerThread serverThread;
    private ClientThread clientThread;



    private final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(BluetoothSocket socket) {
            return new CommunicatorImpl(socket, new CommunicatorImpl.CommunicationListener() {
                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton quitButton = (ImageButton) findViewById(R.id.imageButton);

        bluetoothAdapter = getDefaultAdapter();
        searchButton = (Button) findViewById(R.id.btnSearch);
        btnOn = (Button)findViewById(R.id.btOn);
        btnOff = (Button)findViewById(R.id.btOff);
        btnAsClient = (Button)findViewById(R.id.btnAsClient);
        btnAsServer = (Button)findViewById(R.id.btnAsServer);
        btnDiscoverable = (Button)  findViewById(R.id.btDiscoverable);
        myListView = (ListView)findViewById(R.id.listView);

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


        btnAsServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverThread = new ServerThread(communicatorService);
                serverThread.start();
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

    private void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() >0){
            for(BluetoothDevice device: pairedDevices){
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
        if (clientThread != null) {
            clientThread.cancel();
        }
        if (serverThread != null) serverThread.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
