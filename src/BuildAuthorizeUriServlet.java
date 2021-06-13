/**
 * This file communicates builds the uri that sends the user to the Spotify
 * authorization page to ask them to allow our website to read their Spotify
 * artists and tracks.
 */

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@WebServlet(name="BuildAuthorizeUriServlet", urlPatterns="/build-uri")
public class BuildAuthorizeUriServlet extends HttpServlet {
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
        final URI redirectURI = SpotifyHttpManager.makeUri(SpotifyApiConstants.REDIRECT_URI);

        // Create a PrintWriter to write the response back to the front-end
        PrintWriter out = response.getWriter();

        // Set the response type to JSON
        response.setContentType("application/json");

        try {
            // Create a new SpotifyApi object.
            final SpotifyApi api = SpotifyApi.builder()
                    .setClientId(SpotifyApiConstants.CLIENT_ID)
                    .setClientSecret(SpotifyApiConstants.CLIENT_SECRET)
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
            Pass back the uri so we can use it to redirect the user in the frontend
            to the authorization page.
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
