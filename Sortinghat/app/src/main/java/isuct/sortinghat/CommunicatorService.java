package isuct.sortinghat;

import android.bluetooth.BluetoothSocket;

interface CommunicatorService {
    Communicator createCommunicatorThread(BluetoothSocket socket);
}