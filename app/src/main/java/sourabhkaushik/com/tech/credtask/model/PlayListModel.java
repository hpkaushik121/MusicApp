package sourabhkaushik.com.tech.credtask.model;

/**
 * Created by Sourabh kaushik on 11/16/2019.
 */
public class PlayListModel {
    private String title;
    private String image;
    private String description;
    private String songUrl;
    private boolean playing=false;

    public PlayListModel(String title, String image, String description, String songUrl, boolean playing) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.songUrl = songUrl;
        this.playing = playing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
