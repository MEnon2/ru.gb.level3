package lessons.lesson2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BaseAuthService implements AuthService {

    static final String DATABASE_URL = "jdbc:sqlite:javadb.db";
    static Connection connection;
    static Statement statement;

    static {
        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws SQLException {

        System.out.println("Auth start");
        createTable();

    }

    @Override
    public void stop() {
        System.out.println("Auth stop");
    }

    @Override
    public boolean createUser(String login, String pass, String nick) throws SQLException {
        Optional<Entry> foundUser = selectUserFromLoginAndPass(login, pass);
        if (!foundUser.isPresent()) {
            insertUser(login, pass, nick);
            System.out.println("User <" + nick + "> added successfully");

            foundUser = selectUserFromLoginAndPass(login, pass);
            if (foundUser.isPresent()) {
                entries.add(foundUser.get());
            } else {
                return false;
            }

        } else {
            System.out.println("User <" + nick + "> is already created");
            return false;
        }

        return true;
    }

    @Override
    public boolean removeUser(String login, String pass) {
        return false;
    }

    @Override
    public Optional<String> getNickFromLoginAndPass(String login, String pass) {
        if (login.isEmpty() || pass.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream().filter(e -> e.login.equals(login)).filter(e -> e.pass.equals(pass)).map(e -> e.nick).findFirst();
    }

    private class Entry {
        private final String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private List<Entry> entries = new ArrayList<>();

    public BaseAuthService() throws SQLException {
        selectUsers();
        for (int i = 0; i < 10; i++) {
            createUser("login" + i, "pass" + i, "nick" + i);
        }


    }

    private void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS chatUsers" +
                "(" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "login TEXT NOT NULL UNIQUE," +
                "pass TEXT NOT NULL," +
                "nick TEXT NOT NULL" +
                ")";
        statement.execute(createTableSQL);

    }

    private void insertUser(String login, String pass, String nick) throws SQLException {
        String createTableSQL = "INSERT INTO chatUsers (login, pass, nick) VALUES ('" + login + "', '" + pass + "', '" + nick + "')";
        statement.execute(createTableSQL);
    }

    private Optional<Entry> selectUserFromLoginAndPass(String login, String pass) throws SQLException {
        Optional<Entry> foundUser = Optional.empty();
        String createTableSQL = "SELECT * FROM chatUsers WHERE login = '" + login + "' AND pass = '" + pass + "'";
        ResultSet rs = statement.executeQuery(createTableSQL);
        if (rs.next()) {
            foundUser = Optional.of(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
        }
        return foundUser;
    }

    private void selectUsers() throws SQLException {
        String createTableSQL = "SELECT * FROM chatUsers";
        ResultSet rs = statement.executeQuery(createTableSQL);

        while (rs.next()) {
            entries.add(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
        }
    }


}
