package io.github.dearzack.minaclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

/**
 * Created by Zack on 2017/4/25.
 */

public class ConnectionManager {

    public static final String BROADCAST_ACTION = "io.github.dearzack.mina.receive";
    public static final String MESSAGE = "message";
    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<Context>(config.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

    public boolean connect() {
        try {
            ConnectFuture future = mConnection.connect(mAddress);
            future.awaitUninterruptibly();
            mSession = future.getSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mSession == null;
    }

    public void disConnection() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }


    private static class DefaultHandler extends IoHandlerAdapter {
        private Context mContext;

        DefaultHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            SessionManager.getInstance().setSession(session);
            Log.e("Mina", "sessionOpened");

        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.e("Mina", "messageReceived");
            if (mContext != null) {
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, message.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            Log.e("Mina", "messageSent");
            super.messageSent(session, message);
        }
    }

}
