package clientui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class runApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		
        primaryStage.setScene(new Scene(new mainScreenController()));
		primaryStage.setTitle("FTP Client");
        primaryStage.show();
        
	}

	public static void main(String[] args) {
		launch(args);
	}
}
