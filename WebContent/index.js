/*
index.js
 */

/**
 * Handles the response
 * @param data: data
 */
function handleResponse(data) {
    console.log(JSON.parse(JSON.stringify(data)));
}


/* Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse.
*/
jQuery.ajax({
    // Set the return type to JSON.
    dataType: "json",
    // Set the request method to GET.
    method: "GET",
    // Set the request url, which is mapped in the Java servlet.
    url: "indexServlet",
    // If successful, call the handleResponse function, and pass as a parameter the data returned by the call.
    success: (resultData) => handleResponse(resultData)
});