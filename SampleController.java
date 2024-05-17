package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

public class SampleController {
	final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
	@FXML
	LineChart<String, Number> lineChart;
	@FXML
	Button handleRecord;
	@FXML
	Button selectFile;
	@FXML
	public void setValue(ActionEvent a) 
	{
		handleRecord.setText("Stop Recording");
		

		lineChart.setAnimated(false);
		lineChart.getData().clear();
		lineChart.setAnimated(true);
		XYChart.Series<String,Number> tempSeries=new XYChart.Series<String,Number>();
		tempSeries.getData().add(new XYChart.Data<String, Number>("20",200));
		lineChart.getData().add(tempSeries);
		
		
		/////
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        TargetDataLine microphone;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Microphone not supported");
            System.exit(0);
        }

        try {
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[2];
            int bytesRead = 0;
            List<AmplitudeData> amplitudes = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            
            long elapsedUniqueMillis = 0;
            
            while (true) {
                bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead == -1) break;

                // Convert bytes to amplitude value
                int amplitude = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);

                // Get current time in milliseconds since start
                long currentTimeMillis = System.currentTimeMillis();
                long elapsedMillis = currentTimeMillis - startTime;
                
                //delay 1ms to prevent overloading
                try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                if(elapsedUniqueMillis != elapsedMillis)
                {
                	amplitudes.add(new AmplitudeData(Long.toString(elapsedMillis), amplitude));
                }
                elapsedUniqueMillis = elapsedMillis;

                // For demonstration purposes, let's limit the recording to 10 seconds
                if (elapsedMillis > 10000) break;
            }

            microphone.close();

            // Print out the collected amplitude data
            for (AmplitudeData data : amplitudes) {
                System.out.println("Time: " + data.time + " ms, Amplitude: " + data.amplitude);
        		tempSeries.getData().add(new XYChart.Data<String, Number>(data.time,data.amplitude));
        		lineChart.getData().add(tempSeries);
        		
                if(Long.parseLong(data.time, 10) % 1000 == 0)
                {
                	lineChart.getData().clear();
                }
            }

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    static class AmplitudeData {
        String time;
        int amplitude;

        AmplitudeData(String time, int amplitude) {
            this.time = time;
            this.amplitude = amplitude;
        }
    }

		//////
		
	






///////////////////////////////////////////////////////////

public void handleFileButton(ActionEvent e) {
	
	lineChart.getData().clear();
	XYChart.Series<String,Number> tempSeries=new XYChart.Series<String,Number>();
	
	
	
	 // Specify the audio file path
    String audioFilePath = "/Users/yusuftalhabek/Downloads/dong3_Heart.wav";

    // Create File object for the audio file
    File audioFile = new File(audioFilePath);

    // Check if the file exists
    if (!audioFile.exists()) {
        System.out.println("Audio file does not exist: " + audioFilePath);
        return;
    }

    // Set up AudioInputStream to read from the file
    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
        // Get audio format from the audio input stream
        AudioFormat format = audioInputStream.getFormat();

        // Open a data line to play the audio (optional)
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        byte[] buffer = new byte[1024];
        int bytesRead;
        List<Byte> audioData = new ArrayList<>();
        List<AmplitudeData> amplitudes = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Read audio data from the file
        while ((bytesRead = audioInputStream.read(buffer)) != -1) {
            // Store audio data
            for (int i = 0; i < bytesRead; i++) {
                audioData.add(buffer[i]);
            }

            // Process audio data for amplitude every 100ms
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - startTime >= 100) {
                // Calculate amplitude
                int amplitude = calculateAmplitude(audioData);
                // Get elapsed time as String
                String elapsedTime = Long.toString(currentTimeMillis - startTime);
                // Store amplitude and time
                amplitudes.add(new AmplitudeData(elapsedTime, amplitude));
                // Clear audio data for next round
                audioData.clear();
                // Update start time for next interval
                startTime = currentTimeMillis;
            }

            // Play audio (optional)
            line.write(buffer, 0, bytesRead);
        }

        // Stop and close the data line (optional)
        line.drain();
        line.stop();
        line.close();

        // Print out the collected amplitude data
        
        
        
        /////
        // Print out the collected amplitude data
        for (AmplitudeData data : amplitudes) {
            System.out.println("Time: " + data.time + " ms, Amplitude: " + data.amplitude);
    		tempSeries.getData().add(new XYChart.Data<String, Number>(data.time,data.amplitude));
    		lineChart.getData().add(tempSeries);
    		
            if(Long.parseLong(data.time, 10) % 1000 == 0)
            {
            	//lineChart.getData().clear();
            }
        }

           
        /////

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
        e1.printStackTrace();
    }
}

// Calculate amplitude from audio data
private static int calculateAmplitude(List<Byte> audioData) {
    double sum = 0;
    for (byte b : audioData) {
        sum += Math.abs(b);
    }
    double average = sum / audioData.size();
    return (int) average;
}
}

///////////////////////////////////////////////////////////




