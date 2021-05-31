package lessons.lesson2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;


public class ClientChat extends Application {

    private final Socket socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
    private final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
    private final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

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

    public ClientChat() throws IOException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("chat_structure.fxml"));
        primaryStage.setTitle("Сетевой чат на курсе GeekBrains");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    public void createConnection() {

        Thread thread_read = new Thread(() -> {
            try {
                while (true) {
                    String str = dataInputStream.readUTF();
                    if (str.equals(ChatConstants.STOP_WORD)) {
                        labelNick.setText("");
                        System.out.println("Пришла команда завершения соединения. Разраываем соединение на клиенте.");
                        // closeConnection();
                        return;
                    } else if (str.startsWith(ChatConstants.CLIENTS_LIST)) {
                        String[] parts = str.split("\\s");
                        ObservableList<String> items = FXCollections.observableArrayList();
                        items.addAll(Arrays.asList(parts).subList(1, parts.length));

                        Platform.runLater(() -> userList.setItems(items));
                    } else if (str.startsWith(ChatConstants.AUTH_TIMEOUT)) {
                        if (mainChat != null) {
                            mainChat.appendText("Вышло время для авторизации." + "\n");
                            loginField.setEditable(false);
                            passField.setEditable(false);
                            btnAuth.setDisable(true);
                        }
                    } else if (str.startsWith(ChatConstants.AUTH_OK)) {
                        Platform.runLater(() -> {
                            loginField.setVisible(false);
                            passField.setVisible(false);
                            String[] parts = str.split("\\s");
                            if (parts.length > 1) {
                                labelNick.setText(parts[1]);
                            }
                            btnAuth.setText(ChatConstants.EXIT_TEXT);
                        });
                    } else if (str.startsWith(ChatConstants.AUTH_CHANGENICK_OK)) {
                        Platform.runLater(() -> {
                            String[] parts = str.split("\\s");
                            if (parts.length > 1) {
                                labelNick.setText(parts[1]);
                            }

                        });
                    } else {
                        if (mainChat != null) {
                            mainChat.appendText(str + "\n");
                        }
                    }
                }
            } catch (IOException ioException) {
                System.out.println("Произошло исключение на клиенте при чтении из потока.");
                ioException.printStackTrace();
            }
        });
        thread_read.start();
    }

    public void sendMessageToServer(String msg) {
        try {
            dataOutputStream.writeUTF(msg);
        } catch (IOException ioException) {
            System.out.println("Произошло исключение на клиенте при записи в поток.");
            ioException.printStackTrace();
        }
    }

    public void btnClickSend(ActionEvent actionEvent) {
        sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    public void btnClickAuth(ActionEvent actionEvent) {

        if (socket.isClosed()) {
            return;
        } else {
            createConnection();
        }

        if (btnAuth.getText().equals(ChatConstants.EXIT_TEXT)) {
            sendMessageToServer(ChatConstants.STOP_WORD);

            Platform.runLater(() -> {
                loginField.setVisible(true);
                passField.setVisible(true);
                mainChat.clear();
                userList.setItems(FXCollections.observableArrayList());
                labelNick.setText("");
                btnAuth.setText(ChatConstants.AUTH_TEXT);
            });
            closeConnection();

            Stage stage = (Stage) btnAuth.getScene().getWindow();
            stage.close();


        } else {
            sendMessageToServer(ChatConstants.AUTH_COMMAND + " " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        }
    }

    @FXML
    private void messageFieldPress(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            sendMessageToServer(messageField.getText());
            messageField.clear();
        }
    }

    public void userListClicked(MouseEvent event) {
        String messageText = messageField.getText().replaceAll(ChatConstants.SEND_TO_LIST + " ", "");
        messageText = messageText.replaceAll("/" + userList.getSelectionModel().getSelectedItem() + " ", "");
        messageField.setText(ChatConstants.SEND_TO_LIST + " /" + userList.getSelectionModel().getSelectedItem() + " " + messageText);
    }

    public void closeConnection() {
        try {
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
