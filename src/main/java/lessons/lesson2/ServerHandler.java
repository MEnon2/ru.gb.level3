package lessons.lesson2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler {

    private MainController mctrl;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public ServerHandler(MainController mctrl) {
        try {
            this.socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.mctrl = mctrl;

            new Thread(() -> {
                try {
                    readMessagesFromServer();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToServer(String msg) {
        try {
            dataOutputStream.writeUTF(msg);
        } catch (IOException ioException) {
            System.out.println("Произошло исключение на клиенте при записи в поток.");
            ioException.printStackTrace();
        }
    }

    public void readMessagesFromServer() throws IOException {
        try {
            while (true) {
                String str = dataInputStream.readUTF();
                if (str.equals(ChatConstants.STOP_WORD)) {
                    Platform.runLater(() -> mctrl.labelNick.setText(""));

                    System.out.println("Пришла команда завершения соединения. Разраываем соединение на клиенте.");

                    return;
                } else if (str.startsWith(ChatConstants.CLIENTS_LIST)) {
                    String[] parts = str.split("\\s");
                    ObservableList<String> items = FXCollections.observableArrayList();
                    items.addAll(Arrays.asList(parts).subList(1, parts.length));

                    Platform.runLater(() -> mctrl.userList.setItems(items));

                } else if (str.startsWith(ChatConstants.AUTH_TIMEOUT)) {
                    Platform.runLater(() -> {
                        mctrl.mainChat.appendText("Вышло время для авторизации." + "\n");
                        mctrl.loginField.setEditable(false);
                        mctrl.passField.setEditable(false);
                        mctrl.btnAuth.setDisable(true);
                    });

                } else if (str.startsWith(ChatConstants.AUTH_OK)) {
                    Platform.runLater(() -> {
                        mctrl.loginField.setVisible(false);
                        mctrl.passField.setVisible(false);
                        String[] parts = str.split("\\s");
                        if (parts.length > 1) {
                            mctrl.labelNick.setText(parts[1]);
                        }
                        mctrl.btnAuth.setText(ChatConstants.EXIT_TEXT);
                    });

                } else if (str.startsWith(ChatConstants.AUTH_CHANGENICK_OK)) {
                    Platform.runLater(() -> {
                        String[] parts = str.split("\\s");
                        if (parts.length > 1) {
                            mctrl.labelNick.setText(parts[1]);
                        }

                    });
                } else {
                    Platform.runLater(() -> {
                        //if (mctrl.mainChat != null) {
                            mctrl.mainChat.appendText(str + "\n");
//                        }
                    });
                }
            }
        } catch (IOException ioException) {
            System.out.println("Произошло исключение на клиенте при чтении из потока.");
            ioException.printStackTrace();
        }
    }


    public void closeConnection() {
        try {
            this.dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
