package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import audio.MP3Player;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerApp extends Application {
    private MP3Player mp3Player;
    private List<String> playlist = new ArrayList<>();
    private ListView<String> playlistView = new ListView<>();
    private Slider volumeSlider = new Slider(0, 1, 0.5);
    private Slider timeSlider = new Slider();
    private Label timeLabel = new Label("00:00 / 00:00");
    private Label volumeLabel = new Label("Volume");

    @Override
    public void start(Stage primaryStage) {
        // Gombok létrehozása
        Button playButton = new Button("Play");
        playButton.getStyleClass().add("play-button");

        Button pauseButton = new Button("Pause");
        pauseButton.getStyleClass().add("pause-button");

        Button stopButton = new Button("Stop");
        stopButton.getStyleClass().add("stop-button");

        Button addFolderButton = new Button("Add Folder");
        addFolderButton.getStyleClass().add("add-folder-button");

        // Hangerőszabályzó csúszka és felirat
        volumeLabel.getStyleClass().add("volume-label");
        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        VBox volumeBox = new VBox(5, volumeLabel, volumeSlider);

        // Idővonal csúszka
        timeSlider.getStyleClass().add("time-slider");
        timeSlider.setDisable(true);

        // Időcímke stílus
        timeLabel.getStyleClass().add("time-label");

        // Mappabetöltő funkció
        DirectoryChooser directoryChooser = new DirectoryChooser();
        addFolderButton.setOnAction(event -> {
            File folder = directoryChooser.showDialog(primaryStage);
            if (folder != null && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                if (files != null) {
                    playlist.clear();
                    playlistView.getItems().clear();
                    for (File file : files) {
                        playlist.add(file.getAbsolutePath());
                        playlistView.getItems().add(file.getName());
                    }
                }
            }
        });

        // Gombok eseménykezelése
        playButton.setOnAction(event -> {
            String selectedSong = playlistView.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                if (mp3Player != null) {
                    mp3Player.stop();
                }
                String filePath = playlist.get(playlistView.getSelectionModel().getSelectedIndex());
                mp3Player = new MP3Player(filePath);
                mp3Player.play();
                mp3Player.setVolume(volumeSlider.getValue());

                // Idővonal csúszka beállítása
                timeSlider.setDisable(false);
                mp3Player.getMediaPlayer().currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!timeSlider.isValueChanging()) {
                        timeSlider.setValue(newTime.toSeconds());
                        updateTimeLabel(newTime, mp3Player.getMediaPlayer().getTotalDuration());
                    }
                });

                mp3Player.getMediaPlayer().setOnReady(() -> {
                    Duration totalDuration = mp3Player.getMediaPlayer().getMedia().getDuration();
                    timeSlider.setMax(totalDuration.toSeconds());
                    updateTimeLabel(mp3Player.getMediaPlayer().getCurrentTime(), totalDuration);
                });

                timeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (timeSlider.isValueChanging()) {
                        mp3Player.getMediaPlayer().seek(Duration.seconds(newValue.doubleValue()));
                    }
                });
            }
        });

        pauseButton.setOnAction(event -> {
            if (mp3Player != null) mp3Player.pause();
        });
        stopButton.setOnAction(event -> {
            if (mp3Player != null) mp3Player.stop();
        });

        // Hangerő csúszka eseménykezelése
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mp3Player != null) mp3Player.setVolume(newValue.doubleValue());
        });

        // Elrendezés beállítása
        HBox buttonBox = new HBox(10, playButton, pauseButton, stopButton, addFolderButton);
        VBox root = new VBox(15, buttonBox, volumeBox, timeSlider, timeLabel, playlistView);

        Scene scene = new Scene(root, 600, 450);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Music Player App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateTimeLabel(Duration currentTime, Duration totalTime) {
        String currentTimeString = formatTime(currentTime);
        String totalTimeString = formatTime(totalTime);
        timeLabel.setText(currentTimeString + " / " + totalTimeString);
    }

    private String formatTime(Duration time) {
        int minutes = (int) time.toMinutes();
        int seconds = (int) (time.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
