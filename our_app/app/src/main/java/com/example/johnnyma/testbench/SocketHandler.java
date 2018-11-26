package com.example.johnnyma.testbench;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Allows the use of sharing a socket from any activity.
 */
public class SocketHandler {
    private static Socket socket;
    private static boolean disconnected = false;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static boolean isDisconnected() {return disconnected;}

    public static void setDisconnected(boolean bool) {disconnected = bool;}
}