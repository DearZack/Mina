package io.github.dearzack.minaclient;

import org.apache.mina.core.session.IoSession;

/**
 * Created by Zack on 2017/4/25.
 */

public class SessionManager {
    private static SessionManager mInstance = null;

    private IoSession mSession;

    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    public void setSession(IoSession session) {
        this.mSession = session;
    }

    public void writeToService(Object msg) {
        if (mSession != null) {
            mSession.write(msg);
        }
    }

    public void closeSession() {
        if (mSession != null) {
            mSession.closeOnFlush();
        }
    }

    public void removeSession() {
        if (mSession != null) {
            this.mSession = null;
        }
    }
}
