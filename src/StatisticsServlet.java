/**
 * This file communicates with the Spotify API to get the user's top artists, songs
 * and albums. After getting the results from the API, that data is formatted and
 * written back to the front-end, where the JavaScript file index.js parses the data
 * and displays it to the user.
 */

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

@WebServlet(name="StatisticsServlet", urlPatterns="/statisticsServlet")
public class StatisticsServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Create a PrintWriter to write the response back to the front-end
		PrintWriter out = response.getWriter();

		// Set the response type to JSON
		response.setContentType("application/json");

		try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("test", "URMOM2");

			out.write(responseJsonObject.toString());
		}
		catch (Exception e) {
			// Write an error message
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// Set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
