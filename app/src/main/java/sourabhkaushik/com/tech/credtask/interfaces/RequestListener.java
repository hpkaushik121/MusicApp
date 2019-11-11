package sourabhkaushik.com.tech.credtask.interfaces;

/**
 * Created by Sourabh kaushik on 11/9/2019.
 */
public interface RequestListener {
    void onStarted();
    void onSuccess();
    void OnFailure(String message);
}