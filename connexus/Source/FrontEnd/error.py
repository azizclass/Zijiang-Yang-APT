import webapp2

class ErrorPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/html; charset=utf-8'
        self.response.write('Error!')