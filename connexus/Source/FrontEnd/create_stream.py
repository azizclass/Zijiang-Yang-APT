from google.appengine.api import users
from storage import Stream
from storage import getStreamKey
from  search import addStreamToSearchIndex

import webapp2
import re
import urls

template_name = 'create_stream.html'


class CreateStreamPage(webapp2.RequestHandler):
    def get(self):
        self.response.write(urls.getTemplate(template_name).render(urls.getUrlDir()))

    def post(self):
        user = users.get_current_user()
        name = self.request.get("name")
        emails = re.findall(r'[^(?: |\n)].+',self.request.get("email"))
        extra_message = self.request.get("extra_message")
        tag = self.request.get("tag")
        cover_image_url = self.request.get("cover_image_url")
        stream = Stream(parent=getStreamKey(), user=user.email(), name=name, tag=tag,
                        subscribers=emails)
        if cover_image_url:
            stream.coverImageUrl = cover_image_url
        stream.put()
        addStreamToSearchIndex(stream)
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)
