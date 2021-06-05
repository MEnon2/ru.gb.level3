package lessons;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler {

    private MainController mctrl;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private String login;
    private String nick;

    public ServerHandler(MainController mctrl) {
        try {
            this.socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.mctrl = mctrl;
            this.nick = "";
            this.login = "";

            //А что если сделать их daemon ? один закончился, вырубятся и остальные?
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
                        mctrl.sendTextToMainChat("Вышло время для авторизации.");
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

                        nick = parts[1];
                        login = mctrl.loginField.getText();

                        openChatHistory();
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
                        mctrl.sendTextToMainChat(str);
                        saveChatHistory(str + "\n");
                    });
                }
            }
        } catch (IOException ioException) {
            System.out.println("Произошло исключение на клиенте при чтении из потока.");
            ioException.printStackTrace();
        }
    }

    public boolean saveChatHistory(String text) {
        if (!nick.isEmpty()) {

            File fileHistory = new File(login + "_chat_history.txt");

            try (OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileHistory, true))) {
                bufferedOutputStream.write(text.getBytes("UTF-8"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean openChatHistory() {
        if (nick.isEmpty()) {
            return false;
        }

        int defaultBufferSize = 8192;
        int countStringChat = 100;

        File fileHistory = new File(login + "_chat_history.txt");

        if (!fileHistory.exists()) {
            return false;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int fileSize = Math.toIntExact(fileHistory.length());
        byte[] historyToChatBytes = new byte[fileSize < defaultBufferSize ? fileSize : defaultBufferSize];

        try (InputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileHistory))) {
            int i = 0;
            while ((i = bufferedInputStream.read(historyToChatBytes)) != -1) {
                outStream.write(historyToChatBytes);
            }
            String[] parts = new String(outStream.toByteArray(), "UTF-8").split("\n");

            String historyToChatString = Arrays.stream(parts)
                    .skip(parts.length - countStringChat > 0 ? parts.length - countStringChat : 0)
                    .limit(countStringChat > parts.length ? parts.length : countStringChat)
                    .collect(Collectors.joining("\n"));

            mctrl.sendTextToMainChat(historyToChatString);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
