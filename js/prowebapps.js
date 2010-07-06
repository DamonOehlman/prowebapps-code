PROWEBAPPS = (function() {
    var ROTATION_CLASSES = {
        "0": "none",
        "90": "right",
        "-90": "left",
        "180": "flipped"
    };
    
    function monitorOrientationChanges() {
        var canDetect = "onorientationchange" in window;
        var orientationTimer = 0; 
        
        $(window).bind(canDetect ? "orientationchange" : "resize", function(evt) {
            clearTimeout(orientationTimer);
            orientationTimer = setTimeout(function() {
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

                $(window).trigger("reorient", [orientation, rotationText]);
            }, 500);
        });        
    } // monitorOrientationChanges
    
    var module = {
    };
    
    monitorOrientationChanges();
    
    // set autoscrolling to hide the URL bar
    $(document).ready(function() {
        window.scrollTo(0, 1);
    });
    
    return module;
})();

jQuery.fn.loadSnippet = function(params) {
    // initialise default parameters
    params = jQuery.extend({
        
    }, params);
    
    // if the html element has an id, then that is the snippet we are to load
    this.each(function() {
        var element = this;
        if (element.id) {
            jQuery.ajax({
                url: "snippets/" + element.id + ".html",
                dataType: "html",
                success: function(data, textStatus, rawRequest) {
                    jQuery(element).html(data);
                },

                error: function(rawRequest, textStatus, errorThrow) {
                    jQuery(element).html("Could not load snippet...");
                }
            });
        } // if
    }); // each
}; // loadSnippet

jQuery(document).ready(function() {
    // load snippets for any objects with the class of snippet
    jQuery(".snippet").loadSnippet();
});