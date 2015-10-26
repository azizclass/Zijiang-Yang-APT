import endpoints
from protorpc import messages
from protorpc import remote
from google.appengine.api.images import get_serving_url

from Source.Services.storage import getStreamKey
from Source.Services.storage import Stream
from Source.Services.storage import Image
from API import ConnexusAPI

import datetime


package = 'Connexus'


class RequestForImages(messages.Message):
    stream_id = messages.IntegerField(1, required=True)


class ImageInfo(messages.Message):
    url = messages.StringField(1, required=True)
    createTime = messages.IntegerField(2, required=True)
    latitude = messages.FloatField(3)
    longitude = messages.FloatField(4)
    parentId = messages.IntegerField(5)
    streamName = messages.StringField(6)
    owner = messages.StringField(7)

class ResponseImages(messages.Message):
    images = messages.MessageField(ImageInfo, 1, repeated=True)

def getImageInfo(img):
    stream = img.key.parent().get()
    return ImageInfo(url=get_serving_url(img.img), createTime=int((img.create_time-datetime.datetime.utcfromtimestamp(0)).total_seconds() * 1000.0),
                     latitude=img.latitude, longitude=img.longitude, parentId=stream.key.id(), streamName=stream.name, owner=stream.user)

# Request for geo-info
class GeoRequest(messages.Message):
    latitude = messages.FloatField(1, required=True)
    longitude = messages.FloatField(2, required=True)


@ConnexusAPI.api_class()
class ImageAPI(remote.Service):

    @endpoints.method(RequestForImages, ResponseImages, http_method='GET', name='getImages')
    def getImages(self, request):
        stream = Stream.get_by_id(request.stream_id, getStreamKey())
        if not stream:
            return ResponseImages()
        return ResponseImages(images=[getImageInfo(x) for x in Image.query(ancestor=stream.key).order(-Image.create_time)])

    @endpoints.method(GeoRequest, ResponseImages, http_method='GET', name='getNearbyImages')
    def getNearbyImages(self, request):
        if request.latitude < -90.0 or request.latitude > 90.0 or request.longitude < -180.0 or request.longitude > 180.0:
            return ResponseImages()
        images = filter(lambda x: x.longitude>=-180.0 and x.longitude<=180.0, Image.query(Image.latitude>=-90.0, Image.latitude<=90.0).fetch());
        return ResponseImages(images=[getImageInfo(img) for img in sorted(images,
                key=lambda x: (x.latitude-request.latitude)**2+(x.longitude-request.longitude)**2)])

