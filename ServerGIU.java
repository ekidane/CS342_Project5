package sample;

//so many imports!
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class driverClass extends Application{
 
	TextField text;
	Button btn;
	Stage myStage;
	Scene scene, scene2;
	HashMap<String, Scene> sceneMap;

public static void main(String[] args) {

launch(args);
}
 

public void start(Stage primaryStage) throws Exception {
	primaryStage.setTitle("A Number Guessing Game");
	
	myStage = primaryStage;
	
	btn = new Button("Start game");
	
	

	
	
	btn.setOnAction(new EventHandler<ActionEvent>(){
		
		public void handle(ActionEvent event){
			//game should start here
		}
	});
	


	BorderPane pane = new BorderPane();
	pane.setPadding(new Insets(70));
	
	VBox paneCenter = new VBox(10, text, btn);
	

	
	pane.setCenter(paneCenter);
	

	
	scene = new Scene(pane, 400, 500);


		
	primaryStage.setScene(scene);
	primaryStage.show();
	
	}

}