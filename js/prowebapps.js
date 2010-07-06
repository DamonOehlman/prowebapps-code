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