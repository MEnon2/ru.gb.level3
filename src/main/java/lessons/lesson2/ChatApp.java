package lessons.lesson2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ChatApp.class.getResource("/ChatFX.fxml"));

        Parent root = (Parent) loader.load();
        MainController ctrl = loader.getController();

        primaryStage.setTitle("Сетевой чат на курсе GeekBrains");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                 ctrl.getServerHandler().closeConnection();
           }
        });
    }

    @Override
    public void stop() throws Exception {


    }



}