package com.ccz.myvillage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import com.ccz.myvillage.common.db.DbHelper;
import com.ccz.myvillage.common.ws.WsMgr;

public class MyVillageApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        super.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    public class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        // running activity count
        private int running = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if(++running > 0) {
                if(WsMgr.getInst().isConnected() == false)
                    WsMgr.getInst().reConnectServer();
            }
            if (++running == 1) {
                // running activity is 1, app must be returned from background just now (or first launch)
            } else if (running > 1) {
                // 2 or more running activities, should be foreground already.
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (--running == 0) {
                WsMgr.getInst().disconnect();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}
