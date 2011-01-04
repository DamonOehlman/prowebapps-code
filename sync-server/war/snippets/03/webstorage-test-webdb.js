function errorHandler(transaction, error) {
    alert("SQL error: " + error.message);
}

// open the database
var itemsDb = openDatabase('testdb', '1.0', 'Web Storage Test DB', 100 * 1024);

// create the list items table if it doesn't exist
itemsDb.transaction(function (transaction) {
    transaction.executeSql("CREATE TABLE IF NOT EXISTS listitems(title, createdTime);", [], null, errorHandler);
}, errorHandler);


$(document).ready(function() {
    var loadTicks = new Date().getTime();
    
    function readItems(db, callback) {
        var items = [];
        
        db.readTransaction(function(transaction) {
            transaction.executeSql("SELECT * FROM listitems", [], function(transaction, results) {
                for (var ii = 0; ii < results.rows.length; ii++) {
                    items.push(results.rows.item(ii));
                } // for
                
                if (callback) {
                    callback(items);
                } // if
            });
        });
    } // readItems
    
    function addItem(title, callback) {
        itemsDb.transaction(function(transaction) {
            transaction.executeSql(
                "INSERT INTO listitems(title, createdTime) VALUES (?, ?)", 
                [title, new Date().getTime()],
                callback,
                errorHandler
            );
        });
    } // addItem
    
    function clearItems(callback) {
        itemsDb.transaction(function(transaction) {
            transaction.executeSql("DELETE FROM listitems", [], callback);
        });
    }
    
    function displayItems() {
        loadTicks = new Date().getTime();
        
        readItems(itemsDb, function(items) {
            $("#items li[class!='header']").remove();

            // create list items to display the current items
            for (var ii = 0; ii < items.length; ii++) {
                var itemAge = Math.floor((loadTicks - items[ii].createdTime) / 1000);
                $("#items").append("<li>" + items[ii].title + " (created " + itemAge + "s ago)</li>");
            } // for
            
            // if we have no items, then display the message
            if (items.length === 0) {
                $("#items").append("<li>No items</li>");
            } // if
        });
    } // displayItems
    
    $("#add").click(function() {
        addItem($("#newtitle").val(), function() {
            displayItems();
        });
    });
    
    $("#clear").click(function() {
        clearItems(function() {
            displayItems();
        });
    });
    
    // read the items from the database
    displayItems();
});