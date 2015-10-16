from google.appengine.api import users

from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey
from Source.Services.storage import deleteStream
from Source.Services.storage import unsubscribeStream
from Source.Services.search import removeStreamFromSearchService

import urls
import webapp2

template_name = 'management.html'


class ManagementPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streams = Stream.query(Stream.user == user.email(), ancestor=getStreamKey()).order(-Stream.last_newpic_time,
                               -Stream.pic_num, Stream.name, -Stream.create_time)
        subscribed = Stream.query(Stream.subscribers == user.email(), ancestor=getStreamKey()).order(-Stream.last_newpic_time,
                               -Stream.pic_num, Stream.name, -Stream.create_time)
        template_dict = urls.getUrlDir()
        template_dict['cur_page'] = 'management'
        template_dict["streams"] = streams
        template_dict["subscribed"] = subscribed
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        if self.request.get('delete'):
            for sid in self.request.get_all("del_checkbox"):
                stream = Stream.get_by_id(int(sid), getStreamKey())
                if stream:
                    removeStreamFromSearchService(stream)
                    deleteStream(stream)
            self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)
            return
        if self.request.get('unsubscribe'):
            for sid in self.request.get_all("unsub_checkbox"):
                stream = Stream.get_by_id(int(sid), getStreamKey())
                if stream:
                    unsubscribeStream(stream, user.email())
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)




