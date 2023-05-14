//package com.src.socket.client;
package com.example.tcp_test.socket.client;
import static com.example.tcp_test.socket.client.WelcomePage.getPortNo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import android.os.Build;
import android.util.Log;

public class TCPClient implements Serializable {
    private String serverMessage;

	public String serverIp = "192.168.1.105";
    public static int SERVERPORT;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = true;
 
    private PrintWriter out = null;

    private Socket socket;



    //DataInputStream din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    private boolean invali_ip=false;
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener, String ipAddressOfServerDevice) throws IOException {
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
            socket = new Socket(serverAddr, Integer.parseInt(getPortNo()));
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

    public void sendStructData(OperationParam send_struct) throws IOException {
//        if (out != null && !out.checkError()) {
//
//            byte[] send_data_tmp = new byte[36];
//            byte[] send_data = new byte[34];
//            send_data_tmp=StructToByteArr(send_struct);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//                send_data=Arrays.copyOfRange(send_data_tmp, 154, 187);
//            }
//            //System.out.println("hmm " +send_data + "\n");
//            out.println(send_data);
//            out.flush();
//
//
//        }


        OutputStream out1 = new DataOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(out1);


        byte[] send_data_tmp = new byte[36];
        byte[] send_data = new byte[34];
        send_data_tmp=StructToByteArr(send_struct);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            send_data=Arrays.copyOfRange(send_data_tmp, 154, 187);
        }


        dos.write(send_data, 0, 33);
    }

    public byte[] StructToByteArr(OperationParam send_struct) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(send_struct);
        oos.flush();
        byte [] data = bos.toByteArray();
        return data;
    }

    public ProgramStruct readStructData(String prog_name, int number_steps_in_prog) throws IOException {
        InputStream stream = socket.getInputStream();
        byte[] data = new byte[36*number_steps_in_prog];
        int count = stream.read(data);
        int tmp=0;
        ProgramStruct returnStruct=new ProgramStruct();
        OperationParam tmpStruct= new OperationParam();
        returnStruct.progName=prog_name;
        for(int i=0; i<36*number_steps_in_prog; i=i+36)
        {
            byte[] tmpByte= new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                tmpByte = Arrays.copyOfRange(data, i, i+35);
            }
            tmpStruct= fillParams(tmpByte);
            returnStruct.program.add(tmpStruct);
            tmpByte=null;
        }

            tmp=stream.read(data);


        //stream.reset();
        //return data;
        return returnStruct;
        //return tmpStruct;
    }

    public OperationParam fillParams(byte[] data)
    {
        OperationParam opParam=new OperationParam();
        int zadanie_asInt = (data[0] & 0xFF)
                | ((data[1] & 0xFF) << 8)
                | ((data[2] & 0xFF) << 16)
                | ((data[3] & 0xFF) << 24);
        float zadanie_asFloat = Float.intBitsToFloat(zadanie_asInt);
        //opParam.setZadanie(zadanie_asFloat);
        opParam.zadanie=zadanie_asFloat;

        int kp_asInt = (data[4] & 0xFF)
                | ((data[5] & 0xFF) << 8)
                | ((data[6] & 0xFF) << 16)
                | ((data[7] & 0xFF) << 24);
        float kp_asFloat = Float.intBitsToFloat(kp_asInt);
        //opParam.setKp(kp_asFloat);
        opParam.kp=kp_asFloat;

        int kid_asInt = (data[8] & 0xFF)
                | ((data[9] & 0xFF) << 8)
                | ((data[10] & 0xFF) << 16)
                | ((data[11] & 0xFF) << 24);
        float kid_asFloat = Float.intBitsToFloat(kid_asInt);
        //opParam.setKid(kid_asFloat);
        opParam.kid=kid_asFloat;



        int limit_asInt = (data[12] & 0xFF)
                | ((data[13] & 0xFF) << 8)
                | ((data[14] & 0xFF) << 16)
                | ((data[15] & 0xFF) << 24);
        float limit_asFloat = Float.intBitsToFloat(limit_asInt);
        //opParam.setLimit(limit_asFloat);
        opParam.limit=limit_asFloat;


        int kd_asInt = (data[16] & 0xFF)
                | ((data[17] & 0xFF) << 8)
                | ((data[18] & 0xFF) << 16)
                | ((data[19] & 0xFF) << 24);
        float kd_asFloat = Float.intBitsToFloat(kd_asInt);
        //opParam.setKd(kd_asFloat);
        opParam.kd=kd_asFloat;


        int timeStart_asInt = (data[20] & 0xFF)
                | ((data[21] & 0xFF) << 8)
                | ((data[22] & 0xFF) << 16)
                | ((data[23] & 0xFF) << 24);
        float timeStart_asFloat = Float.intBitsToFloat(timeStart_asInt);
        //opParam.setTimeStart(timeStart_asFloat);
        opParam.timeStart=timeStart_asFloat;

        int timeStop_asInt = (data[24] & 0xFF)
                | ((data[25] & 0xFF) << 8)
                | ((data[26] & 0xFF) << 16)
                | ((data[27] & 0xFF) << 24);
        float timeStop_asFloat = Float.intBitsToFloat(timeStop_asInt);
        //opParam.setTimeStop(timeStop_asFloat);
        opParam.timeStop=timeStop_asFloat;


        int time_asInt = (data[28] & 0xFF)
                | ((data[29] & 0xFF) << 8)
                | ((data[30] & 0xFF) << 16)
                | ((data[31] & 0xFF) << 24);
        float time_asFloat = Float.intBitsToFloat(time_asInt);
        //opParam.setTime(time_asFloat);
        opParam.timeStart=time_asFloat;

        int rele2On_asInt = (data[32] & 0xFF);
        //opParam.setRele1On(false);
        //opParam.setRele1On(true);
        opParam.rele1On= rele2On_asInt != 0;

        int rele3On_asInt = (data[33] & 0xFF);
        //opParam.setRele3On(false);
        //opParam.setRele3On(true);
        opParam.rele3On= rele3On_asInt != 0;

        return opParam;
    }
}

