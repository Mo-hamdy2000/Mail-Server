package GUI;

import Classes.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.LinearGradient;
import javafx.stage.Stage;

public class TrioMail extends Application {

	private final String sep = System.getProperty("file.separator");
	
	public static void main(String[] args) {
		launch(args);
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
        App app = new App();
        app.loadUsers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("signin.fxml"));
        Parent root = loader.load();
        Signin signinConntroller = loader.getController();
        signinConntroller.setApp(app);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TrioMail");
        try {
            primaryStage.getIcons().add(new Image("icon.png"));
        } catch (Exception e) {
            primaryStage.getIcons().add(new Image("GUI" + sep + "icon.png"));
        }
        primaryStage.show();
        primaryStage.setResizable(false);
	}

}
