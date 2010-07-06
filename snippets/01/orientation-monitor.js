$(document).ready(function() {
    var canDetect = "onorientationchange" in window;
    var orientationTimer = 0;
    
    var ROTATION_CLASSES = {
        "0": "none",
        "90": "right",
        "-90": "left",
        "180": "flipped"
    };
    
    $(window).bind(canDetect ? "orientationchange" : "resize", function(evt) {
        clearTimeout(orientationTimer);
        orientationTimer = setTimeout(function() {
            // display the event type and window details
            $("#event-type").html(evt.type);
            $("#window-orientation").html(window.orientation);
            $("#window-width").html(window.innerWidth);
            $("#window-height").html(window.innerHeight);

            // given we can only really rely on width and height at this stage, 
            // calculate the orientation based on aspect ratio
            var aspectRatio = 1;
            if (window.innerHeight !== 0) {
                aspectRatio = window.innerWidth / window.innerHeight;
            } // if
            
            // determine the orientation based on aspect ratio
            var orientation = aspectRatio <= 1 ? "portrait" : "landscape";
            
            // if the event type is an orientation change event, we can rely on
            // the orientation angle
            var rotationText = null;
            if (evt.type == "orientationchange") {
                rotationText = ROTATION_CLASSES[window.orientation.toString()];
            } // if
            
            // display the details we have determine from the display
            $("#orientation").html(orientation);
            $("#rotation-class").html(rotationText);
        }, 500);
    });
});