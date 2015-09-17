from google.appengine.api import users

import webapp2
import jinja2
import os
import  urls

path = os.path.dirname(__file__)
path = os.path.dirname(path)
path = os.path.dirname(path)
path = path + '\html_templates'
JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(path),
    extensions=['jinja2.ext.autoescape'],
    autoescape=True)
template_name = 'management.html'


class ManagementPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()

        if user:
            self.response.headers['Content-Type'] = 'text/html; charset=utf-8'
            self.response.write('Hello, ' + user.nickname())
        else:
            self.redirect(users.create_login_url(self.request.uri))

        template_values = urls.getUrlDir()
        template = JINJA_ENVIRONMENT.get_template(template_name)
        self.response.write(template.render(template_values))

