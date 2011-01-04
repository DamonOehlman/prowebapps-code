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
        
        Easing: (function() {
            var s = 1.70158;
            
            return {
                Linear: function(t, b, c, d) {
                    return c*t/d + b;
                },
                
                Sine: {
                    In: function(t, b, c, d) {
                        return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
                    },
                    
                    Out: function(t, b, c, d) {
                        return c * Math.sin(t/d * (Math.PI/2)) + b;
                    },
                    
                    InOut: function(t, b, c, d) {
                        return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
                    }
                },
                
                Back: {
                    In: function(t, b, c, d) {
                        return c*(t/=d)*t*((s+1)*t - s) + b;
                    },
                    
                    Out: function(t, b, c, d) {
                        return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
                    },
                    
                    InOut: function(t, b, c, d) {
                        return ((t/=d/2)<1) ? c/2*(t*t*(((s*=(1.525))+1)*t-s))+b : c/2*((t-=2)*t*(((s*=(1.525))+1)*t+s)+2)+b;
                    }
                }
            };
        })(),
        
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
            var views = {},
                activeView = null,
                viewStack = [],
                backAction = null;
                
            function getViewActions(view) {
                var html = "";
                for (var ii = 0; view && (ii < view.actions.length); ii++) {
                    html += "<li>" + view.actions[ii].getAnchor() + "</li>";
                } // for
                
                // if the view stack is active, then add a back action
                if (viewStack.length > 0) {
                    html += "<li>" + backAction.getAnchor() + "</li>";
                } // if

                return html;
            } // getViewActions
            
            function getAction(view, actionId) {
                // extrac the id portion from the action id
                actionId = actionId.replace(/^action_(\d+)$/i, "$1");
                
                if (backAction && (backAction.id == actionId)) {
                    return backAction;
                } // if
                
                // find the specified view in the active view and execute it
                for (var ii = 0; ii < view.actions.length; ii++) {
                    if (view.actions[ii].id == actionId) {
                        return view.actions[ii];
                    } // if
                } // for
                
                return null;
            } // getAction
            
            function updateViewStack(oldView, newView) {
                // first let's determine if we should push onto the stack
                var shouldPush = oldView && (
                    (viewStack.length === 0) ||
                    (newView && (viewStack[viewStack.length - 1].id != newView.id))
                );
                 
                // if we should push onto the stack, then do so, otherwise pop
                if (shouldPush) {
                    viewStack.push(oldView);
                    
                    // if the back action does not exist yet, then create it
                    if (! backAction) {
                        backAction = new module.ViewAction({
                            label: "Back",
                            run: function() {
                                subModule.activate(viewStack[viewStack.length - 1].id);
                            }
                        });
                    } // if
                }
                else if (oldView && newView && (viewStack.length > 0)) {
                    viewStack.pop();
                } // if..else
            } // updateViewStack
            
            function switchView(oldView, newView) {
                var ii, menu = jQuery("#menu").get(0);
                
                // if the old view is assigned, then push it onto the stack
                updateViewStack(oldView, newView);
                
                // switch the views using jQuery
                oldView ? jQuery("#" + oldView.id).hide() : null;
                newView ? jQuery("#" + newView.id).show().trigger("activated") : null;
                
                // if we have a menu, then update the actions
                if (menu) {
                    // clear the menu and create list items and anchors as required 
                    jQuery(menu).html(getViewActions(activeView));
                    
                    // attach a click handler to each of the anchors now in the menu
                    jQuery(menu).find("a").click(function(evt) {
                        var action = getAction(activeView, this.id);
                        if (action) {
                            action.execute();
                            evt.preventDefault();
                        } // if
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
                
                back: function() {
                    if (backAction) {
                        backAction.execute();
                    } // if
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
            
            jQuery(document).ready(function() {
                jQuery("a.changeview").each(function() {
                    jQuery(this).click(function(evt) {
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