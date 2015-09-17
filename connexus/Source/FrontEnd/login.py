import urls

from google.appengine.api import users

import webapp2

class LoginPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        if user:
            self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)
        self.response.write('Please log in!')