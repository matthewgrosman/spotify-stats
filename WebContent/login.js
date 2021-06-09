/**
 * Redirect the user to the authorization request page.
 *
 * @param data  The returned data from the backend servlet which contains the
 *              redirect uri.
 */
function redirectUser(data) {
    // Parse the returned data and redirect the user to the authorization request url.
    let json = JSON.parse(JSON.stringify(data));
    location.href = json["uri"];
}

/**
 * Handles the button click on the login page. The function makes a request to the
 * backend to generate a redirect URI that asks the user to grant our website access
 * to their Spotify artist and song information.
 */
function authorizeUser() {
    $.ajax(
        "build-uri", {
            method: "POST",
            success: redirectUser
        }
    );
}
