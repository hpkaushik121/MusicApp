package sourabhkaushik.com.tech.credtask.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.network.ApiInterface;

/**
 * Created by Sourabh kaushik on 11/6/2019.
 */
public class AppUtils {

    private static final String BASE_URL = "http://starlord.hackerearth.com/";

    public static void showToast(String message){
        Toast.makeText(MainApplication.getAppContext(),message,Toast.LENGTH_SHORT).show();
    }

    public static String getsongLength(int milliseconds){
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        return (minutes<10?"0"+minutes:minutes)+":"+(seconds<10?"0"+seconds:seconds);
    }
    public static int getSeekbarPercentage(int total,int played){
        if(total==0||played==0){
            return 0;
        }
        return (played*100)/total;
    }
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) MainApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = MainApplication.getAppContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    public static ApiInterface getApi(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60,TimeUnit.SECONDS);

        OkHttpClient okHttpClient=builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppUtils.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();
        return retrofit.create(ApiInterface.class);
    }

}
