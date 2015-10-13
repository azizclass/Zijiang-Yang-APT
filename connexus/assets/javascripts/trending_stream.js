$(function(){
    toastr.options.preventDuplicates = true;
    toastr.options.newestOnTop = false;
    $('.trending_button').click(function(){
        toastr.clear();
        toastr.options.timeOut = 0;
        toastr.info('Changing report frequency...');
        $.ajax({
            type: 'POST',
            url:  window.location.href,
            dataType: 'text',
            data: {
                update_frequency: $('input[name=update_frequency]:checked').val()
            },
            success: function(data){
                toastr.clear();
                toastr.options.timeOut = 1000;
                toastr.success('Success!');
            },
            error: function(){
                toastr.clear();
                toastr.options.timeOut = 1000;
                toastr.error('Fail!');
            }
        });
    });
});
