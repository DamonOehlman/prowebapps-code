(function() {
    var ANIMATION_DURATION = 3000;
    
    var canvas = null,
        context = null,
        car = null,
        wheel = null,
        endPos = null,
        endAngle = 0,
        wheelOffset = 0,
        animationStart = 0;
    
    function resetCanvas() {
        canvas = document.getElementById("main");

        // set the canvas height to the window height and width
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        
        // get a reference to our drawing context
        context = canvas.getContext("2d");
    } // resetContext
    
    function drawWheel(x, y, rotation) {
        if (wheel && wheel.complete) {
            context.save();
            try {
                // translate and rotate around the wheel center
                context.translate(x, y);
                context.rotate(rotation);

                // draw the wheel image (taking into account the wheel image size)
                // context.fillRect(-10, -10, 20, 20);
                context.drawImage(wheel, -wheelOffset, -wheelOffset);
            }
            finally {
                context.restore();
            } // try..finally
            
        } // if
    } // drawWheel
    
    function animate() {
        context.save();
        try {
            if (endPos && car && car.complete) {
                // determine the elapsed time
                var elapsedTime = new Date().getTime() - animationStart,
                    carX = PROWEBAPPS.Easing.Back.Out(
                                elapsedTime, 
                                0, 
                                endPos.x, 
                                ANIMATION_DURATION) - car.width,
                    wheelAngle = PROWEBAPPS.Easing.Back.Out(
                                elapsedTime,
                                0,
                                endAngle,
                                ANIMATION_DURATION);
                                
                // clear the drawing surface
                context.clearRect(0, 0, canvas.width, canvas.height);
                
                // draw the car
                context.drawImage(car, carX, endPos.y - car.height);
                
                // draw the wheels at the appropriate position
                drawWheel(carX + 17, endPos.y - 10, wheelAngle);
                drawWheel(carX + 99, endPos.y - 10, wheelAngle);
                
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
    
    function startCar(destX, destY) {
        endPos = {
            x: destX,
            y: destY
        };
        
        // calculate the end angle based on the end x position
        endAngle = (endPos.x / window.innerWidth) * 8 * Math.PI;

        // capture the animation start tick count
        animationStart = new Date().getTime();
    } // startCar
    
    $(window).bind("resize", resetCanvas).bind("reorient", resetCanvas);

    $(document).ready(function() {
        window.scrollTo(0, 1);
        resetCanvas();

        document.body.addEventListener("touchstart", function(evt) {
            startCar(
                evt.touches[0].pageX,
                evt.touches[0].pageY);
            
            // prevent screen scrolling
            evt.preventDefault();
        }, false);
        
        // load our car image
        car = new Image();
        car.src = "car.png";
        
        wheel = new Image();
        wheel.src = "wheel.png";
        wheel.onload = function() {
            wheelOffset = wheel.width * 0.5;
        };
        
        setInterval(animate, 20);
    });
})();
