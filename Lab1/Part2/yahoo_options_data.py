import json
import sys
import re
import urllib
import cgi
from bs4 import BeautifulSoup

def contractAsJson(filename):
    scrapedData = dict()
    with open(filename) as f:
        soup = BeautifulSoup(f, 'html.parser')
    scrapedData['currPrice'] = float(soup.find('span', id=re.compile('yfs_l84_.+')).string)
    tags = soup.find(string="View By Expiration: ").parent.find_all('a', href=re.compile('m=.+-.+'))
    scrapedData['dateUrls'] = ['http://finance.yahoo.com' + cgi.escape(x['href']).encode('ascii', 'xmlcharrefreplace') for x in tags]
    scrapedData['optionQuotes'] = []
    iterator = soup.find_all('td', class_=['yfnc_h', 'yfnc_tabledata1']).__iter__()
    try:
        while True:
            option = dict()
            option['Strike'] = iterator.next().string
            contractName = re.findall(r'[a-zA-Z]+\d+(?:C|P)', iterator.next().string)[0]
            option['Symbol'] = contractName[: len(contractName)-7]
            option['Date'] = contractName[len(contractName)-7: len(contractName)-1]
            option['Type'] = contractName[len(contractName)-1]
            option['Last'] = iterator.next().string
            option['Change'] = ' ' + iterator.next().find('b').string
            option['Bid'] = iterator.next().string
            option['Ask'] = iterator.next().string
            option['Vol'] = iterator.next().string
            option['Open'] = iterator.next().string
            scrapedData['optionQuotes'].append(option)
    except StopIteration:
        pass
    scrapedData['optionQuotes'].sort(lambda x, y: len(y)-len(x) if len(x)!=len(y) else 1 if x<y else -1 if x>y else 0, lambda x: x['Open'])
    jsonQuoteData = json.dumps(scrapedData)
    return jsonQuoteData
