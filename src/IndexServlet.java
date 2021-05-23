import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

@WebServlet(name="IndexServlet", urlPatterns="/indexServlet")
public class IndexServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Create a PrintWriter to write the response back to the front-end
		PrintWriter out = response.getWriter();

		// Set the response type to JSON
		response.setContentType("application/json");

		try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("test", "urmom");

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
