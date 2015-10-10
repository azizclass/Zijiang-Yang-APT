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
    slider.data('unslider').add(ich.image_template({url: url}));
}