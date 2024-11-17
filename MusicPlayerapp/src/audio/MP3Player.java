package audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

public class MP3Player {
    private MediaPlayer mediaPlayer;
    private String filePath;

    public MP3Player(String filePath) {
        this.filePath = filePath;
        initializePlayer();
    }

    private void initializePlayer() {
        Media media = new Media(Paths.get(filePath).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    public void play() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                return; // Már lejátszás alatt van, semmit nem kell tenni
            }
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause(); // Szünetelteti a lejátszást
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Leállítja a lejátszást
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
