package sourabhkaushik.com.tech.credtask.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sourabh kaushik on 10/10/2019.
 */
public interface ApiInterface {

    @GET("studio")
    Call<ResponseBody> getMusicList();



}
