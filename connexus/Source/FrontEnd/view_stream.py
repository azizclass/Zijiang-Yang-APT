import urllib

from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api.images import get_serving_url

from Source.Services.storage import Stream
from Source.Services.storage import Image
from Source.Services.storage import getStreamKey
from Source.Services.storage import addImage
from Source.Services.storage import subscribeStream
from Source.Services.storage import unsubscribeStream
from Source.Services.summarize_trending_stream import increaseViewNum
from error import jumpToErrorPage
from datetime import datetime
from datetime import timedelta

import json
import urls
import webapp2
import logging


template_name = 'view_stream.html'
images_per_page = 3


class ViewStreamPage(webapp2.RequestHandler):
    def get(self):
        user = users.get_current_user()
        streamId = self.request.get("id")
        if not streamId:
            logging.error("Unable to get id from url")
            self.error(400)
            jumpToErrorPage(self)
            return
        stream = Stream.get_by_id(int(streamId), getStreamKey())
        if not stream:
            logging.error("Unable to get stream with id "+streamId)
            self.error(400)
            jumpToErrorPage(self, 'Unable to find the stream! The stream may be deleted!')
            return
        if self.request.get('newest_pic'):
            self.response.write(get_serving_url(Image.query(ancestor=stream.key).order(-Image.create_time).get().img))
            return
        if(self.request.get('geo_info')):
            timeline = self.request.get_all('timeline[]')
            if (timeline is None) or len(timeline)<2:
                self.error(400)
                return
            startDate = datetime.now() - timedelta(365-int(timeline[0]))
            endDate = datetime.now() - timedelta(365-int(timeline[1]))
            images = []
            for image in Image.query(Image.create_time >= startDate, Image.create_time <= endDate, ancestor=stream.key):
                if image.latitude>=-90.0 and image.latitude<=90.0 and image.longitude>=-180.0 and image.longitude<=180.0:
                    images.append(image)
            self.response.write(json.dumps([{'url': get_serving_url(img.img), 'latitude': img.latitude, 'longitude': img.longitude}
                                            for img in images]))
            return
        if self.request.get('upload'):
            self.response.write(blobstore.create_upload_url(urls.URL_VIEW_STREAM_PAGE + urls.URL_UPLOAD_HANDLER +
                                                          '/?'+urllib.urlencode({'id':streamId})))
            return
        template_dict = urls.getUrlDir()
        template_dict['images'] = [get_serving_url(x.img) for x in Image.query(ancestor=stream.key).order(-Image.create_time)]
        template_dict['id'] = streamId
        template_dict['stream'] = stream
        template_dict['images_per_page'] = images_per_page
        if stream.user == user.email():
            template_dict['is_owner'] = True
        else:
            template_dict['is_owner'] = False
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
            self.error(400)
            return
        stream = Stream.get_by_id(int(streamId), getStreamKey())
        if not stream:
            logging.error("Unable to get stream with id "+streamId)
            self.error(400)
            return
        if self.request.get('subscribe'):
            if user.email() in stream.subscribers:
                unsubscribeStream(stream, user.email())
                self.response.write('unsubscribed')
            else:
                subscribeStream(stream, user.email())
                self.response.write('subscribed')


class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        if not self.get_uploads('img') or len(self.get_uploads('img')) < 1 :
            logging.error("Unable to get img")
            self.error(400)
            return
        upload = self.get_uploads('img')[0]
        if not upload.key():
            logging.error("Unable to get img")
            self.error(500)
            return
        sid = self.request.get('id')
        if not sid:
            logging.error("Unable to get id from url")
            self.error(400)
            blobstore.delete(upload.key())
            return
        stream = Stream.get_by_id(int(sid), getStreamKey())
        if not stream:
            logging.error("Unable to get stream with id "+str(sid))
            self.error(400)
            blobstore.delete(upload.key())
            return
        lat = self.request.get('latitude')
        if not lat:
            logging.error("Unable to get latitude!")
            self.error(400)
            blobstore.delete(upload.key())
            return
        lgi = self.request.get('longitude')
        if not lgi:
            logging.error('Unable to get longitude!')
            logging.error(400)
            blobstore.delete(upload.key())
            return
        addImage(stream, upload, float(lat), float(lgi))
        self.response.write('success')
