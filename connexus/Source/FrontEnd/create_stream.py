from google.appengine.api import users
from storage import Stream
from storage import getStreamKey

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
        stream = Stream(parent=getStreamKey(user.user_id()), user=user.user_id(), name=name, tag=tag,
                        subscriber_emails=emails, coverImageUrl=cover_image_url)
        stream.put()
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)
