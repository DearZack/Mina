package io.github.dearzack.minaclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button connect, send;
    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.text_msg);
        connect = (Button) findViewById(R.id.connect);
        send = (Button) findViewById(R.id.send);
        IntentFilter filter = new IntentFilter("io.github.dearzack.mina.receive");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MinaService.class);
                startService(intent);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.getInstance().writeToService("Hello Mina!");
            }
        });
    }

    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            textView.setText(intent.getStringExtra("message"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MinaService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
