from Source.Services.summarize_trending_stream import getTrendingStreams
from Source.Services.storage import getStreamKey
from Source.Services.storage import Stream
from Source.Services.storage import UserEmailSetting
from google.appengine.api import users
from error import jumpToErrorPage

import logging
import webapp2
import urls

template_name = 'trending_streams.html'
trendingStream_num = 10

class TrendingStreamsPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        trending = getTrendingStreams()
        n = 0
        trendingStreams = []
        for stat in trending:
            stream = Stream.get_by_id(stat[0], getStreamKey())
            if stream:
                trendingStreams.append((stream, stat[1]))
                n = n+1
                if n == trendingStream_num:
                    break
        email_setting = 'no_reports'
        setting = UserEmailSetting.get_by_id(user.email())
        if setting:
            email_setting = setting.email_update_rate
        template_dict = urls.getUrlDir()
        template_dict['cur_page'] = 'trending_streams'
        template_dict['trendingStreams'] = trendingStreams
        template_dict['email_setting'] = email_setting
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        setting = UserEmailSetting.get_by_id(user.email())
        if not setting:
            setting = UserEmailSetting(user = user.email(), name=user.nickname(), id = user.email())
        if self.request.get('update_frequency'):
            setting.email_update_rate = self.request.get('update_frequency')
            setting.put()
        else:
            logging.error('Unable to fetch update frequency!')
            self.error(404)
            jumpToErrorPage(self)
            return
        self.redirect(urls.URL_TRENDING_STREAMS_PAGE, permanent=True)
