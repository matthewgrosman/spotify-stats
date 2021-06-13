/**
 * This file checks if the current url that the user is trying to access
 * is okay for the user to access, and takes appropriate action depending
 * on the answer to that question.
 */

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    // This holds the list of allowed URIs
    private final ArrayList<String> allowed = new ArrayList<>();

    /**
     * The init function adds all of the allowed URIs to the allowed ArrayList
     *
     * @param fConfig   A FilterConfig object
     */
    public void init(FilterConfig fConfig) {
        // Add urls that are allowed to be accessed without being logged in.

        // Login pages
        allowed.add("login.html");
        allowed.add("login.js");
        allowed.add("login.css");

        // Verification pages
        allowed.add("build-uri");
        allowed.add("authorize-user");
        allowed.add("verify.html");
        allowed.add("verify.js");
    }

    /**
     * This function sees if the URI trying to be accessed is allowed to be accessed by the user.
     * If it is not allowed to be accessed, the user is redirected to login.html.
     *
     *
     * @param request       The request that contains the URI the user is trying to access
     *                      as well as the current session, which tells us if a user is
     *                      logged in or not.
     * @param response      The response that we will use to redirect the user if they are
     *                      attempting to access a page they are not allowed to access.
     * @param chain         A FilterChain chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Cast the request and response to their Http variants.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if the url accessed is allowed to be reached without logging in.
        if (this.checkUrl(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // If this url needs a logged-in user to be accessed, check if user is logged in.
        if (httpRequest.getSession().getAttribute("user_status") == null) {
            httpResponse.sendRedirect("https://spotifstats.com/login.html");
        }
        else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Checks to see if the URI is in the allowed ArrayList.
     *
     * @param currentUrl    The current URL the user is trying to access.
     * @return
     */
    private boolean checkUrl(String currentUrl) {
        return allowed.stream().anyMatch(currentUrl.toLowerCase()::endsWith);
    }

    /**
     * Destroys.
     */
    public void destroy() {
    }

}