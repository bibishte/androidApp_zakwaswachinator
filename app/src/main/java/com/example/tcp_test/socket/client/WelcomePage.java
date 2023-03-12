//package com.src.socket.client;
package com.example.tcp_test.socket.client;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.tcp_test.R;

public class WelcomePage  extends Activity {
    private Button send;
    private EditText ipPrompt;
    private EditText portPrompt;
    public static String serverIp;
    public static String portNo;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        send = (Button)findViewById(R.id.enterIP);
        portPrompt = (EditText) findViewById(R.id.portPrompt);
        ipPrompt = (EditText) findViewById(R.id.ipPrompt);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIp =  ipPrompt.getText().toString();
                portNo =  portPrompt.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if ( !serverIp.matches("[" + .0123456789 + "]+") || serverIp.contains(" ") || serverIp.isEmpty() || serverIp.equals(null) || serverIp == null ) {
                        ipPrompt.setText("");
                        Toast.makeText(WelcomePage.this, "Invalid IP Address!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent i = new Intent( view.getContext(), MainActivity.class);
                        startActivity(i);
                    }
                }
            }
        });
    }

    public static String getServerIp() {
        return serverIp;
    }
    public static String getPortNo() {
        return portNo;
    }
}
