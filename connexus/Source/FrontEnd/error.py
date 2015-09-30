import webapp2
import urls
import urllib


template_name = 'error.html'


class ErrorPage(webapp2.RequestHandler):
    def get(self):
        error = self.request.get('error')
        if not error:
            error = "This page is not available!"
        template_dict = urls.getUrlDir()
        template_dict['error'] = error
        self.response.write(urls.getTemplate(template_name).render(template_dict))


# Jump to the error page to print error message
def jumpToErrorPage(handler, error=None):
    if error:
        error = error.encode('utf-8')
    handler.redirect(urls.URL_ERROR_PAGE + '/?' + urllib.urlencode({'error': error}))