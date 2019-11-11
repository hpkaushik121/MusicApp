
package sourabhkaushik.com.tech.credtask.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class AlbumListModel {

    @SerializedName("Albums")
    private List<Album> mAlbums;
    @SerializedName("status")
    private Boolean mStatus;

    public List<Album> getAlbums() {
        return mAlbums;
    }

    public void setAlbums(List<Album> albums) {
        mAlbums = albums;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

}
