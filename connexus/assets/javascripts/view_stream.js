var slider;

$(function(){
    $('.light_gallery').lightGallery({
        selector: '.gallery_item',
        mode: 'lg-fade'
    });
    slider = $('.slider').unslider({
        dots: true,
        numPerSlider: 3
    });
});

function updatePicture(url){
    var pic_num = parseInt($('#pic_num').html());
    if(pic_num> 0)
        slider.data('unslider').add(ich.image_template({url: url}));
    else{
        $('#view_stream_words').replaceWith(ich.slider_template({url: url}));
        slider = $('.slider').unslider({
            dots: true,
            numPerSlider: 3
        });
    }
    $('#pic_num').html(++pic_num+'');
    $('.light_gallery').data('lightGallery').destroy(true);
    $('.light_gallery').lightGallery({
        selector: '.gallery_item',
        mode: 'lg-fade'
    });
}