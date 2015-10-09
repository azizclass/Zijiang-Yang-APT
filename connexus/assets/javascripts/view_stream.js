$(function(){
    $('.light_gallery').lightGallery({
        selector: '.gallery_item',
        mode: 'lg-fade'
    });
    $('.slider').lightSlider();
});

function updatePicture(url){
    $('.slider').prepend(ich.image_template({url: url}));
    $('.light_gallery').data('lightGallery').destroy(true);
    $('.light_gallery').lightGallery();
}
