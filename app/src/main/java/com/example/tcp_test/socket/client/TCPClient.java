//package com.src.socket.client;
package com.example.tcp_test.socket.client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.tcp_test.R;
public class TCPClient {
    private String serverMessage;

	public String serverIp = "192.168.1.105";
    public static int SERVERPORT = 3333;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = true;
 
    private PrintWriter out = null;

    private Socket socket;
    private boolean invali_ip=false;
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener, String ipAddressOfServerDevice)
    {
        mMessageListener = listener;
        serverIp = ipAddressOfServerDevice;
    }
 
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
        	System.out.println("message: "+ message);
            out.println(message);
            out.flush();
        }
    }
 
    public void stopClient(){


        try
        {
            socket.close();
            mRun = false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void run() {

        //mRun = true;
        try {

            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIp);

            Log.e("TCP SI Client", "SI: Connecting...");

            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVERPORT);
            try {
                invali_ip=false;
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP SI Client", "SI: Sent.");

                Log.e("TCP SI Client", "SI: Done.");
                //receive the message which the server sends back
                //while (mRun) {
                    //serverMessage = readMssg();
                    //Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                //}

            }
            catch (Exception e)
            {
                Log.e("TCP SI Error2", "SI: Error2", e);
                stopClient();
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "mymessage ", Toast.LENGTH_SHORT).show();
            }
            finally
            {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                if(invali_ip==true) {
                    stopClient();
                    //Toast.makeText(getApplicationContext(), "mymessage ", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {

            Log.e("TCP SI Error11", "SI: Error1111", e);
            //Toast.makeText(getApplicationContext(), "mymessage ", Toast.LENGTH_SHORT).show();
            invali_ip=true;
        }
 
    }
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }


    public boolean getinvali_ip() {
        return invali_ip;
    }


    public String readMssg() {
        String received_mssg=null;
        BufferedReader in = null;
        String serverMessage_loc=null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //in this while the client listens for the messages sent by the server

            try {
                serverMessage_loc = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (serverMessage_loc != null && mMessageListener != null) {
                //call the method messageReceived from MyActivity class
                mMessageListener.messageReceived(serverMessage_loc);
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage_loc + "'");

            }


        return serverMessage_loc;
    }
}