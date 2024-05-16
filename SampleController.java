package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SampleController {
    @FXML
    private Button SelectFile;
    @FXML
    private Button StartRecording;
    @FXML
    private LineChart<Number, Number> Graphic;

    private TargetDataLine microphone;
    private Thread recordingThread;

    @FXML
    private void ActionStartRecording(ActionEvent e) {
        startMicrophoneRecording();
    }

    @FXML
    private void ActionSelectFile(ActionEvent e) {
        File audioFile = new File("path/to/your/audio/file.wav");
        processAudioFile(audioFile);
    }

    private void startMicrophoneRecording() {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Microphone not supported");
            return;
        }
        try {
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (true) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        List<Integer> amplitudes = extractAmplitudes(buffer, bytesRead);
                        updateChart(amplitudes);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            recordingThread.start();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private List<Integer> extractAmplitudes(byte[] buffer, int bytesRead) {
        List<Integer> amplitudes = new ArrayList<>();
        for (int i = 0; i < bytesRead; i += 2) {
            int amplitude = (buffer[i] << 8) | (buffer[i + 1] & 0xFF);
            amplitudes.add(amplitude);
        }
        return amplitudes;
    }

    private void updateChart(List<Integer> amplitudes) {
        Platform.runLater(() -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (int i = 0; i < amplitudes.size(); i++) {
                series.getData().add(new XYChart.Data<>(i, amplitudes.get(i)));
            }
            Graphic.getData().clear();
            Graphic.getData().add(series);
        });
    }

    private void processAudioFile(File audioFile) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            AudioFormat format = audioInputStream.getFormat();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                List<Integer> amplitudes = extractAmplitudes(buffer, bytesRead);
                updateChart(amplitudes);
                Thread.sleep(1000);
            }
        } catch (UnsupportedAudioFileException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
