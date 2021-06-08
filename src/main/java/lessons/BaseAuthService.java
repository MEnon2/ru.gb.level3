package lessons;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BaseAuthService implements AuthService {
    @Override
    public void start() {
        System.out.println("Auth start");
    }

    @Override
    public void stop() {
        System.out.println("Auth stop");
    }

    @Override
    public boolean createUser(String login, String pass, String nick) {
        return false;
    }

    @Override
    public boolean deleteUser(String login) {
        return false;
    }

    @Override
    public boolean updateUserInfo(String login, String field, String newValue) {
        return false;
    }

    @Override
    public Optional<String> getUserNick(String login, String pass) {
        if(login.isEmpty() || pass.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream().filter(e -> e.login.equals(login)).filter(e -> e.pass.equals(pass)).map(e -> e.nick).findFirst();
    }

    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
    }

}