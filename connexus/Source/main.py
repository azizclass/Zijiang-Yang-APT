from FrontEnd import management
from FrontEnd import create_stream
from FrontEnd import search_streams
from FrontEnd import trending_streams
from FrontEnd import view_all_streams
from FrontEnd import view_stream
from FrontEnd import urls

import webapp2


class MainPage(webapp2.RequestHandler):
    def get(self):
        self.redirect(urls.URL_MANAGEMENT_PAGE, permanent=True)


app = webapp2.WSGIApplication([
    (urls.URL_MANAGEMENT_PAGE+"(?:/(?:\?.*)?)?", management.ManagementPage),
    (urls.URL_CREATE_STREAM_PAGE+"(?:/(?:\?.*)?)?", create_stream.CreateStreamPage),
    (urls.URL_VIEW_STREAM_PAGE+"(?:/(?:\?.*)?)?", view_stream.ViewStreamPage),
    (urls.URL_VIEW_ALL_STREAMS_PAGE+"(?:/(?:\?.*)?)?", view_all_streams.ViewAllStreamsPage),
    (urls.URL_SEARCH_STREAM_PAGE+"(?:/(?:\?.*)?)?", search_streams.SearchStreamsPage),
    (urls.URL_TRENDING_STREAMS_PAGE+"(?:/(?:\?.*)?)?", trending_streams.TrendingStreamsPage),
    (urls.URL_VIEW_STREAM_PAGE + urls.URL_UPLOAD_HANDLER+"(?:/(?:\?.*)?)?", view_stream.UploadHandler),
    ('/', MainPage)
], debug=True)