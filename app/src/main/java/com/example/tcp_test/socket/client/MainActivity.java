package com.example.tcp_test.socket.client;

import java.net.Socket;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tcp_test.R;
/*Display Activity with sending messages to server*/

@SuppressLint("NewApi")
public class MainActivity extends Activity
{
    private TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    private String zad_rec=null;
    private String ipAddressOfServerDevice;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            ipAddressOfServerDevice = WelcomePage.getServerIp();

            final EditText editText = findViewById(R.id.editText);
            final EditText temp = findViewById(R.id.assign_temperature);
            Button send = findViewById(R.id.send_button);
            Button write_temp = findViewById(R.id.button_write_assign_temp);
            Button read_temp = findViewById(R.id.button_read_assign_temp);
            Button disconnect = findViewById(R.id.disconnect);


            //mTcpClient = null;
            // connect to the server
            conctTask = new connectTask();
            conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            if(conctTask.getip_invalid()==true)
            {
                setContentView(R.layout.welcome_page);
                Toast.makeText(MainActivity.this, "server not running", Toast.LENGTH_LONG).show();
            }
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = editText.getText().toString();
                    //sends the message to the server
                    if (mTcpClient != null)
                    {
                        mTcpClient.sendMessage(message);
                    }
                    editText.setText("");
                }
            });

            write_temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = "setZadanie " + temp.getText().toString();
                    //sends the message to the server
                    if (mTcpClient != null)
                    {
                        mTcpClient.sendMessage(message);
                    }
                }
            });
            read_temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTcpClient.sendMessage("zadanie?");
                    //serverListener.messageReceived(serverReadTemp);
                    zad_rec=mTcpClient.readMssg();
                    temp.setText(zad_rec);

                }
            });

            disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTcpClient.stopClient();
                    setContentView(R.layout.welcome_page);
                }
            });
        }


    }

    /*receive the message from server with asyncTask*/
    public class connectTask extends AsyncTask<String,String,TCPClient>{
        private boolean ip_invalid=false;
        @Override
        protected TCPClient doInBackground(String... message)
        {
            //create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    try
                    {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                        if(message!=null)
                        {
                            System.out.println("Return Message from Socket::::: >>>>> "+message);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            },ipAddressOfServerDevice);
            mTcpClient.run();

            if(mTcpClient!=null)
            {
                //mTcpClient.sendMessage("Initial Message when connected with Socket Server");
                System.out.println("connected");
            }
            if(mTcpClient.getinvali_ip()==true)
            {
                //setContentView(R.layout.welcome_page);
                //Toast.makeText(MainActivity.this, "server not running", Toast.LENGTH_LONG).show();
                ip_invalid=true;
            }
            return null;
        }

        public boolean getip_invalid() {
            return ip_invalid;
        }

    }

    @Override
    protected void onDestroy()
    {
        try
        {
            System.out.println("onDestroy.");
            mTcpClient.sendMessage("bye");
            mTcpClient.stopClient();
            conctTask.cancel(true);
            conctTask = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }


}