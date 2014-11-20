/*
javafxfilebrowsedemo - Demo application for browsing files in a JavaFX TreeView
Copyright (C) 2012 Hugues Johnson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; version 2.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package javafxfilebrowsedemo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXFileBrowseDemoApp extends Application{
    private TreeView<String> treeView;
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage){
        //create tree pane
        VBox treeBox=new VBox();
        treeBox.setPadding(new Insets(10,10,10,10));
        treeBox.setSpacing(10);
        //setup the file browser root
        String hostName="computer";
        try{hostName=InetAddress.getLocalHost().getHostName();}catch(UnknownHostException x){}
        TreeItem<String> rootNode=new TreeItem<>(hostName,new ImageView(new Image(ClassLoader.getSystemResourceAsStream("com/huguesjohnson/javafxfilebrowsedemo/computer.png"))));
        Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
        for(Path name:rootDirectories){
            FilePathTreeItem treeNode=new FilePathTreeItem(name);
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
        //create the tree view
        treeView=new TreeView<>(rootNode);
        //add everything to the tree pane
        treeBox.getChildren().addAll(new Label("File browser"),treeView);
        VBox.setVgrow(treeView,Priority.ALWAYS);
       
        //setup and show the window
        primaryStage.setTitle("JavaFX File Browse Demo");
        StackPane root=new StackPane();
        root.getChildren().addAll(treeBox);
        primaryStage.setScene(new Scene(root,400,300));
        primaryStage.show();
    }
}
