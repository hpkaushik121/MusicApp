package sourabhkaushik.com.tech.credtask;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sourabh kaushik on 11/3/2019.
 */
public class MainApplication extends Application {

    public static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext=getApplicationContext();
    }

    public static Context getAppContext(){
        return appContext;
    }
}
