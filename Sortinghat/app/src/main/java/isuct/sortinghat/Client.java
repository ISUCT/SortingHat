package isuct.sortinghat;

import android.app.ListActivity;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static android.bluetooth.BluetoothAdapter.*;

import java.util.ArrayList;
import java.util.List;

public class Client extends ListActivity {

    public final static String UUID = "d491d460-6ebd-11e6-bdf4-0800200c9a66";
    private final static int REQUEST_ENABLE_BT = 1;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    private ListView myListView;
    /* request BT enable */
    private static final int REQUEST_ENABLE = 0x1;
    /* request BT discover */
    private static final int REQUEST_DISCOVERABLE = 0x2;

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

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;

    private TextView textData;
    private EditText textMessage;

    private ProgressDialog progressDialog;

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
                            textData.setText(textData.getText().toString() + "\n" + message);
                        }
                    });
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        ImageButton imgBut = (ImageButton) findViewById(R.id.imageButton);
        Button btnOn = (Button) findViewById(R.id.btOn);
        Button btnOff = (Button) findViewById(R.id.btOff);
        Button searchButton = (Button) findViewById(R.id.btnSearch);
        Button btnAsClient = (Button) findViewById(R.id.btnAsClient);
        Button getRes = (Button) findViewById(R.id.getRes);
        Button btnDiscoverable = (Button) findViewById(R.id.btnDiscoverable);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        textData = (TextView) findViewById(R.id.data_text);
//        textMessage = (EditText) findViewById(R.id.message_text);

        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };
        setListAdapter(listAdapter);

        getRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                ArrayList<Integer> mark = i.getIntegerArrayListExtra("mark");
                System.out.println(mark);
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
//                    new Client.WriteTask().execute("Test");
                } else {
                    Toast.makeText(getApplicationContext(), "Сначала выберите клиента", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void makeDiscoverable(View view) {
        Intent i = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(i);
    }

    public void discoverDevices(View view) {

        discoveredDevices.clear();
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
                    getListView().setEnabled(true);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Поиск закончен. Выберите устройство для отправки ообщения.", Toast.LENGTH_LONG).show();
                    unregisterReceiver(discoveryFinishedReceiver);
                }
            };
        }

        registerReceiver(discoverDevicesReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        getListView().setEnabled(false);

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetoothAdapter.startDiscovery();
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
        serverThread = new ServerThread(communicatorService);
        serverThread.start();

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
        if (clientThread != null) {
            clientThread.cancel();
        }

        BluetoothDevice deviceSelected = discoveredDevices.get(position);

        clientThread = new ClientThread(deviceSelected, communicatorService);
        clientThread.start();

        Toast.makeText(this, "Вы подключились к устройству \"" + discoveredDevices.get(position).getName() + "\"", Toast.LENGTH_SHORT).show();
    }

    public void sendMessage(View view) {
        if (clientThread != null) {
//            new WriteTask().execute(textMessage.getText().toString());
//            textMessage.setText("");
        } else {
            Toast.makeText(this, "Сначала выберите клиента", Toast.LENGTH_SHORT).show();
        }
    }
}