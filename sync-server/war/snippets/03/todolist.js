TODOLIST = (function() {
    // define the module
    var module = {

        /* todo task */
        
        Task: function(params) {
            params = jQuery.extend({
                name: "",
                description: "",
                due: null
            }, params);
            
            // initialise self
            var self = {
                id: null,
                name: params.name,
                description: params.description,
                due: params.due ? new Date(params.due) : null
            };
            
            return self;
        },
        
        /* storage module */
        
        Storage: (function() {
            // open / create a database for the application (expected size ~ 100K)
            var db = null;
            
            try {
                db = openDatabase("todolist", "1.1", "To Do List Database", 100 * 1024);
                
                // check that we have the required tables created
                db.transaction(function(transaction) {
                    transaction.executeSql(
                        "CREATE TABLE IF NOT EXISTS task(" + 
                        "  name TEXT NOT NULL, " + 
                        "  description TEXT, " + 
                        "  due DATETIME, " + 
                        "  completed DATETIME);");
                });
            }
            catch (e) {
                 db = openDatabase("todolist", "1.0", "To Do List Database", 100 * 1024);

                // check that we have the required tables created
                db.transaction(function(transaction) {
                    transaction.executeSql(
                        "CREATE TABLE IF NOT EXISTS task(" + 
                        "  name TEXT NOT NULL, " + 
                        "  description TEXT, " + 
                        "  due DATETIME);");
                });
                
                db.changeVersion("1.0", "1.1", function(transaction) {
                    transaction.executeSql("ALTER TABLE task ADD completed DATETIME;");
                });
            }
            
            return {
                saveTask: function(task, callback) {
                    db.transaction(function(transaction) {
                        transaction.executeSql(
                            "INSERT INTO task(name, description, due) VALUES (?, ?, ?);", 
                            [task.name, task.description, task.due]
                        );
                    });
               }
            };
        })(),
        
        /* validation module */
        
        Validation: (function() {
            var errors = {};

            return {
                displayErrors: function(newErrors) {
                    // initialise variables
                    var haveErrors = false;
                    
                    // update the errors with the new errors
                    errors = newErrors;

                    // remove the invalid class for all inputs
                    $(":input.invalid").removeClass("invalid");

                    // iterate through the fields specified in the errors array
                    for (var fieldName in errors) {
                        haveErrors = true;
                        $("input[name='" + fieldName + "']").addClass("invalid");
                    } // for

                    // if we have errors, then add a message to the errors div
                    $("#errors")
                        .html(haveErrors ? "Errors were found." : "")
                        .css("display", haveErrors ? "block" : "none");
                },
                
                displayFieldErrors: function(field) {
                    var messages = errors[field.name];
                    if (messages && (messages.length > 0)) {
                        // find an existing error detail section for the field
                        var errorDetail = $("#errordetail_" + field.id).get(0);

                        // if it doesn't exist, then create it
                        if (! errorDetail) {
                            $(field).before("<ul class='errors-inline' id='errordetail_" + field.id + "'></ul>");
                            errorDetail = $("#errordetail_" + field.id).get(0);
                        } // if

                        // add the various error messages to the div
                        for (var ii = 0; ii < messages.length; ii++) {
                            $(errorDetail).html('').append("<li>" + messages[ii] + "</li>");
                        } // for
                    } // if
                } // displayFieldErrors
            };
        })()
    };
    
    // define the main view
    PROWEBAPPS.ViewManager.define({
        id: "main",
        actions: [
            new PROWEBAPPS.ChangeViewAction({
                target: "add",
                label: "Add"
            })
        ]
    });
    
    return module;
})();

$(document).ready(function() {
    $(":input").focus(function(evt) {
        TODOLIST.Validation.displayFieldErrors(this);
    }).blur(function(evt) {
        $("#errordetail_" + this.id).remove();
    });

    $("#taskentry").validate({
        submitHandler: function(form) {
            // get the values from the form in hashmap
            var formValues = PROWEBAPPS.getFormValues(form);
            
            // create a new task to save to the database
            var task = new TODOLIST.Task(formValues);
            
            // now create a new todo list task
            TODOLIST.Storage.saveTask(task);
        },
        showErrors: function(errorMap, errorList) {
            // initialise an empty errors map
            var errors = {};

            // iterate through the jQuery validation error map, and convert to 
            // something we can use
            for (var elementName in errorMap) {
                if (! errors[elementName]) {
                    errors[elementName] = [];
                } // if

                errors[elementName].push(errorMap[elementName]);
            } // for

            // now display the errors
            TODOLIST.Validation.displayErrors(errors);
        }
    });
});