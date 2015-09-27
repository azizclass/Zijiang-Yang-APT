from google.appengine.api import users

from Source.Services.search import search_streams

import urls
import webapp2
import urllib

template_name = 'search_streams.html'
num_results_per_page = 5
num_pages_displayed = 7

class SearchStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        template_dict = urls.getUrlDir()
        key_word = self.request.get('key_word')
        if key_word:
            offset = self.request.get('offset')
            if offset:
                offset = int(offset)
            else:
                offset = 0
            template_dict['key_word'] = key_word
            template_dict['streams'] = search_streams(key_word)
            template_dict['offset'] = offset
            template_dict['num_results_per_page'] = num_results_per_page
            template_dict['num_pages_displayed'] = num_pages_displayed
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        search_key = self.request.get('search_key').encode('utf-8')
        params = {'key_word': search_key}
        self.redirect(urls.URL_SEARCH_STREAM_PAGE + '/?' + urllib.urlencode(params), permanent=True)
