package lessons.lesson2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BaseAuthService implements AuthService {

    static final String DATABASE_URL = "jdbc:sqlite:javadb.db";
    static Connection connection;
    static Statement statement;
    static final String TABLE_NAME = "chatUsers";

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
        selectUsers();

        for (int i = 0; i < 10; i++) {
            createUser("login" + i, "pass" + i, "nick" + i);
        }
    }

    @Override
    public void stop() {
        System.out.println("Auth stop");
    }


    @Override
    public boolean updateUserInfo(String login, String pass, String field, String newValue) {
        return true;
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

    }


    //создание таблицы
    private void createTable() throws SQLException {
        if (createTableSQL()) {
            System.out.println("Table <" + TABLE_NAME + "> created");
        }
    }

    private boolean createTableSQL() throws SQLException {
        String QuerySQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "login TEXT NOT NULL UNIQUE," +
                "pass TEXT NOT NULL," +
                "nick TEXT NOT NULL" +
                ")";
        return statement.execute(QuerySQL);
    }

    //выборка пользователей
    private void selectUsers() throws SQLException {
        ResultSet rs = selectUsersSQL();

        while (rs.next()) {
            entries.add(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
        }
    }

    private ResultSet selectUsersSQL() throws SQLException {
        String QuerySQL = "SELECT * FROM " + TABLE_NAME;
        return statement.executeQuery(QuerySQL);
    }

    //получение пользователя
    private Optional<Entry> selectUser(String login, String pass) throws SQLException {
        Optional<Entry> foundUser = Optional.empty();

        ResultSet rs = selectUserSQL(login, pass);
        if (rs.next()) {
            foundUser = Optional.of(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
        }
        return foundUser;
    }

    private ResultSet selectUserSQL(String login, String pass) throws SQLException {
        String QuerySQL = "SELECT * FROM " + TABLE_NAME + " WHERE login = '" + login + "' AND pass = '" + pass + "'";
        return statement.executeQuery(QuerySQL);
    }


    @Override
    public Optional<String> getUserNick(String login, String pass) {
        if (login.isEmpty() || pass.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream().filter(e -> e.login.equals(login)).filter(e -> e.pass.equals(pass)).map(e -> e.nick).findFirst();
    }

    //добавление пользователей
    @Override
    public boolean createUser(String login, String pass, String nick) throws SQLException {
        if (login.isEmpty() || pass.isEmpty() || nick.isEmpty()) {
            System.out.println("The login, password and  nickname cannot be empty");
            return false;
        }
        return createUserSQL(login, pass, nick);
    }

    private boolean createUserSQL(String login, String pass, String nick) throws SQLException {
        String createTableSQL = "INSERT INTO " + TABLE_NAME + " (login, pass, nick) VALUES ('" + login + "', '" + pass + "', '" + nick + "')";
        return statement.execute(createTableSQL);
    }


    //удаление пользователей
    @Override
    public boolean deleteUser(String login, String pass) throws SQLException {
   /*     Optional<Entry> foundUser = selectUser(login, pass);
        if (!foundUser.isPresent()) {
            insertUser(login, pass, nick);
            System.out.println("User <" + nick + "> added successfully");

            foundUser = selectUser(login, pass);
            if (foundUser.isPresent()) {
                entries.add(foundUser.get());
            } else {
                return false;
            }
        } else {
            System.out.println("User <" + nick + "> is already created");
            return false;
        }
        */
        return true;
    }

    public boolean deleteUserSQL(String login, String pass) throws SQLException {
 /*       Optional<Entry> foundUser = selectUser(login, pass);
        if (!foundUser.isPresent()) {
            insertUser(login, pass, nick);
            System.out.println("User <" + nick + "> added successfully");

            foundUser = selectUser(login, pass);
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

        String createTableSQL = "DELETE FROM chatUsers WHERE login = '" + login + "' AND pass = '" + pass + "'";
        statement.execute(createTableSQL);*/
        return true;
    }

}
