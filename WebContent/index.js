/**
 * This is the JavaScript file that displays the user's top artists, songs and albums. This
 * is done through making a jQuery.ajax call to the Java servlet in the backend of the
 * program, parsing the returned data, and then using jQuery to grab the HTML elements and
 * append to them the appropriate data.
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
 * Handles the response from the backend Java servlet.
 *
 * @param data  The returned data from the backend servlet. This is in the form of a jsonObject
 *              which contains the key of "user_type"- this lets us know if we need the site
 *              to redirect the user to the authorization page or if we can display their data.
 */
function handleResponse(data) {
    let json = JSON.parse(JSON.stringify(data));

    /*
    If the user is not authorized, redirect them to the authorization request page. If they are
    are already authorized, log their top artists as a test (will change this later)
     */
    if (json["user_type"] === "new") {
        location.href = json["uri"];
    }
    else {
        displayResults(json);
    }
}

/**
 * Displays the users top artists and tracks.
 *
 * @param json  The top tracks and artists data returned from the backend, formatted
 *              in JSON.
 */
function displayResults(json) {
    let top_results_div = jQuery("#top-results");

    top_results_div.append(buildOrderedList("artist", "Top Artists (Long Term)", json["long_term_artists"]));
    top_results_div.append(buildOrderedList("artist", "Top Artists (Medium Term)", json["medium_term_artists"]));
    top_results_div.append(buildOrderedList("artist", "Top Artists (Short Term)", json["short_term_artists"]));
    top_results_div.append(buildOrderedList("track", "Top Tracks (Long Term)", json["long_term_tracks"]));
    top_results_div.append(buildOrderedList("track", "Top Tracks (Medium Term)", json["medium_term_tracks"]));
    top_results_div.append(buildOrderedList("track", "Top Tracks (Short Term)", json["short_term_tracks"]));
}


function buildOrderedList(category, list_title, data) {
    let ordered_list = "<p1>" + list_title + "</p1><ol>";

    for(let i = 0; i < data.length; i++) {
        if (category === "artist") {
            ordered_list += "<li>" + data[i]["artist_name"] + "</li>";
        }
        else {
            ordered_list += "<li>" + data[i]["track_name"] + " - ";
            for (let j = 0; j < data[i]["track_artists"].length; j++) {
                ordered_list += data[i]["track_artists"][j]["artist"];
                if (j < data[i]["track_artists"].length - 1) {
                    ordered_list += ", ";
                }
            }
            ordered_list += "</li>";
        }
    }

    ordered_list += "</ol><br><br>";
    return ordered_list
}

/*
Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse. Before the call is made, we check to see if we have an access token
in the url, which is denoted through the "code" parameter. We pass this through in the url.
*/
let ajaxURL = "statisticsServlet";
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