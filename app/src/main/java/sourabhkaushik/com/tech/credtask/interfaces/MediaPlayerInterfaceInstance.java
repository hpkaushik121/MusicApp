package sourabhkaushik.com.tech.credtask.interfaces;

import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;

/**
 * Created by Sourabh kaushik on 11/6/2019.
 */
public class MediaPlayerInterfaceInstance {
    private static MediaPlayerInterfaceInstance instanc;
    private static MediaPlayerInterface mpinterface;

    public static MediaPlayerInterfaceInstance getInstance(){
        if(instanc==null){
            instanc=new MediaPlayerInterfaceInstance();
        }
        return instanc;
    }

    public  void setMpinterface(MediaPlayerInterface mpinterface) {
        MediaPlayerInterfaceInstance.mpinterface = mpinterface;
    }

    public  MediaPlayerInterface getMpinterface() {
        return mpinterface;
    }
}
