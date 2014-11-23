package clientui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class RenameController extends Controller implements Initializable {

	@FXML TextField renameTF;
	@FXML Button renameBtn;
	
	public RenameController(String fxml) {
		super(fxml);
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public String rename(){
		Stage stage = (Stage) renameBtn.getScene().getWindow();
		stage.close();
		return renameTF.getText();
	}
}
