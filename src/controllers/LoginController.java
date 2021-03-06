package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import models.BeanLogin;
import utils.DAO;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("LoginController.");
		
		BeanLogin login = new BeanLogin();
	    try {
			
	    	BeanUtils.populate(login, request.getParameterMap());

	    	if (login.isComplete()) {
	    		
	    		DAO dao = new DAO();
		    	dao.login(login);
		    	
		    	if(login.getError() == 0) {
		    		
		    		HttpSession session = request.getSession();
		    		
		    		if(session.getAttribute("user") == null){
		    			String isadminquery = "SELECT IsAdmin FROM Users WHERE Username='"+ login.getUser() + "';";
		    			ResultSet rs = dao.executeSQL(isadminquery);
		    			if(rs.first()){
		    				boolean isAdmin = rs.getBoolean("IsAdmin");
		    				session.setAttribute("admin", isAdmin);
		    			}
		    			session.setAttribute("user",login);
		    		}
		    		
			    	RequestDispatcher dispatcher = request.getRequestDispatcher("ViewLoginDone.jsp");
				    dispatcher.forward(request, response);
		    	}
		    	/*else{
		    		
				    request.setAttribute("login",login);
				    RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				    dispatcher.forward(request, response);
		    	}*/
		    } 
			else {
			    request.setAttribute("user",login);
			    RequestDispatcher dispatcher = request.getRequestDispatcher("ViewLoginForm.jsp");
			    dispatcher.forward(request, response);
		    	
		    }
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
