/**
* This is the JavaScript file that displays the user's top artists, songs and albums. This
* is done through making a jQuery.ajax call to the Java servlet in the backend of the
* program, parsing the returned data, and then using jQuery to grab the HTML elements and
* append to them the appropriate data.
 */

/**
 * Handles the response
 * @param data  some json data
 */
function handleResponse(data) {
    console.log(JSON.parse(JSON.stringify(data)));
}


/*
Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse.
*/
jQuery.ajax({
    // Set the return type to JSON.
    dataType: "json",
    // Set the request method to GET.
    method: "GET",
    // Set the request url, which is mapped in the Java servlet.
    url: "statisticsServlet",
    // If successful, call the handleResponse function, and pass as a parameter the data returned by the call.
    success: (resultData) => handleResponse(resultData)
});