import endpoints
from protorpc import messages
from protorpc import message_types
from protorpc import remote

from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey
from Source.Services.search import search_streams
from Source.Services.search import search_suggestion
from API import ConnexusAPI

import datetime

package = 'Connexus'


# Get information of a stream, return a StreamInfo object
def getStreamInfo(stream):
    if stream:
        return StreamInfo(
            user=stream.user,
            name=stream.name,
            create_time=int((stream.create_time-datetime.datetime.utcfromtimestamp(0)).total_seconds() * 1000.0),
            last_newpic_time=int((stream.last_newpic_time-datetime.datetime.utcfromtimestamp(0)).total_seconds() * 1000.0)
                             if stream.last_newpic_time else -1,
            pic_num=stream.pic_num,
            tag=stream.tag,
            subscribers=stream.subscribers,
            coverImageUrl=stream.coverImageUrl if stream.coverImageUrl != "/assets/images/default_cover.jpg"
                            else "http://connexus-1078.appspot.com"+stream.coverImageUrl,
            viewCount=stream.viewCount,
            stream_id=stream.key.id()
        )
    else:
        return None


# Request for searching
class SearchRequest(messages.Message):
    key_word = messages.StringField(1, required=True)


class StreamRequest(messages.Message):
    stream_id = messages.IntegerField(1, required=True)


# Stream infor class
class StreamInfo(messages.Message):
    user = messages.StringField(1, required=True)
    name = messages.StringField(2, required=True)
    create_time = messages.IntegerField(3)
    last_newpic_time = messages.IntegerField(4)
    pic_num = messages.IntegerField(5)
    tag = messages.StringField(6)
    subscribers = messages.StringField(7, repeated=True)
    coverImageUrl = messages.StringField(8)
    viewCount = messages.IntegerField(9)
    stream_id = messages.IntegerField(10)


# Response to stream requests
class RespondStreams(messages.Message):
    streams = messages.MessageField(StreamInfo, 1, repeated=True)

# Response to get stream request
class RespondStream(messages.Message):
    stream = messages.MessageField(StreamInfo, 1)

# Response to search suggestions
class RespondSearchSuggestions(messages.Message):
    suggestions = messages.StringField(1, repeated=True)


@ConnexusAPI.api_class()
class StreamAPI(remote.Service):

    @endpoints.method(message_types.VoidMessage, RespondStreams, http_method='GET', name='getAllStreams')
    def getAllStreams(self, request):
        return RespondStreams(streams=[getStreamInfo(stream) for stream in Stream.query(ancestor=getStreamKey())
                                .order(-Stream.last_newpic_time, -Stream.pic_num, Stream.name, -Stream.create_time)])

    @endpoints.method(message_types.VoidMessage, RespondStreams, http_method='GET', name='getOwnedStreams')
    def getOwnedStreams(self, request):
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

    @endpoints.method(SearchRequest, RespondSearchSuggestions, http_method='GET', name='getSearchSuggestion')
    def getSearchSuggestion(self, request):
        return RespondSearchSuggestions(suggestions=[suggestion for suggestion in search_suggestion(request.key_word, 20)])

    @endpoints.method(StreamRequest, RespondStream, http_method='GET', name='getStream')
    def getStream(self, request):
        stream = getStreamInfo(Stream.get_by_id(request.stream_id, getStreamKey()))
        if stream:
            return RespondStream(stream=stream)
        else:
            return RespondStream()

