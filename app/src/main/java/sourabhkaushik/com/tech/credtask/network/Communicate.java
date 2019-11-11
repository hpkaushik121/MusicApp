package sourabhkaushik.com.tech.credtask.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sourabh kaushik on 10/10/2019.
 */
public class Communicate {
    public static MutableLiveData<String> resp=null;
    public static MutableLiveData<String> hitApi(Call<ResponseBody> bodyCall){
        resp=new MutableLiveData<>();
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        resp.setValue(response.body().string());
                    } catch (IOException e) {
                        resp.setValue(e.getMessage());
                    }
                }else{
                    try {
                        resp.setValue(response.errorBody().string());
                    } catch (IOException e) {
                        resp.setValue(e.getMessage());
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                resp.setValue(t.getMessage());
            }
        });
        return resp;

    }
}
