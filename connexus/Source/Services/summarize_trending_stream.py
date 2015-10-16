from google.appengine.api import memcache
from collections import deque
from storage import Stream
from datetime import datetime
from datetime import timedelta
from storage import getStreamKey
from storage import UserEmailSetting


import webapp2
import logging
import emails


# Get the view statistic queue from memory cache. If it does not exist, create it
def getQueue():
    queue = memcache.get('ViewingQueue')
    if not (queue is None):
        return queue
    queue = deque()
    memcache.delete('ViewingStat')  # If queue is lost, stat must be cleared too.
    updateQueue(queue)
    return queue


# Put the queue back to memcache
def updateQueue(queue):
    if not memcache.get('ViewingQueue'):
        memcache.add('ViewingQueue', queue)
    client = memcache.Client()
    while True:
        client.gets('ViewingQueue')
        if client.cas('ViewingQueue', queue):
            break;


# Get the statistic information dictionary of streams. If it does not exist, create it
def getStat():
    stat = memcache.get('ViewingStat')
    if not (stat is None):
        return stat
    stat = dict()
    memcache.delete('ViewingQueue')  # If stat is lost, then queue should be cleared too.
    updateStat(stat)
    return stat


# Put the stat back to memcache
def updateStat(stat):
    if not memcache.get('ViewingStat'):
        memcache.add('ViewingStat', stat)
    client = memcache.Client()
    while True:
        client.gets('ViewingStat')
        if client.cas('ViewingStat', stat):
            break;


# Get most trending streams, (stream_id, views_in_last_hour), breaking ties using picture number
def getTrendingStreams():
    trending = memcache.get('TrendingStat')
    if trending:
        return trending
    trending = [(x.key.id(),0) for x in Stream.query(ancestor=getStreamKey()).order(Stream.viewCount)]
    updateTrendingStreams(trending)
    return trending


# Put the trending streams data back
def updateTrendingStreams(trending):
    if not memcache.get('TrendingStat'):
        memcache.add('TrendingStat', trending)
    memcache.replace('TrendingStat', trending)


# Get specific number of trending streams, ranked by popularity
def getTopStreams(max_num):
    trending = getTrendingStreams()
    n = 0
    trendingStreams = []
    for stat in trending:
        stream = Stream.get_by_id(stat[0], getStreamKey())
        if stream:
            trendingStreams.append((stream, stat[1]))
            n = n+1
            if n == max_num:
                break
    return trendingStreams

# Increase the view number by 1 for a stream
def increaseViewNum(stream):
    queue = getQueue()
    queue.append((stream.key.id(), datetime.now()))
    stat = getStat()
    stat[stream.key.id()] = 1 if not(stream.key.id() in stat) else stat[stream.key.id()]+1
    updateQueue(queue)
    updateStat(stat)


class Trending_Stream_Handler(webapp2.RequestHandler):
    email_digest_counter = {'every_5_minutes': 0, 'every_1_hour': 0, 'every_day': 0}
    email_digest_bound = {'every_5_minutes': 1, 'every_1_hour': 12, 'every_day': 288}
    digest_num = 10

    def get(self):
        queue = getQueue()
        stat = getStat()
        now = datetime.now()
        while len(queue)>0 and now-queue[0][1] > timedelta(hours=1):
            if queue[0][0] in stat:
                stat[queue[0][0]] = stat[queue[0][0]]-1
                if stat[queue[0][0]] == 0:
                    del(stat[queue[0][0]])
            else:
                logging.error("Fatal error with trending stream data. Data in queue but does not in stat.")
            queue.popleft()
        trending = [(x.key.id(),stat[x.key.id()] if x.key.id() in stat else 0) for x in Stream.query(ancestor=getStreamKey()).order(-Stream.viewCount)]
        trending.sort(key=lambda x: -x[1])
        updateQueue(queue)
        updateStat(stat)
        updateTrendingStreams(trending)
        n = 0
        trendingStreams = []
        for stat in trending:
            stream = Stream.get_by_id(stat[0], getStreamKey())
            if stream:
                trendingStreams.append((stream, stat[1]))
                n = n+1
                if n == Trending_Stream_Handler.digest_num:
                    break
        for frequency in Trending_Stream_Handler.email_digest_counter.keys():
            Trending_Stream_Handler.email_digest_counter[frequency] = Trending_Stream_Handler.email_digest_counter[frequency]+1
            if Trending_Stream_Handler.email_digest_counter[frequency] == Trending_Stream_Handler.email_digest_bound[frequency]:
                Trending_Stream_Handler.email_digest_counter[frequency] = 0
                for userSetting in UserEmailSetting.query(UserEmailSetting.email_update_rate == frequency):
                    emails.sendTrendingStreamEmails(userSetting.user, userSetting.name, trendingStreams)

        self.response.write(str(trending))


app = webapp2.WSGIApplication([
    ('/tasks/trending_stream', Trending_Stream_Handler)
], debug=True)