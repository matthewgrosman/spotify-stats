/**
 * This file handles the clicking of any of the navigation bars to display
 * different statistics. This includes moving/animating the underline on
 * the selected item, as well as sending a request to the backend to fetch
 * new data when the user has selected to view something new.
 */

/**
 * Sends a request to the backend server to get the data for selected
 * time range and content type.
 *
 * @param content_type  String representing the type of content the user
 *                      wants. This can either be "Artists" or "Tracks".
 * @param time_range    String representing the time range of results the
 *                      user wants. This can either be "Short Term",
 *                      "Medium Term" or "Long Term".
 */
function sendRequest(content_type, time_range) {
    let url = "statisticsServlet?content-type=" + content_type
        + "&time-range=" + time_range;

    $.ajax(
        url, {
            method: "POST",
            success: displayResults
        }
    );
}

/*
All of the following code handles clicking one of the items in the navigation
bar for either the content type or time range sections.
 */

// Get both of the nav sections and append line and line-time classes, respectively.
var nav = $('#content-type');
var nav2 = $('#time-range');
var line = $('<div />').addClass('line');
var line2 = $('<div />').addClass('line-time');
line.appendTo(nav);
line2.appendTo(nav2);

// Find the active selection on both divs
var active = nav.find('.active');
var active2= nav2.find('.active');

/*
 Set the starting position and width of the line that is underneath the selected item in
 each div to start at 0.
 */
var pos = 0;
var pos2 = 0;
var wid = 0;
var wid2 = 0;


// Update the position and width of the underline to be where the selected list item is.
if(active.length) {
    pos = active.position().left;
    wid = active.width();
    line.css({
        left: pos,
        width: wid
    });
}
if(active2.length) {
    pos2 = active2.position().left;
    wid2 = active2.width();
    line2.css({
        left: pos2,
        width: wid2
    });
}

/*
This is the click function that handles when a user clicks an item in the
content-type div.
 */
nav.find('ul li a').click(function(e) {
    e.preventDefault();

    // This updates the current active selection and animates the underline to move.
    if(!$(this).parent().hasClass('active') && !nav.hasClass('animate')) {

        nav.addClass('animate');

        var _this = $(this);

        nav.find('ul li').removeClass('active');

        var position = _this.parent().position();
        var width = _this.parent().width();

        if(position.left >= pos) {
            line.animate({
                width: ((position.left - pos) + width)
            }, 300, function() {
                line.animate({
                    width: width,
                    left: position.left
                }, 150, function() {
                    nav.removeClass('animate');
                });
                _this.parent().addClass('active');
            });
        } else {
            line.animate({
                left: position.left,
                width: ((pos - position.left) + wid)
            }, 300, function() {
                line.animate({
                    width: width
                }, 150, function() {
                    nav.removeClass('animate');
                });
                _this.parent().addClass('active');
            });
        }
        pos = position.left;
        wid = width;

        // Get the current selected content and time range.
        let active_time = document.getElementsByClassName("active")[0];
        let regex = new RegExp('>(.+)<');
        let active_time_parsed = regex.exec(active_time.innerHTML)[1];
        let active_content = _this[0].innerHTML;

        // Send a request to the backend to update the content list
        sendRequest(active_content, active_time_parsed);
    }
});

/*
This is the click function that handles when a user clicks an item in the
content-type div.
 */
nav2.find('ul li a').click(function(e) {
    e.preventDefault();

    // This updates the current active selection and animates the underline to move.
    if(!$(this).parent().hasClass('active') && !nav2.hasClass('animate')) {

        nav2.addClass('animate');

        var _this = $(this);

        nav2.find('ul li').removeClass('active');

        var position = _this.parent().position();
        var width = _this.parent().width();

        if(position.left >= pos2) {
            line2.animate({
                width: ((position.left - pos2) + width)
            }, 300, function() {
                line2.animate({
                    width: width,
                    left: position.left
                }, 150, function() {
                    nav2.removeClass('animate');
                });
                _this.parent().addClass('active');
            });
        } else {
            line2.animate({
                left: position.left,
                width: ((pos2 - position.left) + wid2)
            }, 300, function() {
                line2.animate({
                    width: width
                }, 150, function() {
                    nav2.removeClass('animate');
                });
                _this.parent().addClass('active');
            });
        }
        pos2 = position.left;
        wid2 = width;

        // Get the current selected content and time range.
        let active_content = document.getElementsByClassName("active")[0];
        let regex = new RegExp('>(.+)<');
        let active_content_parsed = regex.exec(active_content.innerHTML)[1];
        let active_time = _this[0].innerHTML;

        // Send a request to the backend to update the content list
        sendRequest(active_content_parsed, active_time);
    }
});