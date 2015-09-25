from google.appengine.api import users
from storage import Stream
from storage import getStreamKey

import webapp2
import urls

template_name = 'view_all_streams.html'


class ViewAllStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streams = Stream.query(Stream.user == user.email(), ancestor=getStreamKey()).order(-Stream.last_newpic_time,
                               -Stream.pic_num, Stream.name, -Stream.create_time)
        template_dict = urls.getUrlDir()
        template_dict["streams"] = streams
        s = [x.coverImageUrl for x in streams]
        self.response.write(urls.getTemplate(template_name).render(template_dict))

