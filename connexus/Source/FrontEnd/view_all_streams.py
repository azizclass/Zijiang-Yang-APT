from google.appengine.api import users


from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey

import webapp2
import urls

template_name = 'view_all_streams.html'


class ViewAllStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streams = Stream.query(Stream.user == user.email(), ancestor=getStreamKey()).order(-Stream.last_newpic_time,
                               -Stream.pic_num, Stream.name, -Stream.create_time)
        template_dict = urls.getUrlDir()
        template_dict['cur_page'] = 'view_all_stream'
        template_dict["streams"] = streams
        self.response.write(urls.getTemplate(template_name).render(template_dict))

