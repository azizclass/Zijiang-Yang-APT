from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api.images import get_serving_url
from storage import Image
from storage import Stream
from storage import getStreamKey

import webapp2
import urls
import urllib

template_name = 'view_stream.html'


class ViewStreamPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streamId = self.request.get("id");
        if streamId == None:
            self.error(404)
        stream = Stream.get_by_id(int(streamId), getStreamKey(user.user_id()))
        images = Image.query(ancestor=stream.key).order(Image.time, Image.name)
        upload_url = blobstore.create_upload_url(urls.URL_VIEW_STREAM_PAGE + urls.URL_UPLOAD_HANDLER +
                                                 '/?'+urllib.urlencode({'id':streamId}))
        template_dict = urls.getUrlDir()
        template_dict['upload_url'] = upload_url
        template_dict['images'] = [get_serving_url(x.image) for x in images]
        self.response.write(urls.getTemplate(template_name).render(template_dict))


class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        user = users.get_current_user()
        sid = self.request.get('id')
        if sid == None:
            self.error(404)
        stream = Stream.get_by_id(int(sid), getStreamKey(user.user_id()))
        if stream == None:
            self.error(404)
        upload = self.get_uploads('img')[0]
        name = self.request.get('name')
        comments = self.request.get('comments')
        img = Image(parent= stream.key,name=name, comments=comments, image=upload.key())
        img.put()
        stream.pic_num = stream.pic_num + 1
        stream.last_newpic_time = img.time
        stream.put()
        self.redirect(urls.URL_VIEW_STREAM_PAGE+"/?"+urllib.urlencode({'id':sid}))
