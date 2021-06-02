import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowed = new ArrayList<>();

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
            httpResponse.sendRedirect("login.html");
        }
        else {
            chain.doFilter(request, response);
        }
    }

    private boolean checkUrl(String currentUrl) {
        return allowed.stream().anyMatch(currentUrl.toLowerCase()::endsWith);
    }

    public void destroy() {
    }

}