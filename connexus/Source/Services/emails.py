from google.appengine.api import mail
from google.appengine.api import users
from Source.FrontEnd import urls

import urllib

SENDER = "info@connexus-1078.appspotmail.com"


def sendSubscribeInvitationEmail(receivers, message, stream):
    user = users.get_current_user()
    subject = 'Connexus - %s invites you to subscribe the new stream!' % user.nickname()
    message = ("""
%s leaves a message:

%s
""" % (user.nickname(), message)) if message and len(message) > 0 else ""

    content = """Dear Sir/Madam,

%s has created a new stream "%s" in Connexus and invited you to subscribe it.
%s
Subscribe now to get the newest pictures added to the stream!

Click the following link to view and subscribe the stream:

%s

If you have not tried Connexus yet, do not hesitate to start your journey!

Thank you!

Yours sincerely,
Connexus""" % (user.nickname(), stream.name, message,
       'http://connexus-1078.appspot.com/'+urls.URL_VIEW_STREAM_PAGE+'/?'+urllib.urlencode({'id': stream.key.id()}))

    for receiver in receivers:
        mail.send_mail(SENDER, receiver, subject, content)



def sendTrendingStreamEmails(receiver_addr, receiver_name, trendingStreams):
    subject = 'Connexus - Digests of trending streams'
    digests = ''
    for stream, views in trendingStreams:
        digests = digests + '\n%-30s  %-5d views in last hour\n' % (stream.name, views)
    content = """Dear %s,

We have prepared digests for trending streams by your request:
%s
Thank you for your interests in Connexus!

For more information about trending streams, please click the following link:

%s

Thank you!

Yours sincerely,
Connexus""" % (receiver_name, digests, 'http://connexus-1078.appspot.com/'+urls.URL_TRENDING_STREAMS_PAGE)

    mail.send_mail(SENDER, receiver_addr, subject, content)


