from google.appengine.api import search
from google.appengine.api import memcache

from storage import Stream
from storage import getStreamKey

import re


# Format string for easier search, and for non-ascii character search
def formatSearchContent(str):
    segs = re.findall(r'[0-9a-zA-Z]+', str)
    nonEnglish = re.findall(r'[^\x00-\x7f]', str)
    return "".join([x+' ' for x in segs])+"".join([x+' ' for x in nonEnglish])


# Add a stream to search index for searching service
def addStreamToSearchIndex(stream):
    fields = [
        search.TextField(name='name', value=formatSearchContent(stream.name)),
        search.TextField(name='tag', value=formatSearchContent(stream.tag)),
        search.TextField(name='user', value="".join(re.findall(r'(.+)@', stream.user)))
    ]
    index = getIndex()
    index.put(search.Document(doc_id=str(stream.key.id()), fields=fields))
    updateIndex(index)


# Remove stream from search index
def removeStreamFromSearchIndex(stream):
    index = getIndex()
    index.delete(str(stream.key.id()))
    updateIndex(index)


# Escape reserved characters and words by query language
def escapeSearchQuery(query_words):
    segs = re.findall(r'[0-9a-zA-Z]+', query_words)  # extract words
    nonEnglish = re.findall(r'[^\x00-\x7f]', query_words)  # extract non-ascii characters
    return "".join(['"'+x+'" ' for x in segs])+"".join(['"'+x+'" ' for x in nonEnglish])


# Search for results of a query, return streams
def search_streams(query_words):
    query_words = escapeSearchQuery(query_words)
    if len(query_words) == 0:
        return []
    query_option = search.QueryOptions(limit=1000)
    query = search.Query(query_string=query_words, options=query_option)
    ret = dict()
    for doc in getIndex().search(query):
        ids = doc.doc_id.split("_")
        if len(ids) < 2:
            content = Stream.get_by_id(int(ids[0]), getStreamKey())
            if content:
                ret[ids[0]] = content
        else:
            content = Stream.get_by_id(int(ids[1]), getStreamKey())
            if content:
                ret[ids[1]] = content
    return ret.values()


# Try to get index from memcache. If fails, build the index.
def getIndex():
    index = memcache.get('search')
    if index:
        return index
    index = search.Index(name='search')
    for stream in Stream.query(ancestor=getStreamKey()):
        fields = [
            search.TextField(name='name', value=formatSearchContent(stream.name)),
            search.TextField(name='tag', value=formatSearchContent(stream.tag)),
            search.TextField(name='user', value="".join(re.findall(r'(.+)@', stream.user)))
        ]
        index.put(search.Document(doc_id=str(stream.key.id()), fields=fields))
        updateIndex(index)
    return index

# Write the index back to memcache, update the memcache
def updateIndex(index):
    if not memcache.get('search'):
        memcache.add('search', index)
        return
    client = memcache.Client()
    while True:
        client.gets('search')
        if client.cas('search', index):
            break;


