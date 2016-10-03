package isuct.sortinghat;

interface Communicator {
    void startCommunication();
    void write(String message);
    void stopCommunication();
}