$(function(){
    var cache = {};
    $('#search').autocomplete({
        minLength: 1,
        source: function( request, response ){
            var term = request.term;
            if ( term in cache ) {
                response(cache[term]);
                return;
            }
            $.ajax({
                type: 'GET',
                url:  window.location.href,
                dataType: 'json',
                data:{
                    request_suggestion: true,
                    query_key: term
                },
                success: function(data){
                    cache[term] = data;
                    response(data)
                },
                error: function(){
                    response();
                }
            });
        }
    });
});
