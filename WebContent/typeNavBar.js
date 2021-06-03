var nav = $('#content-type');
var nav2 = $('#time-range');
var line = $('<div />').addClass('line');
var line2 = $('<div />').addClass('line-time');

line.appendTo(nav);
line2.appendTo(nav2);

var active = nav.find('.active');
var active2= nav2.find('.active');
var pos = 0;
var pos2 = 0;
var wid = 0;
var wid2 = 0;

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

nav.find('ul li a').click(function(e) {
    e.preventDefault();
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
    }
});

nav2.find('ul li a').click(function(e) {
    e.preventDefault();
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
    }
});