from google.appengine.api import users
from storage import Stream
from storage import getStreamKey

import webapp2
import urls

template_name = 'management.html'


class ManagementPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streams = Stream.query(ancestor=getStreamKey(user.user_id()))
        template_dict = urls.getUrlDir()
        template_dict["streams"] = streams
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        streams = Stream.query(ancestor=getStreamKey(user.user_id()))
        for stream in streams:
            name = "input_"+str(stream.key.id())
            if self.request.get(name) == 'on':
                stream.key.delete()
        self.redirect(urls.URL_MANAGEMENT_PAGE)


