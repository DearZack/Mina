package io.github.dearzack.minaclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

public class MinaService extends Service {
    private ConnectionThread thread;
    public MinaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread = null;
    }

    class ConnectionThread extends HandlerThread {
        private Context context;
        boolean isConnection;
        ConnectionManager mManger;
        ConnectionThread(String name, Context context) {
            super(name);
            this.context = context;
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setReadBufferSize(10240)
                    .setIP("192.168.31.233")
                    .setPort(9123)
                    .setConnectionTimeout(10000)
                    .builder();
            mManger = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            for (;;) {
                isConnection = mManger.connect();
                if (isConnection) {
                    break;
                }

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void disConnection() {
            mManger.disConnection();
        }
    }
}
