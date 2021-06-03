package lessons.lesson2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController {
    private ServerHandler serverHandler;

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    @FXML
    public TextField messageField;

    @FXML
    public TextField loginField;
    @FXML
    public TextField passField;
    @FXML
    public TextArea mainChat;
    @FXML
    public ListView<String> userList;
    @FXML
    public Button btnAuth;
    @FXML
    public Label labelNick;

    public MainController() {
        this.serverHandler = new ServerHandler(this);
    }

    public void btnClickSend(ActionEvent actionEvent) {

        serverHandler.sendMessageToServer(messageField.getText());

        Platform.runLater(() -> messageField.clear());
    }

    public void btnClickAuth(ActionEvent actionEvent) {

        if (btnAuth.getText().equals(ChatConstants.EXIT_TEXT)) {
            serverHandler.sendMessageToServer(ChatConstants.STOP_WORD);

            Platform.runLater(() -> {
                loginField.setVisible(true);
                passField.setVisible(true);
                mainChat.clear();
                userList.setItems(FXCollections.observableArrayList());
                labelNick.setText("");
                btnAuth.setText(ChatConstants.AUTH_TEXT);
            });

            serverHandler.closeConnection();

            Stage stage = (Stage) btnAuth.getScene().getWindow();
            stage.close();


        } else {
            serverHandler.sendMessageToServer(ChatConstants.AUTH_COMMAND + " " + loginField.getText() + " " + passField.getText());

        }
    }

    @FXML
    private void messageFieldPress(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            serverHandler.sendMessageToServer(messageField.getText());
            Platform.runLater(() -> messageField.clear());
        }
    }

    public void userListClicked(MouseEvent event) {
        String messageText = messageField.getText().replaceAll(ChatConstants.SEND_TO_LIST + " ", "");
        messageText = messageText.replaceAll("/" + userList.getSelectionModel().getSelectedItem() + " ", "");

        messageField.setText(ChatConstants.SEND_TO_LIST + " /" + userList.getSelectionModel().getSelectedItem() + " " + messageText);
    }

    public void sendTextToMainChat(String text) {
        Platform.runLater(() -> mainChat.appendText(text + "\n"));
    }

    public String getTextMainChat() {
        return mainChat.getText();
    }
}
