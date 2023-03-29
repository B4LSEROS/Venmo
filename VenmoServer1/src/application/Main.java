package application;
	
import java.lang.ModuleLayer.Controller;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;


public class Main extends Application 
{	
	public static Stage stage = null;
    @Override
    public void start(Stage stage) throws Exception 
    {
    	InetAddress ipAddress = null;
		try
		{
			ipAddress = InetAddress.getLocalHost();
		}
		catch (UnknownHostException el)
		{
			el.printStackTrace();
		}
    	
    	Parent root = FXMLLoader.load(getClass().getResource("/ServerIU.fxml"));   
    	
       
        Scene scene = new Scene(root);
        stage.setTitle("Venmo Socket Server Rev. 10   :   Rel Date  :  May 14, 2021     1:00 PM        " + 
                "IP : " + ipAddress.getHostAddress() + "     Port# : 3333");
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setScene(scene);
        this.stage =  stage;
        stage.show();   
    }
    
    
	public static void main(String[] args) 
	{
		launch(args);
	}
}