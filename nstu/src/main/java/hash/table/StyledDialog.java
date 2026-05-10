package hash.table;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

public class StyledDialog {

    public static Optional<String> showInputDialog(Window owner, String title, String prompt) {
        return showInputDialog(owner, title, prompt, "");
    }

    public static Optional<String> showInputDialog(Window owner, String title, String prompt, String defaultValue) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);

        VBox root = new VBox(12);
        root.getStyleClass().add("styled-dialog");
        root.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");

        Label promptLabel = new Label(prompt);
        promptLabel.getStyleClass().add("dialog-prompt");
        promptLabel.setWrapText(true);

        TextField input = new TextField(defaultValue);
        input.getStyleClass().add("dialog-input");
        input.setPrefWidth(300);

        HBox btnBox = new HBox(8);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().addAll("action-btn", "action-btn-primary");
        okBtn.setPrefWidth(80);

        Button cancelBtn = new Button("Отмена");
        cancelBtn.getStyleClass().add("action-btn");
        cancelBtn.setPrefWidth(80);

        btnBox.getChildren().addAll(cancelBtn, okBtn);

        final String[] result = {null};
        okBtn.setOnAction(e -> {
            result[0] = input.getText();
            stage.close();
        });
        cancelBtn.setOnAction(e -> stage.close());
        input.setOnAction(e -> {
            result[0] = input.getText();
            stage.close();
        });

        root.getChildren().addAll(titleLabel, promptLabel, input, btnBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(StyledDialog.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);

        input.requestFocus();
        stage.showAndWait();

        return result[0] != null && !result[0].isEmpty() ? Optional.of(result[0]) : Optional.empty();
    }

    public static boolean showConfirmDialog(Window owner, String title, String message) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);

        VBox root = new VBox(12);
        root.getStyleClass().add("styled-dialog");
        root.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        HBox btnBox = new HBox(8);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button okBtn = new Button("Да");
        okBtn.getStyleClass().addAll("action-btn", "action-btn-primary");
        okBtn.setPrefWidth(80);

        Button cancelBtn = new Button("Нет");
        cancelBtn.getStyleClass().add("action-btn");
        cancelBtn.setPrefWidth(80);

        btnBox.getChildren().addAll(cancelBtn, okBtn);

        final boolean[] confirmed = {false};
        okBtn.setOnAction(e -> {
            confirmed[0] = true;
            stage.close();
        });
        cancelBtn.setOnAction(e -> stage.close());

        root.getChildren().addAll(titleLabel, messageLabel, btnBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(StyledDialog.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);

        stage.showAndWait();
        return confirmed[0];
    }

    public static void showMessageDialog(Window owner, String title, String message) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);

        VBox root = new VBox(12);
        root.getStyleClass().add("styled-dialog");
        root.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().addAll("action-btn", "action-btn-primary");
        okBtn.setPrefWidth(80);
        okBtn.setOnAction(e -> stage.close());

        HBox btnBox = new HBox();
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().add(okBtn);

        root.getChildren().addAll(titleLabel, messageLabel, btnBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(StyledDialog.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);

        okBtn.requestFocus();
        stage.showAndWait();
    }
}
