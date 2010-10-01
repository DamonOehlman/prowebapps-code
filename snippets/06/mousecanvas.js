(function() {
    var canvas = null,
        context = null,
        buttonDown = 0;
    
    function resetCanvas() {
        canvas = document.getElementById("simple");

        // set the canvas height to the window height and width
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        // get a reference to our drawing context
        context = canvas.getContext("2d");
    } // resetContext
    
    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();
        
        document.body.addEventListener("mousedown", function(evt) {
            if (buttonDown === 0) {
                context.moveTo(evt.pageX, evt.pageY);
            } // if

            ++buttonDown;
        }, false);
        
        document.body.addEventListener("mousemove", function(evt) {
            if (buttonDown > 0) {
                context.lineTo(evt.pageX, evt.pageY);
                context.stroke();
            } // if
        }, false);
        
        document.body.addEventListener("mouseup", function(evt) {
            --buttonDown;
        }, false);
    });
})();
