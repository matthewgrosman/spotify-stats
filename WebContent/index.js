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
    // Display the user's name
    let greet_user = jQuery("#user-greet");
    greet_user.empty();
    greet_user.append(json["user-name"] + "'s Stats");

    // Build the statistics list
    let top_results_div = jQuery("#top-results");
    top_results_div.empty();
    top_results_div.append(buildListItem(json["content-type"], json["list"]));

    if (json.hasOwnProperty("images_list")) {
        buildImageDivs(json)
    }
}

/**
 * Builds a list item to add to the top artists or top tracks list.
 *
 * @param category      The category of data the user is requesting (either Artists or Tracks)
 * @param data          The returned data from the backend (either the top tracks or artists)
 * @returns {string}    A string that contains HTML code of a fully built list.
 */
function buildListItem(category, data) {
    let list_item = "";

    if (category === "Artists") {
        for (let i = 0; i < data.length; i++)  {
            list_item += "<li class='artist'><img src='" + data[i]["artist_image"]
                + "' alt='Artist Image'>" + data[i]["artist_name"] + "</li>";
        }
    }
    else {
        for (let i = 0; i < data.length; i++) {
            list_item += ("<li class='track'><img src='" + data[i]["album_image"]
                + "' alt='Artist Image'>" + data[i]["track_name"] + "<br>by ");

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

/**
 * Builds the div that displays the floating background images on the front-end
 *
 * @param data  The JSON data that contains the image URLs to be displayed.
 */
function buildImageDivs(data) {
    let leaf = jQuery("#leaf")
    let img_list = data["images_list"];

    for (let i = 0; i < img_list.length; i++) {
        leaf.append(
            "<div class='" + getImageClass(i) + "'><img src='"
            + img_list[i]["image"] + "' " + getImageHeightWidth(i) + "></div>"
        );
    }
}

/**
 * Returns the class name which will determine how the opacity and border-radius for
 * the current image. There are 4 types , so we use modulus 4 on the current index to
 * grab the appropriate one and make sure that the images are varied in their appearance.
 *
 * @param index     The current index in the image list we are iterating through.
 * @returns {*}     The class name which determines the border-radius.
 */
function getImageClass(index) {
    let definitions = {0: "no-rad", 1:"low-rad", 2:"mid-rad", 3:"big-rad"};
    return definitions[index % 4];
}

/**
 * Returns the height and width of the image. There are 5 possible sizes, so
 * we use modulus 5 on the current index to grab the appropriate one and make sure
 * that the images are varied in their selection of size.
 *
 * @param index     The current index in the image list we are iterating through.
 * @returns {*}     The height and width of the image.
 */
function getImageHeightWidth(index) {
    let definitions = {
        0: "height=150px width=150px",
        1: "height=175px width=175px",
        2: "height=200px width=200px",
        3: "height=220px width=220px",
        4: "height=160px width=160px"};
    return definitions[index % 5];
}

/*
Makes an HTTP request to the backend Java servlet. Upon success, the returned JSON data is sent
to the function handleResponse. We supply the parameters Artists and Short Term as these are the
default selections when the website is first visited by the user, so we sent this request to display
the results.
*/
let ajaxURL = "statisticsServlet?content-type=Artists&time-range=Short Term&get-images=needed";

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