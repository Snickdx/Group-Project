package clientui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunApp extends Application implements ControllerSwitcher {

	Stage stage;
	Controller current;
	
	@Override
	public void start(Stage primaryStage) {
		
      this.stage = primaryStage;
      showMain();
        
	}
	
	public void showLogin(){
		stage.close();
		stage.setScene(new Scene(new LoginController()));
		stage.setTitle("FTP Client");
	    stage.show();
	}
	
	public void showMain(){
		stage.close();
		stage.setScene(new Scene(new MainController()));
		stage.setTitle("FTP Client");
	    stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void setViewParent(Controller screenPage) {
		// TODO Auto-generated method stub
		
	}
}
