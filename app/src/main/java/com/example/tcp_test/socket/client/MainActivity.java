package com.example.tcp_test.socket.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
        //TabHost.TabSpec spec
        private TabHost.TabSpec spec;
        private EditText progname_text;
        private int numb_progs_struc;
        private int numb_steps_prog;
        private String numb_progs_struc_str;
        private String numb_steps_prog_str;

        private Timer timer = new Timer();
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
                        //TabHost.TabSpec spec = tabhost.newTabSpec("Tab One");
                        spec = tabhost.newTabSpec("Tab One");
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
                                                //LinearLayout ll = (LinearLayout)findViewById(R.id.tab2);
                                                //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                        //LinearLayout.LayoutParams.WRAP_CONTENT);

                                                LinearLayout ll = findViewById(R.id.linear_layout_tab2);
                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                lp.setMargins(15, 0, 15, 0);

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
                                //Timer timer = new Timer();
                                TimerTask timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                                //Download file here and refresh
                                                if(mTcpClient!=null) {
                                                        updateMeasTemp();
                                                }
                                        }
                                };
                                timer.schedule(timerTask,1000, 7000);
                        } catch (IllegalStateException e){
                                Toast.makeText(MainActivity.this, "Disconnected! Restart", Toast.LENGTH_LONG).show();
                        }

                        add_fields.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                        // Code for adding Tab 3 to the tabhost
//                                        spec = tabhost.newTabSpec("Tab Three");
//                                        spec.setContent(R.id.tab3);
//                                        spec.setIndicator("Tab Three");
//                                        tabhost.addTab(spec);
                                        EditText steps_in_program = new EditText(MainActivity.this);
                                        Button add_program= new Button(MainActivity.this);
                                        steps_in_program.setText("Steps");
                                        add_program.setText("Add");

                                        LinearLayout ll_tab3 = findViewById(R.id.linear_layout_tab3);
                                        //LinearLayout ll_tab3=new LinearLayout(MainActivity.this);
                                        LinearLayout.LayoutParams lp_tab3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lp_tab3.setMargins(15, 0, 15, 0);


                                        ll_tab3.addView(steps_in_program, lp_tab3);
                                        ll_tab3.addView(add_program, lp_tab3);

                                        add_program.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View view) {

                                                        //tabhost.setCurrentTab(2);
                                                        progname_text = new EditText(MainActivity.this);
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

                                                        progname_text=new EditText(MainActivity.this);
                                                        progname_text.setText("progname");
                                                        ll_tab3.addView(progname_text, lp_tab3);

                                                        for (int i = 0; i < Integer.parseInt(steps_in_program.getText().toString()); ++i) {
//                                                                progname_text[i]=new EditText(MainActivity.this);
//                                                                progname_text[i].setText("progname");
//                                                                ll_tab3.addView(progname_text[i], lp_tab3);

                                                                zadanie_text[i]=new EditText(MainActivity.this);
                                                                zadanie_text[i].setText("zadanie");
                                                                ll_tab3.addView(zadanie_text[i], lp_tab3);

                                                                kp_text[i]=new EditText(MainActivity.this);
                                                                kp_text[i].setText("kp");
                                                                ll_tab3.addView(kp_text[i], lp_tab3);

                                                                kid_text[i]=new EditText(MainActivity.this);
                                                                kid_text[i].setText("kid");
                                                                ll_tab3.addView(kid_text[i], lp_tab3);

                                                                limit_text[i]=new EditText(MainActivity.this);
                                                                limit_text[i].setText("limit");
                                                                ll_tab3.addView(limit_text[i], lp_tab3);

                                                                kd_text[i]=new EditText(MainActivity.this);
                                                                kd_text[i].setText("kd");
                                                                ll_tab3.addView(kd_text[i], lp_tab3);

                                                                timestart_text[i]=new EditText(MainActivity.this);
                                                                timestart_text[i].setText("time start");
                                                                ll_tab3.addView(timestart_text[i], lp_tab3);

                                                                timestop_text[i]=new EditText(MainActivity.this);
                                                                timestop_text[i].setText("time stop");
                                                                ll_tab3.addView(timestop_text[i], lp_tab3);

                                                                time_text[i]=new EditText(MainActivity.this);
                                                                time_text[i].setText("time");
                                                                ll_tab3.addView(time_text[i], lp_tab3);

                                                                rele1On_text[i]=new EditText(MainActivity.this);
                                                                rele1On_text[i].setText("rele1On");
                                                                ll_tab3.addView(rele1On_text[i], lp_tab3);

                                                                rele3On_text[i]=new EditText(MainActivity.this);
                                                                rele3On_text[i].setText("rele3On");
                                                                ll_tab3.addView(rele3On_text[i], lp_tab3);

                                                        }

                                                        Button add_steps= new Button(MainActivity.this);
                                                        add_steps.setText("Add step");
                                                        ll_tab3.addView(add_steps, lp_tab3);


                                                        add_steps.setOnClickListener(new View.OnClickListener() {
                                                                public void onClick(View view) {
                                                                        //private final List<ProgramStruct> programs= new ArrayList<ProgramStruct>();
                                                                        ProgramStruct tmpProgStruct=new ProgramStruct();
                                                                        OperationParam[] tmpOperstionStruct= new OperationParam[Integer.parseInt(steps_in_program.getText().toString())];
//                                                                        List<OperationParam> tmpOperstionStruct= new ArrayList<OperationParam>();
                                                                        tmpProgStruct.progName=progname_text.getText().toString();
                                                                        for (int i = 0; i < Integer.parseInt(steps_in_program.getText().toString()); ++i) {
                                                                                System.out.println("hjsfjskfsf" + Float.valueOf(zadanie_text[i].getText().toString()) + "\n");

                                                                                tmpOperstionStruct[i]=new OperationParam();
                                                                                tmpOperstionStruct[i].zadanie=Float.valueOf(zadanie_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].kp=Float.valueOf(kp_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].kid=Float.valueOf(kid_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].limit=Float.valueOf(limit_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].kd=Float.valueOf(kd_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].timeStart=Float.valueOf(timestart_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].timeStop=Float.valueOf(timestop_text[i].getText().toString());
                                                                                tmpOperstionStruct[i].time=Float.valueOf(time_text[i].getText().toString());
                                                                                if(rele1On_text[i].getText().toString()=="t")
                                                                                {
                                                                                        tmpOperstionStruct[i].rele1On=true;
                                                                                }else
                                                                                {
                                                                                        tmpOperstionStruct[i].rele1On=false;
                                                                                }

                                                                                if(rele3On_text[i].getText().toString()=="t")
                                                                                {
                                                                                        tmpOperstionStruct[i].rele3On=true;
                                                                                }else
                                                                                {
                                                                                        tmpOperstionStruct[i].rele3On=false;
                                                                                }



                                                                                tmpProgStruct.program= Arrays.asList(tmpOperstionStruct);


                                                                        }
                                                                        programs.add(tmpProgStruct);
                                                                        tabhost.setCurrentTab(1);
                                                                        numb_progs_struc=programs.size();
                                                                        numb_progs_struc_str="transferStruct=" + numb_progs_struc;
                                                                        //numb_steps_prog;
                                                                        mTcpClient.sendMessage(numb_progs_struc_str);

                                                                        final Handler handler = new Handler();
                                                                        handler.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                        for (ProgramStruct progs : programs) {
                                                                                                mTcpClient.sendMessage(progs.progName);
                                                                                                numb_steps_prog=progs.program.size();
                                                                                                numb_steps_prog_str= String.valueOf(numb_progs_struc);
                                                                                                mTcpClient.sendMessage(numb_steps_prog_str);
                                                                                                for(OperationParam op : progs.program)
                                                                                                {
                                                                                                        final Handler handler = new Handler();
                                                                                                        handler.postDelayed(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                        try {

                                                                                                                                mTcpClient.sendStructData(op);
                                                                                                                        } catch (
                                                                                                                                IOException e) {
                                                                                                                                throw new RuntimeException(e);
                                                                                                                        }
                                                                                                                }
                                                                                                        }, 1000);
                                                                                                }


                                                                                        }
                                                                                }
                                                                        }, 1000);


                                                                                ViewGroup layout_progname = (ViewGroup) progname_text.getParent();
                                                                        if(null!=layout_progname) //for safety only  as you are doing onClick
                                                                                layout_progname.removeView(progname_text);
                                                                        for (int i = 0; i < Integer.parseInt(steps_in_program.getText().toString()); ++i) {
//                                                                                ViewGroup layout_progname = (ViewGroup) progname_text[i].getParent();
//                                                                                if(null!=layout_progname) //for safety only  as you are doing onClick
//                                                                                        layout_progname.removeView(progname_text[i]);

                                                                                ViewGroup layout_kp_text = (ViewGroup) kp_text[i].getParent();
                                                                                if(null!=layout_kp_text) //for safety only  as you are doing onClick
                                                                                        layout_kp_text.removeView(kp_text[i]);
                                                                                ViewGroup layout_zadanie_text = (ViewGroup) zadanie_text[i].getParent();
                                                                                if(null!=layout_zadanie_text) //for safety only  as you are doing onClick
                                                                                        layout_zadanie_text.removeView(zadanie_text[i]);
                                                                                ViewGroup layout_kid_text = (ViewGroup) kid_text[i].getParent();
                                                                                if(null!=layout_kid_text) //for safety only  as you are doing onClick
                                                                                        layout_kid_text.removeView(kid_text[i]);

                                                                                ViewGroup layout_limit_text = (ViewGroup) limit_text[i].getParent();
                                                                                if(null!=layout_limit_text) //for safety only  as you are doing onClick
                                                                                        layout_limit_text.removeView(limit_text[i]);

                                                                                ViewGroup layout_kd_text = (ViewGroup) kd_text[i].getParent();
                                                                                if(null!=layout_kd_text) //for safety only  as you are doing onClick
                                                                                        layout_kd_text.removeView(kd_text[i]);

                                                                                ViewGroup layout_timestart_text = (ViewGroup) timestart_text[i].getParent();
                                                                                if(null!=layout_timestart_text) //for safety only  as you are doing onClick
                                                                                        layout_timestart_text.removeView(timestart_text[i]);

                                                                                ViewGroup layout_timestop_text = (ViewGroup) timestop_text[i].getParent();
                                                                                if(null!=layout_timestop_text) //for safety only  as you are doing onClick
                                                                                        layout_timestop_text.removeView(timestop_text[i]);

                                                                                ViewGroup layout_time_text = (ViewGroup) time_text[i].getParent();
                                                                                if(null!=layout_time_text) //for safety only  as you are doing onClick
                                                                                        layout_time_text.removeView(time_text[i]);

                                                                                ViewGroup layout_rele1On_text = (ViewGroup) rele1On_text[i].getParent();
                                                                                if(null!=layout_rele1On_text) //for safety only  as you are doing onClick
                                                                                        layout_rele1On_text.removeView(rele1On_text[i]);

                                                                                ViewGroup layout_rele3On_text = (ViewGroup) rele3On_text[i].getParent();
                                                                                if(null!=layout_rele3On_text) //for safety only  as you are doing onClick
                                                                                        layout_rele3On_text.removeView(rele3On_text[i]);
                                                                        }

//
//
                                                                        ViewGroup layout_steps_in_program = (ViewGroup) steps_in_program.getParent();
                                                                        if(null!=layout_steps_in_program) //for safety only  as you are doing onClick
                                                                                layout_steps_in_program.removeView(steps_in_program);


                                                                        ViewGroup layout_add_program = (ViewGroup) add_program.getParent();
                                                                        if(null!=layout_add_program) //for safety only  as you are doing onClick
                                                                                layout_add_program.removeView(add_program);


                                                                        ViewGroup layout_add_steps = (ViewGroup) add_steps.getParent();
                                                                        if(null!=layout_add_steps) //for safety only  as you are doing onClick
                                                                                layout_add_steps.removeView(add_steps);
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