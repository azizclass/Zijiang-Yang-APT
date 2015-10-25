from Source.FrontEnd.view_stream import UploadHandler

import webapp2


url = '/backend/uploader'



class BackEndUploadHandler(UploadHandler):
    pass


app = webapp2.WSGIApplication([
    (url + "(?:/(?:\?.*)?)?", BackEndUploadHandler)
], debug=True)