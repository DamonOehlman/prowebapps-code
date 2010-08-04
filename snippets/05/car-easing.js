(function() {
    var ANIMATION_DURATION = 1000;
    
    var canvas = null,
        context = null,
        car = null,
        endPos = null,
        animationStart = 0;
    
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
            if (endPos && car && car.complete) {
                // determine the elapsed time
                var elapsedTime = new Date().getTime() - animationStart,
                    carX = PROWEBAPPS.Easing.Sine.Out(
                                elapsedTime, 
                                0, 
                                endPos.x, 
                                ANIMATION_DURATION) - car.width;
                
                // clear the drawing surface
                context.clearRect(0, 0, canvas.width, canvas.height);
                
                // draw the car
                context.drawImage(car, carX, endPos.y - car.height);
                
                // if the car x is greater than the end pos, then remove it
                if (elapsedTime > ANIMATION_DURATION) {
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
                x: evt.touches[0].pageX / window.devicePixelRatio,
                y: evt.touches[0].pageY / window.devicePixelRatio
            };

            // capture the animation start tick count
            animationStart = new Date().getTime();
            
            // prevent screen scrolling
            evt.preventDefault();
        }, false);
        
        // load our car image
        car = new Image();
        car.src = "car.png";
        
        setInterval(animate, 20);
    });
})();
