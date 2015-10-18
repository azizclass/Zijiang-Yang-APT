import endpoints
from protorpc import messages
from protorpc import message_types
from protorpc import remote

from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey
from Source.Services.search import search_streams
from API import ConnexusAPI

package = 'Connexus'


# Get information of a stream, return a StreamInfo object
def getStreamInfo(stream):
    return StreamInfo(
        user=stream.user,
        name=stream.name,
        create_time=stream.create_time,
        last_newpic_time=stream.last_newpic_time,
        pic_num=stream.pic_num,
        tag=stream.tag,
        subscribers=stream.subscribers,
        coverImageUrl=stream.coverImageUrl if stream.coverImageUrl != "/assets/images/default_cover.jpg"
                        else "http://connexus-1078.appspot.com"+stream.coverImageUrl,
        viewCount=stream.viewCount,
        stream_id=stream.key.id()
    )


# Request for searching
class SearchRequest(messages.Message):
    key_word = messages.StringField(1, required=True)


# Stream infor class
class StreamInfo(messages.Message):
    user = messages.StringField(1, required=True)
    name = messages.StringField(2, required=True)
    create_time = message_types.DateTimeField(3)
    last_newpic_time = message_types.DateTimeField(4)
    pic_num = messages.IntegerField(5)
    tag = messages.StringField(6)
    subscribers = messages.StringField(7, repeated=True)
    coverImageUrl = messages.StringField(8)
    viewCount = messages.IntegerField(9)
    stream_id = messages.IntegerField(10)


# Response to stream requests
class RespondStreams(messages.Message):
    streams = messages.MessageField(StreamInfo, 1, repeated=True)


@ConnexusAPI.api_class()
class StreamAPI(remote.Service):

    @endpoints.method(message_types.VoidMessage, RespondStreams, http_method='GET', name='getAllStreams')
    def getAllStreams(self, request):
        return RespondStreams(streams=[getStreamInfo(stream) for stream in Stream.query(ancestor=getStreamKey())
                                .order(-Stream.last_newpic_time, -Stream.pic_num, Stream.name, -Stream.create_time)])

    @endpoints.method(message_types.VoidMessage, RespondStreams, http_method='GET', name='getStream')
    def getStream(self, request):
        user = endpoints.get_current_user()
        if user:
            return RespondStreams(streams=[getStreamInfo(stream) for stream in Stream.query(Stream.user == user.email(),
                                  ancestor=getStreamKey()).order(-Stream.last_newpic_time, -Stream.pic_num, Stream.name,
                                  -Stream.create_time)])
        else:
            return RespondStreams(streams=[])

    @endpoints.method(SearchRequest, RespondStreams, http_method='GET', name='searchStreams')
    def searchStreams(self, request):
        return RespondStreams(streams=[getStreamInfo(stream) for stream in search_streams(request.key_word)])