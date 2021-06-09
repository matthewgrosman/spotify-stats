/**
 * This is the JavaScript file that displays the user's top artists, songs and albums. This
 * is done through making a jQuery.ajax call to the Java servlet in the backend of the
 * program, parsing the returned data, and then using jQuery to grab the HTML elements and
 * append to them the appropriate data.
 */

/**
 * Handles the response from the backend Java servlet.
 *
 * @param data  The returned data from the backend servlet. This is in the form of a jsonObject
 *              which contains the key of "user_type"- this lets us know if we need the site
 *              to redirect the user to the authorization page or if we can display their data.
 */
function handleResponse(data) {
    let json = JSON.parse(JSON.stringify(data));
    displayResults(json);
}

/**
 * Displays the users top artists and tracks.
 *
 * @param json  The top tracks and artists data returned from the backend, formatted
 *              in JSON.
 */
function displayResults(json) {
    let top_results_div = jQuery("#top-results");
    top_results_div.empty();
    top_results_div.append(buildListItem(json["content-type"], json["list"]));
}


/**
 * Builds a list item to add to the top artists or top tracks list.
 *
 * @param category
 * @param data
 * @returns {string}
 */
function buildListItem(category, data) {
    let list_item = "";

    if (category === "Artists") {
        for (let i = 0; i < data.length; i++)  {
            list_item += "<li>" + data[i]["artist_name"] + "</li>";
        }
    }
    else {
        for (let i = 0; i < data.length; i++) {
            list_item += ("<li>" + data[i]["track_name"] + " by ");

            for (let j = 0; j < data[i]["track_artists"].length; j++) {
                list_item += data[i]["track_artists"][j]["artist"];
                if (j < data[i]["track_artists"].length - 1) {
                    list_item += ", ";
                }
            }
            list_item += "</li>"
        }
    }

    return list_item
}


/*
Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse. Before the call is made, we check to see if we have an access token
in the url, which is denoted through the "code" parameter. We pass this through in the url.
*/
let ajaxURL = "statisticsServlet?content-type='Artists'&time-range='Short Term'";

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


function testing() {
    console.log("hi");
}