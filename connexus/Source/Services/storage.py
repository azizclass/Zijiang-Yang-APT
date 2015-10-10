from google.appengine.ext import ndb

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


# An email setting class
class UserEmailSetting(ndb.Model):
    user = ndb.StringProperty(required=True, indexed=False)
    name = ndb.StringProperty(required=True, indexed=False)
    email_update_rate = ndb.StringProperty(default='no_reports')

# Return a key for strongly consistent query
def getStreamKey():
    return ndb.Key("Stream_Ancestor", "Ancestor");

