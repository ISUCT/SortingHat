package isuct.sortinghat;

/**
 * Created by Анастасия on 19.09.2016.
 */

import android.bluetooth.BluetoothSocket;

/**
 * Created by cyrusmith
 * All rights reserved
 * http://interosite.ru
 * info@interosite.ru
 */
interface CommunicatorService {
    Communicator createCommunicatorThread(BluetoothSocket socket);
}