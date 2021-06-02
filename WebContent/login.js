/**
 * Redirect the user to the authorization request page.
 *
 * @param data  he returned data from the backend servlet. This is in the form of a jsonObject
 *              which contains the key of "user_type"- this lets us know if we need the site
 *              to redirect the user to the authorization page or if we can display their data.
 */
function redirectUser(data) {
    let json = JSON.parse(JSON.stringify(data));
    location.href = json["uri"];
}

/**
 * Handles the button click on the login page.
 */
function authorizeUser() {
    $.ajax(
        "build-uri", {
            method: "POST",
            success: redirectUser
        }
    );
}
