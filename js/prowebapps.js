PROWEBAPPS = (function() {
    var actionCounter = 0;
    var ROTATION_CLASSES = {
        "0": "none",
        "90": "right",
        "-90": "left",
        "180": "flipped"
    };
    
    
    function monitorOrientationChanges() {
        var canDetect = "onorientationchange" in window;
        var orientationTimer = 0; 
        
        $(window).bind(canDetect ? "orientationchange" : "resize", function(evt) {
            clearTimeout(orientationTimer);
            orientationTimer = setTimeout(function() {
                // given we can only really rely on width and height at this stage, 
                // calculate the orientation based on aspect ratio
                var aspectRatio = 1;
                if (window.innerHeight !== 0) {
                    aspectRatio = window.innerWidth / window.innerHeight;
                } // if

                // determine the orientation based on aspect ratio
                var orientation = aspectRatio <= 1 ? "portrait" : "landscape";

                // if the event type is an orientation change event, we can rely on
                // the orientation angle
                var rotationText = null;
                if (evt.type == "orientationchange") {
                    rotationText = ROTATION_CLASSES[window.orientation.toString()];
                } // if

                $(window).trigger("reorient", [orientation, rotationText]);
            }, 500);
        });        
    } // monitorOrientationChanges
    
    var module = {
        getFormValues: function(form) {
            var values = {};
            
            // iterate through the form values using jQuery
            jQuery(form).find(":input").each(function() {
                // get the name of the field
                var fieldName = this.name.replace(/^.*\[(\w+)\]$/, "$1");
                
                // set the value for the field
                values[fieldName] = this.value;
            });
            
            return values;
        },
        
        Storage: (function() {
            window.addEventListener("storage", function(evt) {
                alert(evt);
            }, false);
            
            function getStorageScope(scope) {
                if (scope && (scope == "session")) {
                    return sessionStorage;
                } // if
                
                return localStorage;
            } // getStorageTarget
            
            // TODO: check for storage support here
            
            return {
                get: function(key, scope) {
                    // get the storage target
                    var value = getStorageScope(scope).getItem(key);
                    
                    // if the value looks like serialized JSON, parse it
                    return (/^(\{|\[).*(\}|\])$/).test(value) ? JSON.parse(value) : value;
                },
                
                set: function(key, value, scope) {
                    // if the value is an object, the stringify using JSON
                    var serializable = jQuery.isArray(value) || jQuery.isPlainObject(value);
                    var storeValue = serializable ? JSON.stringify(value) : value;
                    
                    // save the value
                    getStorageScope(scope).setItem(key, storeValue);
                },
                
                remove: function(key, scope) {
                    getStorageScope(scope).removeItem(key);
                }
            };
        })(),
        
        ViewAction: function(args) {
            args = jQuery.extend({
                label: "",
                run: null
            }, args);
            
            var self = {
                id: actionCounter++,
                
                getAnchor: function() {
                    return "<a href='#' id='action_" + self.id + "'>" + args.label + "</a>";
                },
                
                execute: function() {
                    if (args.run) {
                        args.run.apply(this, arguments);
                    } // if
                }
            };
            
            return self;
        },
        
        ChangeViewAction: function(args) {
            // if the target is not defined, then raise an error
            if (! args.target) {
                throw new Error("Unable to create a ChangeViewAction without a target specified.");
            } // if
            
            // prep the label to equal the target if not defined
            if (! args.label) {
                args.label = args.target;
            } // if
            
            return new module.ViewAction(jQuery.extend({
                run: function() {
                    module.ViewManager.activate(args.target);
                }
            }, args));
        },
        
        ViewManager: (function() {
            var views = {};
            var activeView = null;
            
            function switchView(oldView, newView) {
                var ii, menu = jQuery("#menu").get(0);
                
                // switch the views
                oldView ? jQuery("#" + oldView.id).hide() : null;
                newView ? jQuery("#" + newView.id).show() : null;
                
                // if we have a menu, then update the actions
                if (menu) {
                    // clear the menu and create list items and anchors as required 
                    jQuery(menu).html('');
                    for (ii = 0; activeView && (ii < activeView.actions.length); ii++) {
                        jQuery(menu).append("<li>" + activeView.actions[ii].getAnchor() + "</li>");
                    } // for
                    
                    // attach a click handler to each of the anchors now in the menu
                    jQuery(menu).find("a").click(function(evt) {
                        // find the specified view in the active view and execute it
                        for (ii = 0; ii < activeView.actions.length; ii++) {
                            if (("action_" + activeView.actions[ii].id) == this.id) {
                                activeView.actions[ii].execute();
                                evt.preventDefault();
                                break;
                            } // if
                        } // for
                    });
                } // if
            } // switchView
            
            var subModule = {
                activate: function(viewId) {
                    // save the old view
                    var oldView = activeView;
                    
                    // if a view id has been specified, but doesn't exist in the views check for a div
                    if (viewId && (! views[viewId]) && (jQuery("#" + viewId).get(0))) {
                        subModule.define({
                            id: viewId
                        });
                    } // if
                    
                    // update the active view
                    activeView = viewId ? views[viewId] : null;
                    
                    // update the associated ui elements
                    switchView(oldView, activeView);
                },
                
                getActiveView: function() {
                    return activeView ? jQuery("#" + activeView.id) : null;
                },
                
                define: function(args) {
                    args = jQuery.extend({
                        id: '',
                        actions: []
                    }, args);
                    
                    // if the id is specified, add the view to the list of defined views
                    if (args.id) {
                        views[args.id] = args;
                    } // if
                }
            };
            
            $(document).ready(function() {
                subModule.activate("main");
                
                $("a.changeview").each(function() {
                    $(this).click(function(evt) {
                        subModule.activate(this.href.replace(/^.*\#(.*)$/, "$1"));
                        evt.preventDefault();
                    }); // click
                });
            });
            
            return subModule;
        })()
    };
    
    monitorOrientationChanges();
    
    // set autoscrolling to hide the URL bar
    $(document).ready(function() {
        window.scrollTo(0, 1);
        document.location = "#";
    });
    
    return module;
})();

jQuery.fn.loadSnippet = function(params) {
    // initialise default parameters
    params = jQuery.extend({
        
    }, params);
    
    // if the html element has an id, then that is the snippet we are to load
    this.each(function() {
        var element = this;
        if (element.id) {
            jQuery.ajax({
                url: "snippets/" + element.id + ".html",
                dataType: "html",
                success: function(data, textStatus, rawRequest) {
                    jQuery(element).html(data);
                },

                error: function(rawRequest, textStatus, errorThrow) {
                    jQuery(element).html("Could not load snippet...");
                }
            });
        } // if
    }); // each
}; // loadSnippet

jQuery(document).ready(function() {
    // load snippets for any objects with the class of snippet
    jQuery(".snippet").loadSnippet();
});