import urllib

from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api.images import get_serving_url


from Source.Services.storage import Image
from Source.Services.storage import Stream
from Source.Services.storage import getStreamKey
from Source.Services.search import addImageToSearchIndex
from Source.Services.summarize_trending_stream import increaseViewNum
from error import jumpToErrorPage

import urls
import webapp2
import logging


template_name = 'view_stream.html'


class ViewStreamPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streamId = self.request.get("id")
        if not streamId:
            logging.error("Unable to get id from url")
            self.error(404)
            jumpToErrorPage(self)
            return
        stream = Stream.get_by_id(int(streamId), getStreamKey())
        if not stream:
            logging.error("Unable to get stream with id "+streamId)
            self.error(404)
            jumpToErrorPage(self, 'Unable to find the stream! The stream may be deleted!')
            return
        images = Image.query(ancestor=stream.key).order(-Image.time, Image.name)
        template_dict = urls.getUrlDir()
        template_dict['images'] = [get_serving_url(x.image) for x in images]
        template_dict['id'] = streamId
        if stream.user == user.email():
            upload_url = blobstore.create_upload_url(urls.URL_VIEW_STREAM_PAGE + urls.URL_UPLOAD_HANDLER +
                                                     '/?'+urllib.urlencode({'id':streamId}))
            template_dict['upload_url'] = upload_url
        else:
            template_dict['upload_url'] = None
            increaseViewNum(stream)
            stream.viewCount = stream.viewCount + 1
            template_dict['subscribed'] = user.email() in stream.subscribers
        stream.put()
        self.response.write(urls.getTemplate(template_name).render(template_dict))

    def post(self):
        user = users.get_current_user()
        streamId = self.request.get("id")
        if not streamId:
            logging.error("Unable to get id from url")
            self.error(404)
            jumpToErrorPage(self)
            return
        stream = Stream.get_by_id(int(streamId), getStreamKey())
        if not stream:
            logging("Unable to get stream with id "+streamId)
            self.error(404)
            jumpToErrorPage(self, 'Unable to find the stream! The stream may be deleted!')
            return
        if self.request.get('subscribe'):
            if self.request.get('subscribe') == 'Subscribe':
                stream.subscribers.append(user.email())
                stream.put()
            else:
                stream.subscribers.remove(user.email())
                stream.put()
        self.redirect(urls.URL_VIEW_STREAM_PAGE+'/?'+urllib.urlencode({'id':streamId}))


class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        sid = self.request.get('id')
        if not sid:
            logging.error("Unable to get id from url")
            self.error(404)
            jumpToErrorPage(self)
            return
        stream = Stream.get_by_id(int(sid), getStreamKey())
        if not stream:
            logging("Unable to get stream with id "+str(sid))
            self.error(404)
            jumpToErrorPage(self, 'Unable to find the stream! The stream may be deleted!')
            return
        if not self.get_uploads('img') or len(self.get_uploads('img')) < 1 :
            jumpToErrorPage(self, 'Unable to upload the image! Upload failed!')
            return
        upload = self.get_uploads('img')[0]
        if not upload.key:
            jumpToErrorPage(self, 'Unable to upload the image! Upload failed!')
            return
        name = self.request.get('name')
        if len(Image.query(Image.name == name, ancestor=stream.key).fetch()) > 0:
            jumpToErrorPage(self, 'Image is not created! Name "'+name+'" already exists. Please use another name.')
            return
        comments = self.request.get('comments')
        img = Image(parent= stream.key,name=name, comments=comments, image=upload.key())
        img.put()
        addImageToSearchIndex(img)
        stream.pic_num = stream.pic_num + 1
        stream.last_newpic_time = img.time
        stream.put()
        self.redirect(urls.URL_VIEW_STREAM_PAGE+"/?"+urllib.urlencode({'id':sid}))
