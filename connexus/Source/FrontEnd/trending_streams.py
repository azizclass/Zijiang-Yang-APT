from Source.Services.summarize_trending_stream import getTopStreams
from Source.Services.storage import getEmailSetting
from Source.Services.storage import setEmailUpdateRate
from google.appengine.api import users

import logging
import webapp2
import urls

template_name = 'trending_streams.html'
trendingStream_num = 10

class TrendingStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        trendingStreams = getTopStreams(trendingStream_num)
        template_dict = urls.getUrlDir()
        template_dict['cur_page'] = 'trending_streams'
        template_dict['trendingStreams'] = trendingStreams
        template_dict['email_setting'] = getEmailSetting(user.email())
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        if self.request.get('update_frequency'):
            setEmailUpdateRate(user.email(), self.request.get('update_frequency'))
        else:
            logging.error('Unable to fetch update frequency!')
            self.error(404)
            return
        self.response.write('success')
