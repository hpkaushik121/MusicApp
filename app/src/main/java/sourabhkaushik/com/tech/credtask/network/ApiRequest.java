package sourabhkaushik.com.tech.credtask.network;

import androidx.lifecycle.LiveData;

import sourabhkaushik.com.tech.credtask.Utils.AppUtils;

/**
 * Created by Sourabh kaushik on 10/10/2019.
 */
public class ApiRequest {
    public static LiveData<String> getMusicList(){

        return Communicate.hitApi(AppUtils.getApi().getMusicList());
    }




}
