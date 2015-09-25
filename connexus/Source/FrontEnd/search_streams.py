from google.appengine.api import users
from search import search_streams

import webapp2
import urls

template_name = 'search_streams.html'


class SearchStreamsPage(webapp2.RequestHandler):
   def get(self):
        user = users.get_current_user()
        self.response.write(urls.getTemplate(template_name).render(urls.getUrlDir()))

   def post(self):
       search_key = self.request.get('search_key')
       d = search_streams(search_key)
       self.redirect(urls.URL_SEARCH_STREAM_PAGE)
