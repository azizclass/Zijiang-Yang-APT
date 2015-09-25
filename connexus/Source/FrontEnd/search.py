from google.appengine.api import search
from storage import Stream
from storage import Image
from storage import getStreamKey

import re


# Add a stream to search index for searching service
def addStreamToSearchIndex(stream):
    fields = [
        search.TextField(name='name', value=stream.name),
        search.TextField(name='tag', value=stream.tag),
    ]
    index.put(search.Document(doc_id=str(stream.key.id()), fields=fields))


#Add an image to search index for searching service
def addImageToSearchIndex(image):
    fields=[
        search.TextField(name='name', value=image.name),
        search.TextField(name='comments', value=image.comments)
    ]
    index.put(search.Document(doc_id=str(image.key.id())+"_"+str(image.key.parent().id()), fields=fields))


# Remove something from search index
def removeFromSearchIndex(content):
    index.delete(str(content.key.id()))


# Escape reserved characters and words by query language
def escapeSearchQuery(query):
    segs = re.findall(r'[0-9a-zA-Z]+', query)
    return "".join([' "'+x+'" ' for x in segs])


# Search for results of a query, return streams
def search_streams(query):
    query = escapeSearchQuery(query)
    ret = dict()
    d=index.search(query)
    for doc in index.search(query):
        ids = doc.doc_id.split("_")
        if len(ids) < 2:
            content = Stream.get_by_id(int(ids[0]), getStreamKey())
            if content:
                ret[ids[0]] = content
        else:
            content = Stream.get_by_id(int(ids[1]), getStreamKey())
            if content:
               ret[ids[1]] = content
    return ret


# Put all records to search index for searching
index = search.Index(name='search')
for stream in Stream.query(ancestor=getStreamKey()):
    addStreamToSearchIndex(stream)
    for image in Image.query(ancestor=stream.key):
        addImageToSearchIndex(image)