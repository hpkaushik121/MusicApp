
package sourabhkaushik.com.tech.credtask.model;

import com.google.gson.annotations.SerializedName;


public class SongItemModel {

    @SerializedName("artists")
    private String mArtists;
    @SerializedName("cover_image")
    private String mCoverImage;
    @SerializedName("song")
    private String mSong;
    @SerializedName("url")
    private String mUrl;

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

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
