package controllers;

import java.io.IOException;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.BeanLogin;
import utils.DAO;

@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchController() { 
    	super(); 
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String searchUser = (String) request.getParameter("content");
		if (searchUser == null) return;
		
		HttpSession session = request.getSession();
		BeanLogin userlogin = (BeanLogin) session.getAttribute("user");
		//if (userlogin == null) return;
			
		System.out.println("SearchController, loading: " + searchUser);
		
		DAO dao;
		try {
			dao = new DAO();
			
			String query1 = "";
			String query2 = "";
			
			if(userlogin == null){
				query1 = "SELECT COUNT(U.Username) AS num FROM Users U WHERE U.Username LIKE '%" + searchUser + "%';";
				query2 = "SELECT U.Username FROM Users U WHERE U.Username LIKE '%" + searchUser + "%';";
			}else{
				query1 = "SELECT COUNT(U.Username) AS num FROM Users U WHERE U.Username LIKE '%" + searchUser + "%' AND U.Username != '" + userlogin.getUser() + "';";
				query2 = "SELECT U.Username FROM Users U WHERE U.Username LIKE '%" + searchUser + "%' AND U.Username != '" + userlogin.getUser() + "';";
			}
			
			//get the number of occurrences
			ResultSet checkQuery1 = dao.executeSQL(query1);
			checkQuery1.first();
			int numResults = checkQuery1.getInt("num");
			
			String[] userList = new String[numResults];
			int i = 0;
			
			//get the usernames
			ResultSet checkQuery2 = dao.executeSQL(query2);
			
			//save the usernames
			while(checkQuery2.next()) {
				userList[i] = checkQuery2.getString(1);
				i++;
			}
			
			//for(String j : userList) System.out.println(j);
			
			//username and similars found
			if(userList.length != 0) 
				request.setAttribute("userList", userList);
			
			//username not found
			else request.setAttribute("searchUser", "notfound");

			//send the information
			RequestDispatcher dispatcher = request.getRequestDispatcher("ViewSearch.jsp");
			dispatcher.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
