from google.appengine.api import users
from Source.Services.storage import Stream
from Source.Services.storage import createStream
from Source.Services.storage import getStreamKey
from Source.Services.search import addStreamToSearchService
from Source.Services.emails import sendSubscribeInvitationEmail
from Source.Services.emails import is_valid_email
from error import jumpToErrorPage

import urls
import webapp2
import re

template_name = 'create_stream.html'


class CreateStreamPage(webapp2.RequestHandler):
    def get(self):
        template_dict = urls.getUrlDir()
        template_dict['cur_page'] = 'create_stream'
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        name = self.request.get("name")
        emails = re.findall(r'[^(?: |\n|,)].+', self.request.get("email"))
        extra_message = self.request.get("extra_message")
        tag = self.request.get("tag")
        cover_image_url = self.request.get("cover_image_url")
        for email in emails:
            if not is_valid_email(email): # take from http://www.regular-expressions.info/
                jumpToErrorPage(self, 'Stream is not created! "'+email+'" is not a valid email!')
                return
        if len(Stream.query( Stream.user==user.email(), Stream.name==name, ancestor=getStreamKey()).fetch()) > 0:
            jumpToErrorPage(self, 'Stream is not created! Name "'+name+'" already exists. Please use another name.')
            return
        stream = createStream(user.email(), name, tag, cover_image_url)
        addStreamToSearchService(stream)
        sendSubscribeInvitationEmail(emails, extra_message, stream)
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)
