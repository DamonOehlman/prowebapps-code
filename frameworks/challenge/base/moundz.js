MOUNDZ = (function() {
    // initailise constants
    var DEFAULT_ZOOM = 8,
        GEOMINER_MOUNDZ_URL = 'http://api.geominer.net/v1/moundz';
    
    // initialise variables
    var geominer = null,
        map = null,
        mainScreen = true,
        markers = [],
        markerContent = {},
        posWatchId = 0;
        
    /* private functions */
        
    function activateMarker(marker) {
        // iterate through the markers and set to the inactive image
        for (var ii = 0; ii < markers.length; ii++) {
            markers[ii].setIcon('img/pin-inactive.png');
        } // for
        
        // update the specified marker's icon to the active image
        marker.setIcon('img/pin-active.png');
            
        // update the navbar title using jQuery
        $('#marker-nav .marker-title')
            .html(marker.getTitle())
            .removeClass('has-detail')
            .unbind('click');
            
        // if content has been provided, then add the has-detail
        // class to adjust the display to be "link-like" and 
        // attach the click event handler
        var content = markerContent[marker.getTitle()];
        if (content) {
            $('#marker-nav .marker-title')
                .addClass('has-detail')
                .click(function() {
                    $('#marker-detail .content').html(content);
                    showScreen('marker-detail');
                });
        } // if
        
        // update the marker navigation controls
        updateMarkerNav(getMarkerIndex(marker));
    } // activateMarker
    
    function markResources(resourceType, deposits) {
        for (var ii = 0; ii < deposits.length; ii++) {
            // add the marker for the resource deposit
            addMarker(
                new google.maps.LatLng(deposits[ii].lat, deposits[ii].lng), 
                deposits[ii].name, 
                '<div class="resinfo">' + deposits[ii].total + 
                ' resources at this location</div>');
        } // for
    } // markResources
    
    function processResourceSearch(data) {
        // clear any existing markers
        clearMarkers();
        
        // iterate through the resource types and pin
        for (var ii = 0; ii < data.resourceTypes.length; ii++) {
            var resType = data.resourceTypes[ii];
            
            // mark the resources
            markResources(resType.typeName, resType.deposits);
        } // for
    } // markResources
    
    /* exported functions */
        
    function addMarker(position, title, content) {
        // create a new marker to and display it on the map
        var marker = new google.maps.Marker({
            position: position, 
            map: map,
            title: title,
            icon: 'img/pin-inactive.png'
        });
        
        // save the marker content
        markerContent[title] = content;
        
        // add the marker to the array of markers
        markers.push(marker);
        
        // capture touch click events for the created marker
        google.maps.event.addListener(marker, 'click', function() {
            // activate the clicked marker
            activateMarker(marker);
        });
    } // addMarker
    
    function clearMarkers() {
        for (var ii = 0; ii < markers.length; ii++) {
            markers[ii].setMap(null);
        } // for
        
        markers = [];
    } // clearMarkers
    
    function findResources(callback) {
        // get the map center position
        var center = map.getCenter();
        
        $.ajax({
            url: GEOMINER_MOUNDZ_URL + '/resources',
            dataType: 'jsonp',
            data: {
                lat: center.lat(),
                lng: center.lng()
            },
            success: function(data) {
                processResourceSearch(data);
                if (callback) {
                    callback();
                } // if
            }
        });
    } // findResources
    
    function getMarkerIndex(marker) {
        for (var ii = 0; ii < markers.length; ii++) {
            if (markers[ii] === marker) {
                return ii;
            } // if
        } // for 
        
        return -1;
    } // getMarkerIndex
    
    function gotoPosition(position, zoomLevel) {
        // define the required options
        var myOptions = {
            zoom: zoomLevel ? zoomLevel : DEFAULT_ZOOM,
            center: position,
            mapTypeControl: false,
            streetViewControl: false,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        // initialise the map
        map = new google.maps.Map(
            document.getElementById("map_canvas"),
            myOptions);
    } // gotoPosition
    
    function initScreen() {
        // watch for location hash changes
        setInterval(watchHash, 10);

        // next attach a click handler to all close buttons
        $('button.close').click(showScreen);
    } // initScreen
    
    function run(zoomLevel, mockPosition) {
        // check that the watch hasn't already been setup
        // if it has, then exit as we don't want two watches...
        if (posWatchId !== 0) {
            return;
        } // if
        
        // if mock position, then use that instead
        if (mockPosition) {
            gotoPosition(mockPosition, zoomLevel ? zoomLevel : 15);
            findResources(function() {
                module.updateDisplay();
            });            
        }
        else {
            // create the watch
            posWatchId = navigator.geolocation.watchPosition(
                function(position) {
                    var pos = new google.maps.LatLng(
                        position.coords.latitude, 
                        position.coords.longitude);

                    if (map) {
                        map.panTo(pos);
                    }
                    else {
                        gotoPosition(pos, zoomLevel ? zoomLevel : 15);
                    } // if..else

                    findResources(function() {
                        module.updateDisplay();
                    });
                }, 
                null,
                {
                    enableHighAccuracy: true
                });
        } // if..else
    } // run
    
    function showScreen(screenId) {
        mainScreen = typeof screenId !== 'string';
        if (typeof screenId === 'string') {
            $('#' + screenId).css('left', '0px');

            // update the location hash to marker detail
            window.location.hash = screenId;
        }
        else {
            $('div.child-screen').css('left', '100%');
            window.location.hash = '';
        } // if..else
        
        scrollTo(0, 1);
    } // showScreen
    
    function sortMarkers() {
        // sort the markers from top to bottom, left to right
        // remembering that latitudes are less the further south we go
        markers.sort(function(markerA, markerB) {
            // get the position of marker A and the position of marker B
            var posA = markerA.getPosition(),
                posB = markerB.getPosition();

            var result = posB.lat() - posA.lat();
            if (result === 0) {
                result = posA.lng() - posB.lng();
            } // if
            
            return result;
        });
    } // sortMarkers
    
    function updateMarkerNav(markerIndex) {
        
        // find the marker nav element
        var markerNav = $('#marker-nav');
        
        // reset the disabled state for the images and unbind click events
        markerNav.find('img')
            .addClass('disabled')
            .unbind('click');
            
        // if we have more markers at the end of the array, then update
        // the marker state
        if (markerIndex < markers.length - 1) {
            markerNav.find('img.right')
                .removeClass('disabled')
                .click(function() {
                    activateMarker(markers[markerIndex + 1]);
                });
        } // if
        
        if (markerIndex > 0) {
            markerNav.find('img.left')
                .removeClass('disabled')
                .click(function() {
                    activateMarker(markers[markerIndex - 1]);
                });
        } // if
    } // updateMarkerNav
    
    function watchHash() {
        // this function monitors the location hash for a reset to empty
        if ((! mainScreen) && (window.location.hash === '')) {
            showScreen();
        } // if
    } // watchHash

    var module = {
        addMarker: addMarker,
        clearMarkers: clearMarkers,
        
        findResources: findResources,
        
        init: function(zoomLevel) {
            // initialise the geominer bridge
            geominer = new GEOMINER.Bridge({
                app: 'moundz',
                login: '#login'
            });

            /*
            $(geominer).bind('authenticated', function(evt) {
                $('#splash').hide();
                $('#app').show();
                
                run(zoomLevel);
            });
            */
            
            // initialise the screen
            initScreen();

            // run the app
            run(zoomLevel, new google.maps.LatLng(-33.86, 151.21));
        },
        
        run: run,
        
        updateDisplay: function() {
            // get the first marker
            var firstMarker = markers.length > 0 ? markers[0] : null;
            
            // sort the markers
            sortMarkers();

            // if we have at least one marker in the list, then 
            // initialize the first marker
            if (firstMarker) {
                activateMarker(firstMarker);
            } // if
        }
    };
    
    return module;
})();