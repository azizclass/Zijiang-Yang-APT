import endpoints
from protorpc import messages
from protorpc import remote
from google.appengine.api.images import get_serving_url

from Source.Services.storage import getStreamKey
from Source.Services.storage import Stream
from Source.Services.storage import Image
from API import ConnexusAPI

package = 'Connexus'


class RequestForImages(messages.Message):
    stream_id = messages.IntegerField(1, required=True)


class ResponseImages(messages.Message):
    image_urls = messages.StringField(1, repeated=True)


@ConnexusAPI.api_class()
class ImageAPI(remote.Service):

    @endpoints.method(RequestForImages, ResponseImages, http_method='GET', name='getImages')
    def getImages(self, request):
        stream = Stream.get_by_id(request.stream_id, getStreamKey())
        if not stream:
            return None
        return ResponseImages(image_urls=[get_serving_url(x.img) for x in Image.query(ancestor=stream.key).order(-Image.create_time)])

