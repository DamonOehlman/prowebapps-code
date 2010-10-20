(function() {
    var timer = null;
    
    function updateAccel(accel) {
        alert("got update");
        $("#accel_x").html(accel);
    } // updateAccel
    
    function handleFail(accel) {
        alert("fail");
    } // handleFail
    
    function init() {
        alert(navigator.accelerometer);
        
        timer = navigator.accelerometer.watchAcceleration(
            updateAccel,
            handleFail, {
                frequency: 100
            });
    } // init
    
    document.addEventListener("deviceready", init, false);
})();