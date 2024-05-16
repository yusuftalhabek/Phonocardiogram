package application;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Amplitude {
	 public static int[] extractAmplitudeData(String filePath) {
	        File audioFile = new File(filePath);
	        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
	            AudioFormat format = audioInputStream.getFormat();
	            byte[] audioBytes = audioInputStream.readAllBytes();
	            int[] amplitudes = new int[audioBytes.length / 2];

	            for (int i = 0; i < amplitudes.length; i++) {
	                int low = audioBytes[2 * i];
	                int high = audioBytes[2 * i + 1];
	                amplitudes[i] = (high << 8) + (low & 0xFF);
	            }
	            return amplitudes;
	        } catch (UnsupportedAudioFileException | IOException e) {
	            e.printStackTrace();
	            return new int[0];
	        }
	    }
}