package com.example.tcp_test.socket.client;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tcp_test.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
/*Display Activity with sending messages to server*/

@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity
{

        private TCPClient mTcpClient = null;
        private MainActivity.connectTask conctTask = null;
        private String zad_rec=null;
        private String prog_name=null;
        private final String steps_in_prog=null;
        private String number_steps_read=null;
        private String steps_in_prog_concat=null;
        private String numb_progs=null;
        private int number_progs=0;
        private int number_steps_in_progs=0;
        private String meas_rec=null;
        private String ipAddressOfServerDevice;
        private final byte[] debugProgr=null;

        private final String debugProgr1=null;

        private String prog_names_concat=null;
        private String prog_numb=null;

        private final ProgramStruct single_programs=new ProgramStruct();
        private final List<ProgramStruct> programs= new ArrayList<ProgramStruct>();

        private final OperationParam tmpOpParams=new OperationParam();
        protected void onCreate(Bundle savedInstanceState) {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.main);

                        // initiating the tabhost
                        TabHost tabhost = findViewById(R.id.tabhost);

                        // setting up the tab host
                        tabhost.setup();

                        // Code for adding Tab 1 to the tabhost
                        TabHost.TabSpec spec = tabhost.newTabSpec("Tab One");
                        spec.setContent(R.id.tab1);

                        // setting the name of the tab 1 as "Tab One"
                        spec.setIndicator("Tab One");

                        // adding the tab to tabhost
                        tabhost.addTab(spec);

                        // Code for adding Tab 2 to the tabhost
                        spec = tabhost.newTabSpec("Tab Two");
                        spec.setContent(R.id.tab2);

                        // setting the name of the tab 1 as "Tab Two"
                        spec.setIndicator("Tab Two");
                        tabhost.addTab(spec);










                        ipAddressOfServerDevice = WelcomePage.getServerIp();

                        final EditText editText = findViewById(R.id.editText);
                        final EditText temp = findViewById(R.id.assign_temperature);

                        Button send = findViewById(R.id.send_button);
                        Button write_temp = findViewById(R.id.button_write_assign_temp);
                        Button read_temp = findViewById(R.id.button_read_assign_temp);
                        Button get_programs = findViewById(R.id.getPrograms);
                        Button disconnect = findViewById(R.id.disconnect);
                        Button  add_fields = findViewById(R.id.add_fields);


                        //mTcpClient = null;
                        // connect to the server
                        conctTask = new MainActivity.connectTask();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
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


                        read_temp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        mTcpClient.sendMessage("zadanie?");
                                        //serverListener.messageReceived(serverReadTemp);
                                        zad_rec=mTcpClient.readMssg();
                                        temp.setText(zad_rec);

                                }
                        });

                        get_programs.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                        mTcpClient.sendMessage("countProgs?");
                                        numb_progs=mTcpClient.readMssg();
                                        number_progs=Integer.parseInt(numb_progs);
                                        for(int i=0; i<number_progs; i++) {
                                                prog_name=null;
                                                prog_names_concat=null;
                                                prog_names_concat="nameProgs="+i;
                                                prog_name=null;
                                                mTcpClient.sendMessage(prog_names_concat);
                                                prog_name = mTcpClient.readMssg();

                                                steps_in_prog_concat="nameStepProgs="+i;
                                                mTcpClient.sendMessage(steps_in_prog_concat);
                                                number_steps_read=mTcpClient.readMssg();
                                                number_steps_in_progs=Integer.parseInt(number_steps_read);

                                                prog_numb="programs="+i;
                                                mTcpClient.sendMessage(prog_numb);


                                                try {
                                                        programs.add(mTcpClient.readStructData(prog_name, number_steps_in_progs));
                                                } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                }
                                                //programs.add(single_programs);



                                        }
                                        System.out.println("111111");
                                        for(ProgramStruct ps : programs)
                                        {
                                                TextView p_name_tv=new TextView(MainActivity.this);
                                                EditText p_name = new EditText(MainActivity.this);
                                                p_name.setText(ps.progName);
                                                p_name_tv.setText("Program Name");
                                                LinearLayout ll = (LinearLayout)findViewById(R.id.tab2);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                                ll.addView(p_name_tv, lp);
                                                ll.addView(p_name, lp);

                                                for(OperationParam op : ps.program)
                                                {
                                                        TextView op_name_tv=new TextView(MainActivity.this);
                                                        EditText op_name = new EditText(MainActivity.this);
                                                        op_name_tv.setText("Zadanie");
                                                        op_name.setText(String.valueOf(op.zadanie));
                                                        LinearLayout ll1= (LinearLayout)findViewById(R.id.tab2);
                                                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                                        ll.addView(op_name_tv, lp1);
                                                        ll1.addView(op_name, lp1);
                                                }
                                        }
                                }
                        });
                        disconnect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        mTcpClient.stopClient();
                                        setContentView(R.layout.welcome_page);
                                }
                        });

                        try {
                                Timer timer = new Timer();
                                TimerTask timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                                //Download file here and refresh
                                                if(mTcpClient!=null) {
                                                        updateMeasTemp();
                                                }
                                        }
                                };
                                timer.schedule(timerTask,1000, 3000);
                        } catch (IllegalStateException e){
                                Toast.makeText(MainActivity.this, "Disconnected! Restart", Toast.LENGTH_LONG).show();
                        }

                        add_fields.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                        EditText tv = new EditText(MainActivity.this);
                                        tv.setText("New");
                                        LinearLayout ll = (LinearLayout)findViewById(R.id.tab2);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        ll.addView(tv, lp);
                                }

                        });
                }
    }





        void updateMeasTemp()
        {
                final TextView meas_temp = findViewById(R.id.meas_temperature);
                try {
                        meas_temp.post(new Runnable() {
                                @Override
                                public void run() {
                                        mTcpClient.sendMessage("mTemp?");
                                        //serverListener.messageReceived(serverReadTemp);
                                        meas_rec = mTcpClient.readMssg();

                                        meas_temp.setText(meas_rec);
                                }
                        });
                }catch(Exception e)
                {
                        System.out.println("NESHTOOOO");
                }
        }


        /*receive the message from server with asyncTask*/
        public class connectTask extends AsyncTask<String,String,TCPClient>{
                private boolean ip_invalid=false;
                @Override
                protected TCPClient doInBackground(String... message)
                {
                        //create a TCPClient object and
                        try {
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
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
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