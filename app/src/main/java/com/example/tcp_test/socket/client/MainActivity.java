package com.example.tcp_test.socket.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tcp_test.R;
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


                        // Code for adding Tab 3 to the tabhost
                        spec = tabhost.newTabSpec("Tab Three");
                        spec.setContent(R.id.tab3);
                        spec.setIndicator("Tab Three");
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
                                                        TextView op_zadanie_tv=new TextView(MainActivity.this);
                                                        EditText op_zadanie = new EditText(MainActivity.this);
                                                        op_zadanie_tv.setText("Zadanie");
                                                        op_zadanie.setText(String.valueOf(op.zadanie));
                                                        ll.addView(op_zadanie_tv, lp);
                                                        ll.addView(op_zadanie, lp);


                                                        TextView op_kp_tv=new TextView(MainActivity.this);
                                                        EditText op_kp = new EditText(MainActivity.this);
                                                        op_kp_tv.setText("Kp");
                                                        op_kp.setText(String.valueOf(op.kp));
                                                        ll.addView(op_kp_tv, lp);
                                                        ll.addView(op_kp, lp);


                                                        TextView op_kid_tv=new TextView(MainActivity.this);
                                                        EditText op_kid = new EditText(MainActivity.this);
                                                        op_kid_tv.setText("Kid");
                                                        op_kid.setText(String.valueOf(op.kid));
                                                        ll.addView(op_kid_tv, lp);
                                                        ll.addView(op_kid, lp);


                                                        TextView op_limit_tv=new TextView(MainActivity.this);
                                                        EditText op_limit = new EditText(MainActivity.this);
                                                        op_limit_tv.setText("Limit");
                                                        op_limit.setText(String.valueOf(op.limit));
                                                        ll.addView(op_limit_tv, lp);
                                                        ll.addView(op_limit, lp);


                                                        TextView op_kd_tv=new TextView(MainActivity.this);
                                                        EditText op_kd = new EditText(MainActivity.this);
                                                        op_kd_tv.setText("Kd");
                                                        op_kd.setText(String.valueOf(op.kd));
                                                        ll.addView(op_kd_tv, lp);
                                                        ll.addView(op_kd, lp);

                                                        TextView op_timeStart_tv=new TextView(MainActivity.this);
                                                        EditText op_timeStart = new EditText(MainActivity.this);
                                                        op_timeStart_tv.setText("timeStart");
                                                        op_timeStart.setText(String.valueOf(op.timeStart));
                                                        ll.addView(op_timeStart_tv, lp);
                                                        ll.addView(op_timeStart, lp);

                                                        TextView op_timeStop_tv=new TextView(MainActivity.this);
                                                        EditText op_timeStop = new EditText(MainActivity.this);
                                                        op_timeStop_tv.setText("timeStop");
                                                        op_timeStop.setText(String.valueOf(op.timeStop));
                                                        ll.addView(op_timeStop_tv, lp);
                                                        ll.addView(op_timeStop, lp);


                                                        TextView op_time_tv=new TextView(MainActivity.this);
                                                        EditText op_time = new EditText(MainActivity.this);
                                                        op_time_tv.setText("time");
                                                        op_time.setText(String.valueOf(op.time));
                                                        ll.addView(op_time_tv, lp);
                                                        ll.addView(op_time, lp);


                                                        TextView op_rele1On_tv=new TextView(MainActivity.this);
                                                        EditText op_rele1On = new EditText(MainActivity.this);
                                                        op_rele1On_tv.setText("rele1On");
                                                        op_rele1On.setText(String.valueOf(op.rele1On));
                                                        ll.addView(op_rele1On_tv, lp);
                                                        ll.addView(op_rele1On, lp);


                                                        TextView op_rele3On_tv=new TextView(MainActivity.this);
                                                        EditText op_rele3On = new EditText(MainActivity.this);
                                                        op_rele3On_tv.setText("rele3On");
                                                        op_rele3On.setText(String.valueOf(op.rele3On));
                                                        ll.addView(op_rele3On_tv, lp);
                                                        ll.addView(op_rele3On, lp);
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
                                        EditText steps_in_program = new EditText(MainActivity.this);
                                        Button add_program= new Button(MainActivity.this);
                                        steps_in_program.setText("Steps");
                                        add_program.setText("Add");

//                                        ScrollView scrollView = new ScrollView(MainActivity.this);
//                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                                        scrollView.setLayoutParams(layoutParams);

                                        LinearLayout ll = (LinearLayout)findViewById(R.id.tab3);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        ll.addView(steps_in_program, lp);
                                        ll.addView(add_program, lp);

                                        add_program.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View view) {
                                                        //tabhost.setCurrentTab(2);
                                                        EditText[] zadanie_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] kp_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] kid_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] limit_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] kd_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] timestart_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] timestop_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] time_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] rele1On_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        EditText[] rele3On_text = new EditText[Integer.parseInt(steps_in_program.getText().toString())];
                                                        for (int i = 0; i < Integer.parseInt(steps_in_program.getText().toString()); ++i) {
                                                                zadanie_text[i]=new EditText(MainActivity.this);
                                                                zadanie_text[i].setText("zadanie");
                                                                ll.addView(zadanie_text[i], lp);

                                                                kp_text[i]=new EditText(MainActivity.this);
                                                                kp_text[i].setText("kp");
                                                                ll.addView(kp_text[i], lp);

                                                                kid_text[i]=new EditText(MainActivity.this);
                                                                kid_text[i].setText("kid");
                                                                ll.addView(kid_text[i], lp);

                                                                limit_text[i]=new EditText(MainActivity.this);
                                                                limit_text[i].setText("limit");
                                                                ll.addView(limit_text[i], lp);

                                                                kd_text[i]=new EditText(MainActivity.this);
                                                                kd_text[i].setText("kd");
                                                                ll.addView(kd_text[i], lp);

                                                                timestart_text[i]=new EditText(MainActivity.this);
                                                                timestart_text[i].setText("time start");
                                                                ll.addView(timestart_text[i], lp);

                                                                timestop_text[i]=new EditText(MainActivity.this);
                                                                timestop_text[i].setText("time stop");
                                                                ll.addView(timestop_text[i], lp);

                                                                time_text[i]=new EditText(MainActivity.this);
                                                                time_text[i].setText("time");
                                                                ll.addView(time_text[i], lp);

                                                                rele1On_text[i]=new EditText(MainActivity.this);
                                                                rele1On_text[i].setText("rele1On");
                                                                ll.addView(rele1On_text[i], lp);

                                                                rele3On_text[i]=new EditText(MainActivity.this);
                                                                rele3On_text[i].setText("rele3On");
                                                                ll.addView(rele3On_text[i], lp);

                                                        }

                                                        Button add_steps= new Button(MainActivity.this);
                                                        add_steps.setText("Add step");
                                                        ll.addView(add_steps, lp);


                                                        add_steps.setOnClickListener(new View.OnClickListener() {
                                                                public void onClick(View view) {
                                                                        tabhost.setCurrentTab(1);

                                                                }
                                                        });

                                                }

                                        });
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