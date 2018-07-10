package cheerfulpeach.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

public class Client extends AppCompatActivity {

    private ToggleButton tglMathBase;
    private ToggleButton tglMathAdv;
    private ToggleButton tglRussian;
    private ToggleButton tglChem;
    private ToggleButton tglPhysics;
    private ToggleButton tglInformatics;
    private ToggleButton tglObshestvo;
    private ToggleButton tglOthers;
    private Button result;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;

    public final static String SERVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    int [] grpchem = {2,3,4,5,9,13,21,25,26,30,33};
    int [] grpPhys = {8,10,11,12,14,29,31,32};
    int [] grpInform = {15,16,22,23};
    int [] grpObsh = {6,7,16,22};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.asclient);


        bluetoothAdapter = getDefaultAdapter();

        tglMathBase = (ToggleButton)findViewById(R.id.tglMathBase);
        tglMathAdv = (ToggleButton)findViewById(R.id.tglMathAdv);
        tglRussian= (ToggleButton)findViewById(R.id.tglRussian);
        tglChem= (ToggleButton)findViewById(R.id.tglChem);
        tglPhysics= (ToggleButton)findViewById(R.id.tglPhysics);
        tglInformatics= (ToggleButton)findViewById(R.id.tglInformatics);
        tglObshestvo= (ToggleButton)findViewById(R.id.tglObshestvo);
        tglOthers= (ToggleButton)findViewById(R.id.tglOthers);
        result = (Button)findViewById(R.id.result);

        Intent intent = getIntent();
        String addr = intent.getStringExtra("device");

        BluetoothDevice deviceSelected = bluetoothAdapter.getRemoteDevice(addr);
        try {
            btSocket = deviceSelected.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            btSocket.connect();
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }



        tglMathAdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglMathAdv.isChecked()){
                    tglMathBase.setChecked(false);
                }
            }
        });

        tglMathBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglMathBase.isChecked()){
                    tglMathAdv.setChecked(false);
                }
            }
        });

        tglPhysics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglPhysics.isChecked()){
                    tglChem.setChecked(false);
                }
            }
        });


        tglChem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglChem.isChecked()){
                    tglPhysics.setChecked(false);
                }
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tglMathBase.isChecked()){
                    sendData(0);
                    return;
                }
                if(tglChem.isChecked()){
                    int randi=(int) (Math.random()*grpchem.length);
                    sendData(grpchem[randi]);
                    return;
                }
                if(tglPhysics.isChecked()){
                    int randi=(int) (Math.random()*grpPhys.length);
                    sendData(grpPhys[randi]);
                    return;
                }

                if(tglInformatics.isChecked()){
                    int randi=(int) (Math.random()*grpInform.length);
                    sendData(grpInform[randi]);
                    return;
                }
                if(tglObshestvo.isChecked()||tglOthers.isChecked()){
                    int randi=(int) (Math.random()*grpObsh.length);
                    sendData(grpObsh[randi]);
                    return;
                }


            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();
        if (outStream != null) {
            try {
                outStream.flush();
                btSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }


    private void sendData(int message) {
//        byte[] msgBuffer = message;

        try {
            outStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
