package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	public static Stage stage = null;

	@Override
	public void start(Stage stage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/Ui.fxml"));

		Scene scene = new Scene(root);
		stage.resizableProperty().setValue(Boolean.FALSE);
		stage.setTitle("Venmo User Interface Rev. 10   :   Rel Date  :  May 14, 2021     1:00 PM        ");
		stage.setScene(scene);
		this.stage = stage;
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}