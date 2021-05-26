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
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.personalization.GetUsersTopArtistsAndTracksRequest;
import com.wrapper.spotify.requests.data.personalization.interfaces.IArtistTrackModelObject;

@WebServlet(name="StatisticsServlet", urlPatterns="/statisticsServlet")
public class StatisticsServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String clientId = "649c589d7ba94443ae40c6bc10025966";
		final String clientSecret = "48c7050d60a646f89663acfd14e208b6";
		final URI redirectURI = SpotifyHttpManager.makeUri( "http://localhost:8080/spotify-stats/");

		String test = "";

		// Create a PrintWriter to write the response back to the front-end
		PrintWriter out = response.getWriter();

		// Set the response type to JSON
		response.setContentType("application/json");
		String accessToken = request.getParameter("code");

		try {
			JsonObject responseJsonObject = new JsonObject();

			final SpotifyApi api = SpotifyApi.builder()
					.setClientId(clientId)
					.setClientSecret(clientSecret)
					.setRedirectUri(redirectURI)
					.build();

			if (accessToken == null || accessToken.equals("")) {
				final AuthorizationCodeUriRequest authorizationCodeUriRequest = api.authorizationCodeUri()
						.state("x4xkmn9pu3j6ukrs8n")
						.scope("user-library-read")
						.show_dialog(true)
						.build();

				final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();
				final URI uri = uriFuture.join();

				responseJsonObject.addProperty("user_type", "new");
				responseJsonObject.addProperty("uri", uri.toString());
				responseJsonObject.addProperty("code", accessToken);
			}
			else {
				final AuthorizationCodeRequest authorizationCodeRequest = api.authorizationCode(accessToken).build();
				final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();
				final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

				// Set access and refresh token for further "spotifyApi" object usage
				api.setAccessToken(authorizationCodeCredentials.getAccessToken());
				api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

				final ModelObjectType type = ModelObjectType.ARTIST;

				final GetUsersTopArtistsAndTracksRequest<? extends IArtistTrackModelObject> getUsersTopArtistsAndTracksRequest = api
						.getUsersTopArtistsAndTracks(type)
						.build();

				final CompletableFuture<? extends Paging<? extends IArtistTrackModelObject>> pagingFuture = getUsersTopArtistsAndTracksRequest.executeAsync();
				final Paging<? extends IArtistTrackModelObject> artistPaging = pagingFuture.join();

				responseJsonObject.addProperty("user_type", "returning");
				responseJsonObject.addProperty("top", artistPaging.getTotal());
				responseJsonObject.addProperty("code", authorizationCodeCredentials.getAccessToken());
			}


			out.write(responseJsonObject.toString());
		}
		catch (Exception e) {
			// Write an error message
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessageUpdated2", e.getMessage());
			out.write(jsonObject.toString());

			// Set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
