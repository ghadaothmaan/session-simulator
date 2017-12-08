/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author gee
 */
@WebServlet(urlPatterns = {"/store_session"})
public class store_session extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
    
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // get username paramter 
            String username = request.getParameter("username");
            // store username in current session and set its duration to 3 mins
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setMaxInactiveInterval(3*60);
            
            // getting session manager 
            HashMap sessionManager = (HashMap<String, HttpSession>) request.getServletContext().getAttribute("sessionManager");
            System.out.println("session manager: " + sessionManager.toString());
            
            // add session into session manager obj from app scope
            sessionManager.put(session.getId(), session);
            // store session id as mcs cookie w/ 3 mins duration
            Cookie mycookie = new Cookie("MyCurrentSession", session.getId());
            mycookie.setMaxAge(3*60);
            
            // TODO: store info in db table
            String email = request.getParameter("email");
            int phone = Integer.parseInt(request.getParameter("phone"));
            
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/users";
            String name = "root"; String password = "root";
            Connection connection = (Connection) DriverManager.getConnection(url, name, password);
            //Statement statement = connection.createStatement();
            String query = "INSERT INTO users  (user_id, email, phone) VALUES (?,?,?)";
            PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(query);
            preparedStatement.setString(1, session.getId());
            preparedStatement.setString(2, email);
            preparedStatement.setInt(3, phone);
            preparedStatement.executeUpdate();
            //System.out.println(session.getId());
            //statement.executeUpdate("INSERT INTO user(user_id, email, phone) VALUES(" + session.getId() +"," + "'" + email + "'" + "," +phone+")");
            //statement.close();
            preparedStatement.close();
            connection.close();
            
            // add cookie 
            response.addCookie(mycookie);
            // redirect to intro.jsp
            response.sendRedirect("intro.jsp");
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(store_session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
