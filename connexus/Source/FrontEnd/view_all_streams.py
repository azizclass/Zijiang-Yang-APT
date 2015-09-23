from google.appengine.api import users

import webapp2
import urls

template_name = 'view_all_streams.html'


class ViewAllStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        self.response.write(urls.getTemplate(template_name).render(urls.getUrlDir()))