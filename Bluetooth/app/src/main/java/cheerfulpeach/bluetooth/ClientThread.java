package cheerfulpeach.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by jskonst on 05.09.16.
 */
public class ClientThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final CommunicatorService communicatorService;

    private volatile Communicator communicator;


    public ClientThread(BluetoothDevice device, CommunicatorService communicatorService) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.communicatorService = communicatorService;
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MainActivity.UUID));
        } catch (IOException e) {
            Log.d("ClientThread", e.getLocalizedMessage());
        }
        mmSocket = tmp;
    }

    public synchronized Communicator getCommunicator() {
        return communicator;
    }

    public void run() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            synchronized (this) {
                communicator = communicatorService.createCommunicatorThread(mmSocket);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ClientThread", "Start");
                    communicator.startCommunication();
                }
            }).start();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}