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

    $('#geo_view').popup({
        scrolllock: true,
        pagecontainer: 'body',
        transition: 'all 0.3s'
    });

    var myMap, oms;
    $('#map').gmap({
        zoom: 2,
        streetViewControl: false
    }).bind('init', function(evt, map) {
        oms = new OverlappingMarkerSpiderfier(map,{
            markersWontMove: true,
            markersWontHide: true,
            keepSpiderfied: true
        });
        oms.addListener('click', function(marker, event){
            marker.onClick();
        });
    });

    $('#geo_view_button').click(function(){
        $('#geo_view').popup('show');
        $('#map').gmap('refresh');
        refreshMarkers(oms);
    });
});

function refreshMarkers(oms){
    console.log('refresh!');
    oms.clearMarkers();
    $('#map').gmap('clear', 'markers');
    $.getJSON( window.location.href+'&'+$.param({geo_info: true}), function(data) {
        $.each(data, function (i, m) {
            $('#map').gmap('addMarker', {
                'position': new google.maps.LatLng(m.latitude, m.longitude),
                'bounds': false
            }, function(map, marker){
                marker.onClick = function(){
                    $('#map').gmap('openInfoWindow', {content: '<img class="map_popout_img" src='+m.url+' />'}, this);
                };
                oms.addMarker(marker);
            });
        });
        $('#map').gmap('set', 'MarkerClusterer', new MarkerClusterer($('#map').gmap('get', 'map'), $('#map').gmap('get', 'markers'), {maxZoom: 15}));
    });
}

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