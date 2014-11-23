package clientui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public abstract class Controller extends VBox{
	FXMLLoader loader;
	
	public Controller(String fxml) {  
		loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

	
}
