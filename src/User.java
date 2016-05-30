import java.util.Date;

/**
 * Created by Svetlana on 30.05.2016.
 */
public class User {

    private int id;
    private String login;
    private String password;
    private String status;
    private Date date_online;

    public User( int id, String login, String password, String status, Date date_online) {
        this.login = login;
        this.id = id;
        this.password = password;
        this.status = status;
        this.date_online = date_online;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate_online() {
        return date_online;
    }

    public void setDate_online(Date date_online) {
        this.date_online = date_online;
    }
}
