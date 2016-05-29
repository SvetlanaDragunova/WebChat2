
/**
 * Created by Svetlana on 30.05.2016.
 */


    import org.json.simple.JSONObject;

    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import javax.servlet.http.HttpSession;
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.sql.*;
    import java.text.SimpleDateFormat;


public class ChatServlet extends javax.servlet.http.HttpServlet {


        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            JSONObject answer = new JSONObject();

            HttpSession session = req.getSession();
            session.setAttribute("authorize",false);

            if (req.getParameter("action").equals("auth")) {
                if (req.getParameter("login") != null && !req.getParameter("login").equals("") && req.getParameter("password") != null && !req.getParameter("password").equals("")) {
                    String login = req.getParameter("login");
                    String password = req.getParameter("password");

                    User user = null;
                    Connection conn = null;
                    Statement statement = null;

                    try {
                        conn = connect();
                        statement = conn.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT * FROM CHAT_USER WHERE LOGIN = '" + login + "' AND PASSWORD = '" + password + "'");

                        if (rs.next()) {
                            //got this user - good
                            user = new User();
                            user.setId(rs.getInt("ID"));
                            user.setLogin(rs.getString("LOGIN"));
                            user.setPassword(rs.getString("PASSWORD"));
                            user.setDate_online(new Date(System.currentTimeMillis()));
                            rs = statement.executeQuery("UPDATE CHAT_USER SET STATUS = 'online' WHERE LOGIN = '" + login + "' AND PASSWORD = '" + password + "'");
                        } else {
                            //no such user - bad
                            answer.put("auth","bad");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (user != null) {
                        session.setAttribute("authorize", true);
                        session.setAttribute("user", user);
                        answer.put("auth","success");
                    }

                } else {
                    answer.put("auth","no data");
                }
            }

            if (req.getParameter("action").equals("reg")) {
                if (req.getParameter("login") != null && !req.getParameter("login").equals("") && req.getParameter("password") != null && !req.getParameter("password").equals("")) {
                    String login = req.getParameter("login");
                    String password = req.getParameter("password");

                    User user = null;
                    Connection conn = null;
                    Statement statement = null;

                    try {
                        conn = connect();
                        statement = conn.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT * FROM CHAT_USER WHERE LOGIN = '" + login + "'");

                        if (rs.next()) {
                            //got this user - bad
                            answer.put("reg","no");
                        } else {
                            //no such user - good
                            user = new User();
                            user.setId(rs.getInt("ID"));
                            user.setLogin(rs.getString("LOGIN"));
                            user.setPassword(rs.getString("PASSWORD"));
                            user.setDate_online(new Date(System.currentTimeMillis()));

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                    if (user != null) {
                        statement = conn.createStatement();
                        ResultSet rs = statement.executeQuery("INSERT INTO CHAT_USER (id,login,password, status) VALUES (chat_seq_us.nextval,'" + login + "','" + password + "', 'online')");
                        answer.put("reg","success");
                        session.setAttribute("authorize", true);
                        session.setAttribute("user", user);
                        answer.put("reg","success");
                    }

                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    answer.put("reg","no");
                }
            }

            out.println(answer);
            out.close();
        }

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

            // Отправляем ответ клиенту в формате JSON
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject answer = new JSONObject();

            Connection conn = null;
            Statement statement;
            ResultSet result;

            if (request.getParameter("action").equals("userlist")) {
                try {
                    conn = connect();
                    statement = conn.createStatement();
                    result = statement.executeQuery("SELECT * FROM CHAT_USER WHERE STATUS = 'online'");
                    while (result.next()) {
                        answer.put(result.getString("ID"), result.getString("LOGIN"));
                    }
                } catch  (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (request.getParameter("action").equals("messagelist")) {
                    HttpSession session = request.getSession();
                    User user = (User) session.getAttribute("user");
                    Date last = (Date) user.getDate_online();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String prev = format.format(last);
                    String now = format.format(new Date(System.currentTimeMillis()));

                    try {
                        conn = connect();
                        statement = conn.createStatement();
                        result = statement.executeQuery("SELECT * FROM CHAT_MESSAGE WHERE SEND_DATE BETWEEN TO_DATE('" + prev + "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('" + now + "','YYYY-MM-DD HH24:MI:SS')");

                        while (result.next()) {
                            String ans = "";

                            ResultSet res = statement.executeQuery("SELECT * FROM CHAT_USER WHERE ID = "+result.getString("sender_id"));
                            if(res.next()){
                                ans+=res.getString("LOGIN");
                            }
                            ans+="@";
                            ans+=result.getString("MESSAGE");
                            answer.put(result.getString("ID"), ans);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

            }

            if (request.getParameter("action").equals("isauth")) {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                if (user == null) {
                    answer.put("auth","no");
                } else {
                    answer.put("auth","yes");
                    answer.put("name",user.getLogin());
                }
            }

            if (request.getParameter("action").equals("send")) {
                HttpSession session = request.getSession();
                    User user = (User) session.getAttribute("user");
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //outputDateFormat.format(lastdate);

                    try {
                        conn = connect();
                        statement = conn.createStatement();
                        String message = request.getParameter("message");
                        String now = outputDateFormat.format(new Date(System.currentTimeMillis()));

                        statement.executeQuery("INSERT INTO CHAT_MESSAGE (id,text,sender_id,send_date) VALUES (chat_seq_mes.nextval,'"+message+"'," + user.getId() + ", TO_DATE('" + now + "','YYYY-MM-DD HH24:MI:SS'))");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }


            out.println(answer);
            out.close();
        }

        private Connection connect() {
            String DB_CONNECTION = "jdbc:oracle:thin:@students.dce.ifmo.ru:1521/xe";
            String DB_USER = "s191690";
            String DB_PASSWORD = "umowg2";
            Connection conn = null;
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER,DB_PASSWORD);
                return conn;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return conn;
        }


    }
