TODOLIST = (function() {
    var MILLISECONDS_TO_DAYS = 86400000;
    
    function showTaskDetails(selector, task) {
        var container = jQuery(selector),
            daysDue = task.getDaysDue();
        
        // update the relevant task details
        container.find(".task-name").html(task.name);
        container.find(".task-description").html(task.description);
        
        if (daysDue < 0) {
            container.find(".task-daysleft").html("OVERDUE BY " + Math.abs(daysDue) + " DAYS").addClass("overdue");
        }
        else {
            container.find(".task-daysleft").html(daysDue + " days").removeClass("overdue");
        } // if..else
        
        container.slideDown();
    } // showTaskDetails
    
    function populateTaskList() {
        function pad(n) {
            return n<10 ? '0'+n : n;
        }
        
        var listHtml = "",
            monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
        
        // iterate through the current tasks
        for (var ii = 0; ii < currentTasks.length; ii++) {
            var dueDateHtml = 
                "<ul class='calendar right'>" + 
                    "<li class='day'>" + pad(currentTasks[ii].due.getDate()) + "</li>" +
                    "<li class='month'>" + monthNames[currentTasks[ii].due.getMonth()] + "</li>" + 
                    "<li class='year'>" + currentTasks[ii].due.getFullYear() + "</li>" + 
                "</ul>";
            
            // add the list item for the task
            listHtml += "<li id='task_" + currentTasks[ii].id + "'>" + dueDateHtml +
                "<div class='task-header'>" + currentTasks[ii].name + "</div>" + 
                "<div class='task-details'>" + 
                    currentTasks[ii].description + "<br />" +
                    "<a href='#' class='task-complete right'>COMPLETE TASK</a>&nbsp;" + 
                "</div>" +
                "</li>";
        } // for
        
        jQuery("ul#tasklist").html(listHtml);
    } // populateTaskList
    
    function toggleDetailsDisplay(listItem) {
        // slide up any active task details panes
        jQuery(".task-details").slideUp();

        // if the current item is selected implement a toggle
        if (activeItem == listItem) { 
            activeItem = null;
            return; 
        }
        
        // in the current list item toggle the display of the details pane
        jQuery(listItem).find(".task-details").slideDown();
        
        // update the active item
        activeItem = listItem;
    } // toggleDetailsDisplay
    
    // define an array that will hold the current tasks
    var currentTasks = [],
        activeItem = null;
    
    // define the module
    var module = {

        /* todo task */
        
        Task: function(params) {
            params = jQuery.extend({
                id: null,
                name: "",
                description: "",
                due: null
            }, params);
            
            // initialise self
            var self = {
                id: params.id,
                name: params.name,
                description: params.description,
                due: params.due ? new Date(params.due) : null,
                completed: null,
                
                complete: function() {
                    self.completed = new Date();
                },
                
                getDaysDue: function() {
                    return Math.floor((self.due - new Date()) / MILLISECONDS_TO_DAYS);
                }
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
            
            function getTasks(callback, extraClauses) {
                db.transaction(function(transaction) {
                    transaction.executeSql(
                        "SELECT rowid as id, * FROM task " + (extraClauses ? extraClauses : ""),
                        [],
                        function (transaction, results) {
                            // initialise an array to hold the tasks
                            var tasks = [];
                            
                            // read each of the rows from the db, and create tasks
                            for (var ii = 0; ii < results.rows.length; ii++) {
                                tasks.push(new module.Task(results.rows.item(ii)));
                            } // for
                            
                            callback(tasks);
                        }
                    );
                });
            } // getTasks
            
            var subModule = {
                getIncompleteTasks: function(callback) {
                    getTasks(callback, "WHERE completed IS NULL");
                },
                
                getTasksInPriorityOrder: function(callback) {
                    subModule.getIncompleteTasks(function(tasks) {
                        callback(tasks.sort(function(taskA, taskB) {
                            return taskA.due - taskB.due;
                        }));
                    });
                },
                
                getMostImportantTask: function(callback) {
                    subModule.getTasksInPriorityOrder(function(tasks) {
                        callback(tasks.length > 0 ? tasks[0] : null);
                    });
                },
                
                saveTask: function(task, callback) {
                    db.transaction(function(transaction) {
                        // if the task id is not assigned, then insert
                        if (! task.id) {
                            transaction.executeSql(
                                "INSERT INTO task(name, description, due) VALUES (?, ?, ?);", 
                                [task.name, task.description, task.due],
                                function(tx) {
                                    transaction.executeSql(
                                        "SELECT MAX(rowid) AS id from task",
                                        [],
                                        function (tx, results) {
                                            task.id = results.rows.item(0).id;
                                            if (callback) {
                                                callback();
                                            } // if
                                        } 
                                    );
                                }
                            );
                        }
                        // otherwise, update
                        else {
                            transaction.executeSql(
                                "UPDATE task " +
                                "SET name = ?, description = ?, due = ?, completed = ? " + 
                                "WHERE rowid = ?;",
                                [task.name, task.description, task.due, task.completed, task.id],
                                function (tx) {
                                    if (callback) {
                                        callback();
                                    } // if
                                }
                            );
                        } // if..else
                    });
                }
            };
            
            return subModule;
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
        })(),
        
        /* view activation handlers */
        
        activateMain: function() {
            TODOLIST.Storage.getMostImportantTask(function(task) {
                if (task) {
                    // the no tasks message may be displayed, so remove it
                    jQuery("#main .notasks").remove();

                    // update the task details
                    showTaskDetails("#main .task", task);
                    
                    // attach a click handler to the complete task button
                    jQuery("#main .task-complete").unbind().click(function() {
                        jQuery("#main .task").slideUp();
                        
                        // mark the task as complete
                        task.complete();
                        
                        // save the task back to storage
                        TODOLIST.Storage.saveTask(task, module.activateMain);
                    });
                }
                else {
                    jQuery("#main .notasks").remove();
                    jQuery("#main .task").slideUp().after("<p class='notasks'>You have no tasks to complete</p>");
                }
            });
        },
        
        activateAllTasks: function() {
            TODOLIST.Storage.getTasksInPriorityOrder(function(tasks) {
                // update the current tasks
                currentTasks = tasks;

                populateTaskList();
                
                // refresh the task list display
                jQuery("ul#tasklist li").click(function() {
                    toggleDetailsDisplay(this);
                });
                
                jQuery("ul#tasklist a.task-complete").click(function() {
                    // complete the task
                    alert("complete the task");
                });
            });
        }
    };
    
    // define the all tasks view
    PROWEBAPPS.ViewManager.define({
        id: "alltasks",
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
    /* validation code */

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
            
            // now create a new task
            TODOLIST.Storage.saveTask(task, function() {
                // PROWEBAPPS.ViewManager.activate("main");
                PROWEBAPPS.ViewManager.back();
            });
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
    
    // bind activation handlers
    $("#main").bind("activated", TODOLIST.activateMain);
    $("#alltasks").bind("activated", TODOLIST.activateAllTasks);

    // initialise the main view
    PROWEBAPPS.ViewManager.activate("main");
});