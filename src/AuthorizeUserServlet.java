/**
 * This file uses the code sent from the authorization redirect callback to generate
 * access and refresh tokens so that we can use the Spotify API. Additionally, we
 * update our session to let the website know the user is authorized and logged in.
 */

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import com.google.gson.JsonObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

@WebServlet(name="AuthorizeUserServlet", urlPatterns="/authorize-user")
public class AuthorizeUserServlet extends HttpServlet {
    /**
     * Responds to a GET request made by the frontend. This method add an attribute to the
     * session to let the website know that a user is authorized and logged in. This method
     * will also generate access and refresh tokens for the Spotify API, and then store
     * the SpotifyApi object in the session for later use.
     *
     * @param request	an HttpServletRequest that contains all the information about the request.
     * @param response	an HttpServletResponse that we use to write back to the frontend.
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final URI redirectURI = SpotifyHttpManager.makeUri( SpotifyApiConstants.REDIRECT_URI);

        /*
		Get the code parameter, which is only in the url if the user has granted  us access to their
		Spotify account. This code parameter is used to generate an access token.
		 */
        String code = request.getParameter("code");

        try {
            // Create a new SpotifyApi object.
            final SpotifyApi api = SpotifyApi.builder()
                    .setClientId(SpotifyApiConstants.CLIENT_ID)
                    .setClientSecret(SpotifyApiConstants.CLIENT_SECRET)
                    .setRedirectUri(redirectURI)
                    .build();

            // Use the code parameter to get an authorization and refresh token.
            final AuthorizationCodeRequest authorizationCodeRequest = api.authorizationCode(code).build();
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for the SpotifyApi object
            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            /*
            Get the current session, we are going to use this to hold the SpotifyApi object for
            our other servlets to easily use it to make requests to the API. We are also going to
            add in something that lets our website know a user has logged in, this will let our
            LoginFilter servlet know the user can access pages only meant for logged in users.
             */
            HttpSession session = request.getSession();
            session.setAttribute("user_status", "Authorized");
            session.setAttribute("api_object", api);

            // Write something to the front-end so the AJAX query registers as success.
            PrintWriter out = response.getWriter();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "good");
            out.write(jsonObject.toString());
        }
        catch (Exception e) {
            // Create a PrintWriter to write the response back to the front-end
            PrintWriter out = response.getWriter();

            // Write the error message.
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Error", e.getMessage() + redirectURI);
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
