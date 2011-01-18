MOUNDZ = (function() {
    // initailise constants
    var DEFAULT_ZOOM = 8,
        GEOMINER_MOUNDZ_URL = 'http://api.geominer.net/v1/moundz';
    
    // initialise variables
    var geominer = null,
        map = null,
        mainScreen = true,
        markers = [],
        markerData = {},
        currentResource = '',
        posWatchId = 0,
        supportsTouch = 'ontouchstart' in window;
        
    /* private functions */
        
    function activateMarker(marker) {
        // iterate through the markers and set to the inactive image
        for (var ii = 0; ii < markers.length; ii++) {
            markers[ii].setIcon('img/pin-inactive.png');
        } // for
        
        // update the specified marker's icon to the active image
        marker.setIcon('img/pin-active.png');
            
        // update the navbar title using jQuery
        $('#marker-nav a[href="#marker-detail"]')
            .find('.ui-btn-text')
                .html(marker.getTitle());
            
        // update the active marker title
        currentResource = marker.getTitle();
        
        // update the marker navigation controls
        updateMarkerNav(getMarkerIndex(marker));
    } // activateMarker
    
    function gatherResource() {
        var currentData = markerData[currentResource];
        if (currentData && geominer) {
            var qty = $('#slider').val();
            geominer.gather(currentData.id, qty, function(totalGathered) {
                // update the quantity available
                currentData.avail = Math.max(currentData.total - totalGathered, 0);
                
                // if the resource is still the same, then update the display
                if (currentData.name === currentResource) {
                    updateResourceDetails();
                } // if
            });
        } // if
    } // gatherResource
    
    function markResources(resourceType, deposits) {
        for (var ii = 0; ii < deposits.length; ii++) {
            // add the marker for the resource deposit
            addMarker(
                new google.maps.LatLng(deposits[ii].lat, deposits[ii].lng), 
                deposits[ii].name,
                deposits[ii]);
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
    
    function updateResourceDetails() {
        var currentData = markerData[currentResource];
        if (currentData) {
            var percAvail = 0;
            
            // determine the resource available percentage
            if (currentData.total !== 0) {
                percAvail = Math.round((currentData.avail / currentData.total) * 100);
            } // if
            
            $('#marker-detail h2').html(currentData.name);
            $('#resavail')
                .html(currentData.avail + ' / ' + currentData.total)
                .css('-webkit-background-size', percAvail + '% 100%');
        } // if
    } // updateResourceDetails
    
    /* exported functions */
        
    function addMarker(position, title, data) {
        // create a new marker and display it on the map
        var marker = new google.maps.Marker({
            position: position, 
            map: map,
            title: title,
            icon: 'img/pin-inactive.png'
        });
        
        markerPosition = position;
        
        // save the marker content
        markerData[title] = data;
        
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
        // size the canvas to the height of the page less the header
        $('#map_canvas').height(
            $('#main').height() - 
            $('#main div[data-role="header"]').outerHeight() -
            $('#main div[data-role="footer"]').outerHeight() - 30
        );
        
        // bind to the marker detail tap event
        $('a[href="#marker-detail"]').live(supportsTouch ? 'tap' : 'click', updateResourceDetails);
        
        $('#btnGather').live(supportsTouch ? 'tap' : 'click', gatherResource);
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
        markerNav.find('a')
            .addClass('disabled')
            .unbind('tap');
            
        // if we have more markers at the end of the array, then update
        // the marker state
        if (markerIndex < markers.length - 1) {
            markerNav.find('a.right')
                .removeClass('disabled')
                .tap(function() {
                    activateMarker(markers[markerIndex + 1]);
                });
        } // if
        
        if (markerIndex > 0) {
            markerNav.find('a.left')
                .removeClass('disabled')
                .tap(function() {
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

            $(geominer).bind('authenticated', function(evt) {
                $('#splash').hide();
                $('.noauth').removeClass('noauth');
                
                // run the app
                run(zoomLevel, new google.maps.LatLng(-33.86, 151.21));
            });
            
            // initialise the screen
            initScreen();
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