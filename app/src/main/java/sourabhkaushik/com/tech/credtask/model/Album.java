
package sourabhkaushik.com.tech.credtask.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class Album {

    @SerializedName("album")
    private List<Album> mAlbum;
    @SerializedName("artists")
    private String mArtists;
    @SerializedName("cover_image")
    private String mCoverImage;
    @SerializedName("song")
    private String mSong;
    @SerializedName("type")
    private String mType;
    @SerializedName("url")
    private String mUrl;

    public List<Album> getAlbum() {
        return mAlbum;
    }

    public void setAlbum(List<Album> album) {
        mAlbum = album;
    }

    public String getArtists() {
        return mArtists;
    }

    public void setArtists(String artists) {
        mArtists = artists;
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        mCoverImage = coverImage;
    }

    public String getSong() {
        return mSong;
    }

    public void setSong(String song) {
        mSong = song;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
