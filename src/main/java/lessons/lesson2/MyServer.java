package lessons.lesson2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyServer {

    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket serverSocket = new ServerSocket(ChatConstants.PORT)) {
            System.out.println("Сервер запущен");
            authService = new BaseAuthService();
            authService.start();

            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключение");
                Socket socket = serverSocket.accept();
                System.out.println("Соединение с клиентом установленно");
                new ClientHandler(this, socket);
            }
        } catch (IOException | SQLException e) {
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void sendMsgToClient(ClientHandler from, List<String> nickTo, String msg) {
        List<ClientHandler> listClients = clients.stream().filter(e -> nickTo.contains(e.getName())).collect(Collectors.toList());

        for (ClientHandler ls : listClients) {
            ls.sendMsg("личное сообщение от " + from.getName() + ": " + msg);
        }
        from.sendMsg("личное сообщение для " + Arrays.toString(listClients.stream().map(ClientHandler::getName).toArray()) + ": " + msg);
    }

    public synchronized void broadcastClientsList() {
        String listClients = ChatConstants.CLIENTS_LIST + " " + clients.stream().map(ClientHandler::getName).collect(Collectors.joining(" "));
        broadcastMsg(listClients);
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientsList();
    }


}
