/**
 * This is the JavaScript file that sends a request to a Java Servlet to authorize
 * the user to use the website, as well as create a SpotifyApi object that will be
 * used to grab the user's top artists and tracks.
 */

/**
 * Given a parameter name, this function returns the value of that parameter in the URL.
 * For example, the url "https://www.somewebsite.com/?param1=hello", calling
 * getParameterByName("param1") would return "hello". When called with a parameter that
 * does not exist, this function returns null.
 *
 * @param target    a String representing the parameter whose value we want
 * @returns {string|null}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the response from the AJAX call to the Java Servlet.
 */
function handleResponse(data) {
    // Send the user to index.html, which is the page that displays results
    window.location.href = "index.html"
}

/*
Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse. Before the call is made, we check to see if we have an access token
in the url, which is denoted through the "code" parameter. We pass this through in the url.
*/
let ajaxURL = "authorize-user";
let code = getParameterByName("code");

if (code != null) {
    ajaxURL += "?code=" + code;
}

jQuery.ajax({
    // Set the return type to JSON.
    dataType: "json",
    // Set the request method to GET.
    method: "GET",
    // Set the request url, which is mapped in the Java servlet.
    url: ajaxURL,
    // If successful, call the handleResponse function, and pass as a parameter the data returned by the call.
    success: (resultData) => handleResponse(resultData)
});