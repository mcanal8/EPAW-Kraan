package controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.BeanLogin;
import models.BeanTweet;
import utils.DAO;

/**
 * Servlet implementation class MainController
 */
@WebServlet("/GetTweetsController")
public class GetTweetsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTweetsController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void fillBeanTweetsArray(BeanTweet[] tweets, ResultSet tweet_rows) {
    	
		try {
	    	// iterate through ResultSets rows
			int current = 0;
	    	while (tweet_rows.next()) {
	    		// Fill current BeanTweet object
	    		BeanTweet tweet = new BeanTweet();
	    		tweet.setId(tweet_rows.getInt("Tweet_Id"));
	    		tweet.setAuthor(tweet_rows.getString("Author"));
	    		tweet.setContent(tweet_rows.getString("Content"));
	    		tweet.setDate(tweet_rows.getDate("Date"));
	    		tweet.setLikes(tweet_rows.getInt("Likes"));
	    		tweets[current]= tweet;
	    		current++;
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			DAO dao = new DAO();
			
			System.out.println("GetTweetsController");
			String tweetsToDisplay = (String) request.getParameter("Display");
			 
			//if the user is the owner
			if(tweetsToDisplay.equals("Yes")){
				HttpSession session = request.getSession();
				BeanLogin user = (BeanLogin) session.getAttribute("user");
				tweetsToDisplay = user.getUser();
			}
			System.out.println(tweetsToDisplay);
			
			// Generate Query to get Tweets from DB
			int number_of_tweets = 0;
			String getTweetsQuery = "SELECT * FROM Tweet;";
			
			String countTweets = "SELECT COUNT(*) as num FROM Tweet;";
			ResultSet rs = dao.executeSQL(countTweets);
			rs.first();
			number_of_tweets = rs.getInt("num");
			
			if(number_of_tweets != 0){
				ResultSet tweet_rows = dao.executeSQL(getTweetsQuery);
				
				// Fill Beantweet[]
				BeanTweet[] tweets = new BeanTweet[number_of_tweets];
				fillBeanTweetsArray(tweets,tweet_rows);
				
				// If tweets have interests get them
				String interestsQuery = "SELECT Interest FROM tweet_has_interests WHERE Tweet_Id =";
				
				for (int i = 0; i < tweets.length; i++){
					
					// complete query
					interestsQuery += tweets[i].getId() + ";";
					ResultSet interest_rows = dao.executeSQL(interestsQuery);
					
					// Restart string query
					interestsQuery = "SELECT Interest FROM tweet_has_interests WHERE Tweet_Id =";
					
					// if tweet has interests
					ArrayList<String> interest_array = new ArrayList<String>();
					interest_rows.beforeFirst();
					while(interest_rows.next()){
						String interest = interest_rows.getString("Interest");
						interest_array.add(interest);
					}
					
					if(!interest_array.isEmpty()){
						String[] interests = new String[interest_array.size()];
						interests = interest_array.toArray(interests);
						// update tweet
						tweets[i].setInterests(interests);
					}
				}
				
				//pass tweets to request
				request.setAttribute("tweets", tweets);
			}
			
			request.setAttribute("display", tweetsToDisplay);
			RequestDispatcher dispatcher = request.getRequestDispatcher("ViewTweets.jsp");
			dispatcher.forward(request, response);	
			
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