package pokercc.android.daynight.demo;

import android.app.Application;

import pokercc.android.nightmodel.NightModelManager;

/**
 * Created by like on 16/7/21.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NightModelManager.getInstance().init(this);
    }
}
