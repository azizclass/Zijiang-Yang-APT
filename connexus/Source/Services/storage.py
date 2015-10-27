from google.appengine.ext import ndb
from google.appengine.api import taskqueue
from google.appengine.ext.blobstore import blobstore

import webapp2

DeleteURL = "/tasks/delete"

# A stream class
class Stream(ndb.Model):
    user = ndb.StringProperty(required=True)
    name = ndb.StringProperty(required=True)
    create_time = ndb.DateTimeProperty('t', auto_now_add=True)
    last_newpic_time = ndb.DateTimeProperty('n')
    pic_num = ndb.IntegerProperty(default=0)
    tag = ndb.StringProperty(indexed=False)
    subscribers = ndb.StringProperty('e', repeated=True)
    coverImageUrl = ndb.StringProperty(indexed=False, default="/assets/images/default_cover.jpg")
    viewCount = ndb.IntegerProperty(default=0)

class Image(ndb.Model):
    img = ndb.BlobKeyProperty(required=True, indexed=False)
    create_time = ndb.DateTimeProperty(auto_now_add=True)
    latitude = ndb.FloatProperty(required=True)
    longitude = ndb.FloatProperty(required=True)
    tag = ndb.StringProperty()


# An email setting class
class UserEmailSetting(ndb.Model):
    user = ndb.StringProperty(required=True, indexed=False)
    name = ndb.StringProperty(required=True, indexed=False)
    email_update_rate = ndb.StringProperty(default='no_reports')

# Return a key for strongly consistent query
def getStreamKey():
    return ndb.Key("Stream_Ancestor", "Ancestor")


# Create and store a new stream
def createStream(user, name, tag, cover_image_url=None):
    stream = Stream(parent=getStreamKey(), user=user, name=name, tag=tag)
    if cover_image_url:
        stream.coverImageUrl = cover_image_url
    stream.put()
    return stream


# delete a stream
def deleteStream(stream):
    if not stream:
        return
    stream.key.delete()
    taskqueue.add(url=DeleteURL, params={'id': stream.key.id()})


def subscribeStream(stream, user):
    stream.subscribers.append(user)
    stream.put()


def unsubscribeStream(stream, user):
    stream.subscribers.remove(user)
    stream.put()


def addImage(stream, blob, lat, lgi, tag):
    image = Image(parent=stream.key, img=blob.key(), latitude=lat, longitude=lgi, tag=tag)
    image.put()
    stream.pic_num = stream.pic_num + 1
    stream.last_newpic_time = image.create_time
    stream.put()


def getEmailSetting(user):
    email_setting = 'no_reports'
    setting = UserEmailSetting.get_by_id(user)
    if setting:
        email_setting = setting.email_update_rate
    return email_setting


def setEmailUpdateRate(user, rate):
    setting = UserEmailSetting.get_by_id(user)
    if not setting:
        setting = UserEmailSetting(user = user, name=user.nickname(), id = user)
    setting.email_update_rate = rate
    setting.put()


class deleteHandler(webapp2.RequestHandler):
    def post(self):
        key = ndb.Key('Stream_Ancestor', 'Ancestor', 'Stream', int(self.request.get('id')))
        for image in Image.query(ancestor=key):
            blobstore.delete(image.img)
            image.key.delete()


app = webapp2.WSGIApplication([
    (DeleteURL+"(?:/(?:\?.*)?)?", deleteHandler)
], debug=True)