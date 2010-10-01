(function() {
    var canvas = null,
        context = null,
        angle = 0;
    
    function resetCanvas() {
        canvas = document.getElementById("main");

        // set the canvas height to the window height and width
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        // get a reference to our drawing context
        context = canvas.getContext("2d");
    } // resetContext
    
    function animate() {
        context.save();
        try {
            // clear the drawing surface
            context.clearRect(0, 0, canvas.width, canvas.height);
            
            // set the origin of the context to the center of the canvas
            context.translate(canvas.width * 0.5, canvas.height * 0.5);
            
            // rotate the canvas around the origin (canvas center)
            context.rotate(angle);
            
            // draw a rectangle at the specified position
            context.fillStyle = "#FF0000";
            context.fillRect(-30, -30, 60, 60);
            
            // increment the angle
            angle += 0.05 * Math.PI;
        }
        finally {
            context.restore();
        } // try..finally
    } // animate
    
    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();
        
        setInterval(animate, 40);
    });
})();
