(function() {
    var canvas = null,
        context = null,
        drops = [];
    
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

            // set a stroke style
            context.strokeStyle = "rgba(68, 221, 255, 0.5)";
            context.lineWidth = 4;
            
            // iterate through the drops and draw them to the canvas
            var ii = 0;
            while (ii < drops.length) {
                // draw the drop
                context.beginPath();
                context.arc(drops[ii].x, drops[ii].y, drops[ii].size, 0, 2 * Math.PI, false);
                context.stroke();

                // increase the size of the drop
                drops[ii].size += 2;

                // if the drop has exceeded it's max size, then remove it
                if (drops[ii].size > drops[ii].maxSize) {
                    drops.splice(ii, 1);
                }
                // otherwise, onto the next drop
                else {
                    ii++;
                } // if..else
            } // while
        }
        finally {
            context.restore();
        } // try..finally
    } // animate
    
    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();
        
        document.body.addEventListener("touchstart", function(evt) {
            // add the new drop
            drops.push({
                size: 2,
                maxSize: 20 + (Math.random() * 50), 
                x: evt.touches[0].pageX,
                y: evt.touches[0].pageY
            });

            // prevent screen scrolling
            evt.preventDefault();
        }, false);
        
        setInterval(animate, 40);
    });
})();
