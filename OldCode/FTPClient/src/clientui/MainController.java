/*
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package clientui;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import clientutils.AuthenticatedClient;
import clientutils.InvalidFTPCodeException;
import clientutils.PoorlyFormedFTPResponse;
import clientutils.ServerResp;

public class MainController extends Controller implements Initializable {
	
	FXMLLoader loader;
	AuthenticatedClient client;
	
	@FXML Button syncBtn, logoutBtn, uploadBtn, downloadBtn;
	@FXML VBox content;
	@FXML ListView<String> homeList, serverList;
	@FXML Label statusLbl;
	
	public static final ObservableList<String> localFiles = FXCollections.observableArrayList();
	public static final ObservableList<String> remoteFiles = FXCollections.observableArrayList();
	
	public MainController(AuthenticatedClient client) throws UnknownHostException, IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException {  
		super("mainView.fxml");
		this.client = client;
		
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem rename = new MenuItem("Rename");
		MenuItem delete = new MenuItem("Delete");

		contextMenu.getItems().addAll(rename, delete);
		
		rename.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	String selected=(String) serverList.getSelectionModel().getSelectedItem();
		    	
		    	RenameController rc = new RenameController("renameView.fxml");
		    	Stage dialogStage = new Stage();
		    	dialogStage.initModality(Modality.WINDOW_MODAL);
		    	dialogStage.setScene(new Scene(rc));
		    	dialogStage.show();
		    	rc.renameBtn.setOnAction(new EventHandler<ActionEvent>(){
		    		public void handle(ActionEvent event){
		    			updateStatus("Rename "+ selected + " to "+rc.renameTF.getText());
		    			System.out.println("Renaming "+selected+ " to "+ rc.renameTF.getText());
		    			try {
							updateStatus(client.rename(selected, rc.renameTF.getText()).getStat().toString());
						} catch (IOException | PoorlyFormedFTPResponse
								| InvalidFTPCodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			Stage stage = (Stage) rc.renameBtn.getScene().getWindow();
		    			stage.close();
		    		}	
		    	});
		    	
		    }
		});
		
		delete.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	String selected=(String) serverList.getSelectionModel().getSelectedItem();
		    	System.out.println("Deleting "+ selected);
		    	try {
					updateStatus(client.delete(selected).getStat().toString());
				} catch (IOException | PoorlyFormedFTPResponse
						| InvalidFTPCodeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		
		//String[] items = client.pwd();
		
		localFiles.addAll("file.txt","file2.txt","file3.txt");
		//remoteFiles.addAll("file4.txt","file5.c","file6.mp3");
		
		homeList.setItems(localFiles);
		serverList.setItems(remoteFiles);
		serverList.setContextMenu(contextMenu);
		
		//client.login();
		
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateStatus(String response){
		statusLbl.setText(response);
	}
	
	public void upload() throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException{
		String selected=(String) homeList.getSelectionModel().getSelectedItem();
		System.out.println("Uploading: "+selected);
		updateStatus(client.stor(globals.dir,selected).getStat().toString());
	}
	
	public void download() throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException{
		String selected=(String) serverList.getSelectionModel().getSelectedItem();
		System.out.println("Downloading: "+selected);
		updateStatus(client.retr(globals.dir, selected).getStat().toString());
	}

	public void sync() throws IOException, InvalidFTPCodeException, PoorlyFormedFTPResponse{
		System.out.println("Syncing Mirror");
		client.clientMirror(globals.dir);
		updateStatus("Mirror Synced!");
	}
	
	public void quit() throws IOException, PoorlyFormedFTPResponse, InvalidFTPCodeException{
		System.out.println("quit");
		client.quit();
		Stage stage = (Stage) logoutBtn.getScene().getWindow();
		stage.close();
	}
	
	public void loadView(){
		
	}
}
