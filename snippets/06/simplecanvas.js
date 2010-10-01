(function() {
    var canvas = null,
        context = null;
    
    function resetCanvas() {
        canvas = document.getElementById("simple");

        // set the canvas height to the window height and width
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        // get a reference to our drawing context
        context = canvas.getContext("2d");
        
        // now draw the line
        drawLine();
    } // resetContext
    
    function drawLine() {
        context.beginPath();
        context.moveTo(0, 0);
        context.lineTo(canvas.width, canvas.height);
        context.stroke();
    } // drawLine

    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();
    });
})();
