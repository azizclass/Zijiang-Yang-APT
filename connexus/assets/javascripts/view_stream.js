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

    $('#map').gmap({
        zoom: 2,
        streetViewControl: false
    }).bind('init', function(evt, map) {
        map.oms = new OverlappingMarkerSpiderfier(map,{
            markersWontMove: true,
            markersWontHide: true,
            keepSpiderfied: true
        });
        map.oms.addListener('click', function(marker, event){
            marker.onClick();
        });
    });

    $('#timeline').slider({
        range: true,
        min: 1,
        max: 365,
        values: [335, 365],
        change: function( event, ui ) {
            refreshMarkers();
        }
    });

    $('#geo_view_button').click(function(){
        $('#geo_view').popup('show');
        $('#map').gmap('refresh');
        refreshMarkers();
    });
});

function refreshMarkers(){
    var timeline = $('#timeline').slider('instance').values();
    var start = timeline[0];
    var end = timeline[1];
    var oms = $('#map').gmap('get', 'map').oms;
    oms.clearMarkers();
    var clusterer = $('#map').gmap('get', 'MarkerClusterer');
    if(clusterer) clusterer.setMap(null);
    $('#map').gmap('clear', 'markers');
    $.ajax({
        type: 'GET',
        url:  window.location.href,
        dataType: 'json',
        data:{
            geo_info: true,
            timeline: timeline
        },
        success: function(data) {
            timeline = $('#timeline').slider('instance').values();
            if(!(start === timeline[0] && end === timeline[1])) return;
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
        }
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
        $('.light_gallery').lightGallery({
            selector: '.gallery_item',
            mode: 'lg-fade'
        });
    }
    $('#pic_num').html(++pic_num+'');
    $('.light_gallery').data('lightGallery').destroy(true);
    $('.light_gallery').lightGallery({
        selector: '.gallery_item',
        mode: 'lg-fade'
    });
    $('.new_added_image').removeAttr('hidden');
    $('.new_added_image').removeClass('new_added_image');
}