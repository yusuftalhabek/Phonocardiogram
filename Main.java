package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Sample.fxml"));
            AnchorPane root = loader.load();
            
            // Set up the scene
            Scene scene = new Scene(root);

            // Set the scene to the stage and show it
            primaryStage.setScene(scene);
            primaryStage.setTitle("Audio Visualization");
            primaryStage.show();
        } catch(Exception e) {
            // Print stack trace for any exceptions
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
