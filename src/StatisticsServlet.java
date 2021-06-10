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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import org.apache.hc.core5.http.ParseException;

@WebServlet(name="StatisticsServlet", urlPatterns="/statisticsServlet")
public class StatisticsServlet extends HttpServlet {
	/**
	 * Responds to a GET request made by the frontend. This method will either return a URI for
	 * redirection if the user is unauthorized, or will return a user's top artists, songs and
	 * albums if they are authorized.
	 *
	 * @param request	an HttpServletRequest that contains all the information about the request.
	 * @param response	an HttpServletResponse that we use to write back to the frontend.
	 * @throws IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Create a PrintWriter to write the response back to the front-end
		PrintWriter out = response.getWriter();

		// Set the response type to JSON
		response.setContentType("application/json");

		// Get the content-type and time-range parameters
		String content_type = request.getParameter("content-type");
		String time_range = request.getParameter("time-range");

		try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("content-type", content_type);

			// Get the SpotifyApi object that was stored in the session in another servlet.
			HttpSession session = request.getSession();
			final SpotifyApi api = (SpotifyApi) session.getAttribute("api_object");

			// Make the call to either getTopArtists or getTopTracks and add the data to the return object
			if (content_type.equals("Artists")) {
				responseJsonObject.add("list", getTopArtists(formatTimeRange(time_range), api));
			}
			else {
				responseJsonObject.add("list", getTopTracks(formatTimeRange(time_range), api));
			}

			// Return the JsonObject to the front end.
			out.write(responseJsonObject.toString());
		}
		catch (Exception e) {
			// Write an error message
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("error", e.getMessage());
			out.write(jsonObject.toString());

			// Set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}

	/**
	 * Responds to a POST request made by the frontend. This method will either return a URI for
	 * redirection if the user is unauthorized, or will return a user's top artists, songs and
	 * albums if they are authorized.
	 *
	 * @param request	an HttpServletRequest that contains all the information about the request.
	 * @param response	an HttpServletResponse that we use to write back to the frontend.
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

	/**
	 * Given a time range, return the top 50 artists from that time range.
	 *
	 * @param range	A String representing the time range of the query. The time range can either be
	 *              short_term, medium_term, or long_term.
	 * @param api	The current SpotifyApi object we are using for this session.
	 * @return		A JsonArray containing the top 50 artists from the given time range.
	 */
	private JsonArray getTopArtists(String range, SpotifyApi api) throws ParseException, SpotifyWebApiException, IOException {
		GetUsersTopArtistsRequest getUsersTopArtistsRequest = api.getUsersTopArtists()
				.limit(50)
				.time_range(range)
				.build();

		JsonArray artists = new JsonArray();
		final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
		for (Artist a : artistPaging.getItems()) {
			JsonObject artistJsonObject = new JsonObject();
			artistJsonObject.addProperty("artist_name", a.getName());
			artistJsonObject.addProperty("artist_image", a.getImages()[0].getUrl());
			artists.add(artistJsonObject);
		}

		return artists;
	}

	/**
	 * Given a time range, return the top 50 tracks from that time range.
	 *
	 * @param range	A String representing the time range of the query. The time range can either be
	 *              short_term, medium_term, or long_term.
	 * @param api	The current SpotifyApi object we are using for this session.
	 * @return		A JsonArray containing the top 50 tracks from the given time range.
	 */
	private JsonArray getTopTracks(String range, SpotifyApi api) throws ParseException, SpotifyWebApiException, IOException {
		GetUsersTopTracksRequest getUsersTopTracksRequest = api.getUsersTopTracks()
				.limit(50)
				.time_range(range)
				.build();

		JsonArray tracks = new JsonArray();
		final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
		for (Track t : trackPaging.getItems()) {
			JsonObject trackJsonObject = new JsonObject();
			trackJsonObject.addProperty("track_name", t.getName());

			/*
			 Since there may be multiple artists per track, we loop through all of the artists and add
			 them to a JsonArray, which is then added to the JsonObject.
			 */
			JsonArray track_artists = new JsonArray();
			for (ArtistSimplified a : t.getArtists()) {
				JsonObject artistJsonObject = new JsonObject();
				artistJsonObject.addProperty("artist", a.getName());
				track_artists.add(artistJsonObject);
			}
			trackJsonObject.add("track_artists", track_artists);

			trackJsonObject.addProperty("track_album", t.getAlbum().getName());
			tracks.add(trackJsonObject);
		}

		return tracks;
	}

	/**
	 * Given a time range (either 'Short Term', 'Medium Term', or
	 * 'Long Term'), format it into the form *range*_term to allow
	 * for easy use in the SpotifyApi function call later in the code.
	 *
	 * @param time_range	String representing a time range.
	 * @return				A formatted String as specified above.
	 */
	private String formatTimeRange(String time_range) {
		if (time_range.equals("Short Term")) {
			return "short_term";
		}
		else if (time_range.equals("Medium Term")) {
			return "medium_term";
		}
		else {
			return "long_term";
		}
	}
}
