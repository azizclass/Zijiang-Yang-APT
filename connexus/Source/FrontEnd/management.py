from google.appengine.api import users
from google.appengine.ext.blobstore import blobstore
from storage import Stream
from storage import Image
from storage import getStreamKey
from search import removeFromSearchIndex

import webapp2
import urls

template_name = 'management.html'


class ManagementPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streams = Stream.query(Stream.user == user.email(), ancestor=getStreamKey()).order(-Stream.last_newpic_time,
                               -Stream.pic_num, Stream.name, -Stream.create_time)
        template_dict = urls.getUrlDir()
        template_dict["streams"] = streams
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        for sid in self.request.get_all("checkbox"):
            stream = Stream.get_by_id(int(sid), getStreamKey())
            if stream:
                for img in Image.query(ancestor=stream.key):
                    blobstore.delete(img.image)
                    removeFromSearchIndex(img)
                    img.key.delete()
                removeFromSearchIndex(stream)
                stream.key.delete()
        self.redirect(urls.URL_MANAGEMENT_PAGE)


