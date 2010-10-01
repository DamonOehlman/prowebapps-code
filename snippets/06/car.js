(function() {
    var canvas = null,
        context = null,
        car = null,
        carX = 0,
        endPos = null;
    
    function resetCanvas() {
        canvas = document.getElementById("main");

        // set the canvas height to the window height and width
        canvas.width = screen.width;
        canvas.height = screen.height;
        
        // get a reference to our drawing context
        context = canvas.getContext("2d");
    } // resetContext
    
    function animate() {
        context.save();
        try {
            if (endPos && car && car.complete) {
                // clear the drawing surface
                context.clearRect(0, 0, canvas.width, canvas.height);
                
                // draw the car
                context.drawImage(car, 
                            (carX / window.devicePixelRatio) - car.width, 
                            (endPos.y / window.devicePixelRatio) - car.height);
                
                // draw an indicator to highlight the difference between the car and 
                context.beginPath();
                context.arc(carX, endPos.y, 5, 0, Math.PI * 2, false);
                context.fill();

                // increment the car x
                carX += 3;
                
                // if the car x is greater than the end pos, then remove it
                if (carX > endPos.x) {
                    endPos = null;
                } // if
            } // if
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
            endPos = {
                x: evt.touches[0].pageX,
                y: evt.touches[0].pageY
            };

            carX = 0;
            
            // prevent screen scrolling
            evt.preventDefault();
        }, false);
        
        // load our car image
        car = new Image();
        car.src = "car.png";
        
        setInterval(animate, 40);
    });
})();
