package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

public class SampleController {
	@FXML
	private Button SelectFile;
	@FXML
	private Button StartRecording;
	private int[] audioData;
	@FXML
	private LineChart<Number,Number> Graphic;
	@FXML
	private void ActionStartRecording(ActionEvent e)
	{
		
	}
	@FXML
	private void ActionSelectFile(ActionEvent e)
	{
		String audioFilePath;
		audioFilePath = "/Users/yusuftalhabek/Desktop/HeartbeatWorkspace/Heart/dong3_Heart.wav";
		audioData = Amplitude.extractAmplitudeData(audioFilePath);
		
		NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> Graphic = new LineChart<>(xAxis, yAxis);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        

        double xScale = 1.0; // Scaling factor for x-axis
        for (int i = 0; i < audioData.length; i++) {
            series.getData().add(new XYChart.Data<>(i * xScale, audioData[i]));
        }

        Graphic.getData().add(series);
        
	}
}
