<%-- 
    Document   : intro
    Created on : Nov 5, 2017, 10:02:45 AM
    Author     : gee
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
      <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Inro</title>
      </head>
      <body>
             
            <% 
                boolean firststep = true;
                HashMap sessionManager  = new HashMap<String, HttpSession>();
                Cookie [] cookies = request.getCookies();
                // looping over cookies
                for(int i = 0; i < cookies.length; i++) {   
                    // search for session cooike
                    if(cookies[i].getName().equals("MyCurrentSession")) {  
                        // check if session manager map exists
                        if(application.getAttribute("sessionManager") != null) {    
                            // search for session id in the map
                            sessionManager = (HashMap<String, HttpSession>) application.getAttribute("sessionManager"); 
                            Cookie mycookie = cookies[i];
                            if(sessionManager.containsKey(mycookie.getValue())) {     
                                // display username and number of sessions
                                out.println("Username: " + session.getAttribute("username"));
                                out.println("Number of sessions: " + sessionManager.size());
                                // gotta connect a third freaking time to get the email and phone no. god
                                Class.forName("com.mysql.jdbc.Driver");
                                String url = "jdbc:mysql://localhost:3306/users";
                                String name = "root"; String password = "root";
                                Connection connection = (Connection) DriverManager.getConnection(url, name, password);
                                String query = "SELECT email, phone FROM users WHERE user_id = ?";
                                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(query);
                                preparedStatement.setString(1, mycookie.getValue());
                                ResultSet rs = preparedStatement.executeQuery();
                                while(rs.next()) {
                                        out.print("Email: " + rs.getString("email")); 
                                        out.print(" Phone Number: " + rs.getInt("phone"));                                
                                }
                                rs.close();
                                preparedStatement.close();
                                connection.close();
                                
                                firststep = false;
                                %>
                                <form action="logout"> 
                                        <input type="submit" value="logout">
                                </form> 
                                <%
                            } else {   // session id not found
                                    cookies[i].setValue(null);
                                    cookies[i].setMaxAge(0);
                                    firststep = true; }

                        } else {   // session manager obj not found
                                    cookies[i].setValue(null);
                                    cookies[i].setMaxAge(0);
                                    firststep = true; }

                       break;   // found the cookie so no need to search further
                    } else {
                            firststep = true; }
                }
                // if its the first time logging in
                if (firststep) {
                     sessionManager = (HashMap<String, HttpSession>)application.getAttribute("sessionManager");
                     if (sessionManager == null) {
                            sessionManager = new HashMap<String, HttpSession>();  
                            application.setAttribute("sessionManager", sessionManager);
                            out.println("hey"); 
                    }
                     %>
                            <form action="store_session">
                                Username: <input type="text" name="username">
                                Email: <input type="text" name="email">
                                Phone Number: <input type="text" name="phone" >
                                <input type="submit">
                            </form>
                    <% 
                }
            %>
            
      </body>
</html>
