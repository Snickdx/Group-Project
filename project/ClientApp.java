package project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

	@Override
	public void start(Stage primaryStage) {
        UIController ui = new UIController();
        primaryStage.setScene(new Scene(ui));
		primaryStage.setTitle("FTP Client");
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
