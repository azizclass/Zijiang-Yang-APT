import webapp2

from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers


# An image class, which stores the photo
class Image(ndb.Model):
    name = ndb.StringProperty(required=True, indexed=False)
    description = ndb.StringProperty(indexed=False)
    image = ndb.BlobKeyProperty(required=True, indexed=False)


# A stream class
class Stream(ndb.Model):
    user = ndb.StringProperty(required=True)
    name = ndb.StringProperty(required=True)
    create_time = ndb.DateTimeProperty('t', auto_now_add=True, indexed=False)
    last_newpic_time = ndb.DateTimeProperty('n');
    pic_num = ndb.IntegerProperty(indexed=False, default=0)
    tag = ndb.StringProperty(indexed=False)
    subscriber_emails = ndb.StringProperty('e', repeated=True, indexed=False)
    coverImageUrl = ndb.StringProperty(indexed=False)


# Return a key for strongly consistent query
def getStreamKey(name):
    return ndb.Key("Stream_Ancestor", name);