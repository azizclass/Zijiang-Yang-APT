from Source.Services.search import search_streams
from Source.Services.search import search_suggestion

import urls
import webapp2
import urllib
import json

template_name = 'search_streams.html'
num_results_per_page = 4
num_pages_displayed = 7

class SearchStreamsPage(webapp2.RequestHandler):
    def get(self):
        if(self.request.get('request_suggestion')):
            self.response.write(json.dumps(search_suggestion(self.request.get('query_key'), 20)))
            return
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
