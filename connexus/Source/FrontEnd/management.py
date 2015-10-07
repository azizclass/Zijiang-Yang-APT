from google.appengine.api import users
from google.appengine.ext.blobstore import blobstore

from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey
from Source.Services.search import removeStreamFromSearchIndex

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
        template_dict["streams"] = streams
        template_dict["subscribed"] = subscribed
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        if self.request.get('delete'):
            for sid in self.request.get_all("del_checkbox"):
                stream = Stream.get_by_id(int(sid), getStreamKey())
                if stream:
                    removeStreamFromSearchIndex(stream)
                    if stream.images:
                        for img in stream.images:
                            blobstore.delete(img)
                    stream.key.delete()
        if self.request.get('unsubscribe'):
            for sid in self.request.get_all("unsub_checkbox"):
                stream = Stream.get_by_id(int(sid), getStreamKey())
                if stream:
                    stream.subscribers.remove(user.email())
                    stream.put()
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)


