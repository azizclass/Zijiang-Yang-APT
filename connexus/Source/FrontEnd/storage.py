from google.appengine.ext import ndb

# An image class, which stores the photo
class Image(ndb.Model):
    name = ndb.StringProperty(required=True)
    comments = ndb.StringProperty(indexed=False)
    image = ndb.BlobKeyProperty(required=True, indexed=False)
    time = ndb.DateTimeProperty('t', auto_now_add=True)


# A stream class
class Stream(ndb.Model):
    user = ndb.StringProperty(required=True)
    name = ndb.StringProperty(required=True)
    create_time = ndb.DateTimeProperty('t', auto_now_add=True)
    last_newpic_time = ndb.DateTimeProperty('n')
    pic_num = ndb.IntegerProperty(default=0)
    tag = ndb.StringProperty(indexed=False)
    subscribers = ndb.StringProperty('e', repeated=True)
    coverImageUrl = ndb.StringProperty(indexed=False, default="/assets/default_cover.jpg")
    viewCount = ndb.IntegerProperty(default=0)


# Return a key for strongly consistent query
def getStreamKey():
    return ndb.Key("Stream_Ancestor", "Ancestor");

