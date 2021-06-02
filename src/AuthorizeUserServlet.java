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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import org.apache.hc.core5.http.ParseException;

@WebServlet(name="AuthorizeUserServlet", urlPatterns="/authorize-user")
public class AuthorizeUserServlet extends HttpServlet {
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
        final String clientId = "649c589d7ba94443ae40c6bc10025966";
        final String clientSecret = "48c7050d60a646f89663acfd14e208b6";
        final URI redirectURI = SpotifyHttpManager.makeUri( "http://localhost:8080/spotify-stats/");

        // Create a PrintWriter to write the response back to the front-end
        PrintWriter out = response.getWriter();

        // Set the response type to JSON
        response.setContentType("application/json");

        try {
            // Create a new SpotifyApi object.
            final SpotifyApi api = SpotifyApi.builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(redirectURI)
                    .build();


            JsonObject responseJsonObject = new JsonObject();

            /*
            Use the SpotifyApi object to create a authorization uri request. We set the scope to
            user-top-read so we can access the user's top artists, songs and albums.
             */
            final AuthorizationCodeUriRequest authorizationCodeUriRequest = api.authorizationCodeUri()
                    .state("x4xkmn9pu3j6ukrs8n")
                    .scope("user-top-read")
                    .show_dialog(true)
                    .build();
            final URI uri = authorizationCodeUriRequest.execute();

            /*
            Add the user type as "new" (since we need their authorization) and pass back the uri
            so we can use it to redirect the user in the frontend to the authorization page.
             */
            responseJsonObject.addProperty("uri", uri.toString());

            // Return the JsonObject to the front end.
            out.write(responseJsonObject.toString());
        }
        catch (Exception e) {
            // Write an error message
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Error", e.getMessage());
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
}
