$(document).ready(function() {
    // read the data from local storage for the items
    var items = PROWEBAPPS.Storage.get("listitems");
    var loadTicks = new Date().getTime();

    function displayItems() {
        loadTicks = new Date().getTime();
        
        $("#items li[class!='header']").remove();
        if (items) {
            // create list items to display the current items
            for (var ii = 0; ii < items.length; ii++) {
                var itemAge = Math.floor((loadTicks - items[ii].created) / 1000);
                $("#items").append("<li>" + items[ii].title + " (created " + itemAge + "s ago)</li>");
            } // for
        }
        else {
            $("#items").append("<li>No items</li>");

            // initialise the items array
            items = [];
        } // if..else
    } // displayItems
    

    $("#add").click(function() {
        items.push({
            title: $("#newtitle").val(),
            created: new Date().getTime()
        });
        
        // save the items
        PROWEBAPPS.Storage.set("listitems", items);
        displayItems();
    });
    
    $("#clear").click(function() {
        items = null;
        PROWEBAPPS.Storage.remove("listitems");
        displayItems();
    });
    
    displayItems();
});