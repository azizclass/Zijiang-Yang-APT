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

class ResponseImages(messages.Message):
    images = messages.MessageField(ImageInfo, 1, repeated=True)

def getImageInfo(img):
    return ImageInfo(url=get_serving_url(img.img), createTime=int((img.create_time-datetime.datetime.utcfromtimestamp(0)).total_seconds() * 1000.0),
                     latitude=img.latitude, longitude=img.longitude)


@ConnexusAPI.api_class()
class ImageAPI(remote.Service):

    @endpoints.method(RequestForImages, ResponseImages, http_method='GET', name='getImages')
    def getImages(self, request):
        stream = Stream.get_by_id(request.stream_id, getStreamKey())
        if not stream:
            return None
        return ResponseImages(images=[getImageInfo(x) for x in Image.query(ancestor=stream.key).order(-Image.create_time)])

