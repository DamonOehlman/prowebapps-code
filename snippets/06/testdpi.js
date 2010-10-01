(function() {
    var canvas = null,
        context = null;
    
    function resetCanvas() {
        canvas = document.getElementById("main");

        // set the canvas height to the window height and width
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        
        // get a reference to our drawing context
        context = canvas.getContext("2d");
        
        context.strokeRect(10, 10, canvas.width - 20, canvas.height - 20);
    } // resetContext
    
    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();
        
        document.body.addEventListener("touchstart", function(evt) {
            context.beginPath();
            context.arc(evt.touches[0].pageX, evt.touches[0].pageY, 5, 0, Math.PI * 2, false);
            context.fill();
            
            // prevent screen scrolling
            evt.preventDefault();
        }, false);
    });
})();
