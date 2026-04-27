package hash.table;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        HashTableController controller = new HashTableController(stage);
        Scene scene = new Scene(controller.getRoot(), 1480, 920);
        scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm()
        );
        stage.setTitle("Hash Table — Linear Probing");
        stage.setMinWidth(1100);
        stage.setMinHeight(750);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
