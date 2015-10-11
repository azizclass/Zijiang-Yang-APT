from google.appengine.api import search
from google.appengine.api import memcache

from storage import Stream
from storage import getStreamKey

import re


# Format string for easier search
def formatSearchContent(string):
    string = string.lower()
    segs = re.findall(r'[0-9a-zA-Z]+', string)
    nonEnglish = re.findall(r'[^\x00-\x7f]+', string)
    return segs + nonEnglish


# Add a stream to search index for searching service
def addStreamToSearchService(stream):
    trie = getTrie()
    sid = stream.key.id()
    for str in formatSearchContent(stream.name):
        trie.add(str, sid)
    for str in formatSearchContent(stream.tag):
        trie.add(str, sid)
    for str in formatSearchContent("".join(re.findall(r'(.+)@', stream.user))):
        trie.add(str, sid)
    updateTrie(trie)


# Remove stream from search index
def removeStreamFromSearchService(stream):
    trie = getTrie()
    sid = stream.key.id()
    for str in formatSearchContent(stream.name):
        trie.remove(str, sid)
    for str in formatSearchContent(stream.tag):
        trie.remove(str, sid)
    for str in formatSearchContent("".join(re.findall(r'(.+)@', stream.user))):
        trie.remove(str, sid)
    updateTrie(trie)


# Search for results of a query, return streams
def search_streams(query_words):
    query_words = formatSearchContent(query_words)
    sids = []
    trie = getTrie()
    for word in query_words:
        sids_of_word = set()
        for string in trie.searchSubstring(word):
            sids_of_word = sids_of_word.union(trie.get(string))
        sids.append(sids_of_word)
    ret = [Stream.get_by_id(sid, getStreamKey()) for sid in reduce(lambda x, y: x.intersection(y), sids)]
    return ret


# Get search suggestions from query words
def search_suggestion(query_words, max_num):
    query_words = formatSearchContent(query_words)
    suggestions = []
    sugOfId = {}
    trie = getTrie()
    for word in query_words:
        suggestion = []
        for string in trie.searchSubstring(word):
            suggestion.append(string)
        suggestions.append(suggestion)
    ret = set()
    combineSuggestions(trie, '', set(), suggestions, 0, ret, max_num)
    return ret


# Get all combinations of suggestions
def combineSuggestions(trie, cur_string, cur_str_set, suggestions, index, result, max_num):
    if len(result) >= max_num:
        return
    if index == len(suggestions):
        m = [trie.get(string) for string in cur_str_set]
        id_set = reduce(lambda x, y: x.intersection(y), [trie.get(string) for string in cur_str_set])
        if len(id_set) > 0:
            result.add(cur_string)
        return
    for suggest in suggestions[index]:
        cur = cur_string
        dup = True
        if not(suggest in cur_str_set):
            cur_str_set.add(suggest)
            cur = cur_string + (' ' if len(cur_string) > 0 else '') + suggest
            dup = False
        combineSuggestions(trie, cur, cur_str_set, suggestions, index+1, result, max_num)
        if not dup:
            cur_str_set.remove(suggest)


# Try to get trie from memcache. If fails, build the trie.
def getTrie():
    trie = memcache.get('search')
    if trie:
        return trie
    trie = Trie()
    for stream in Stream.query(ancestor=getStreamKey()):
        sid = stream.key.id()
        for str in formatSearchContent(stream.name):
            trie.add(str, sid)
        for str in formatSearchContent(stream.tag):
            trie.add(str, sid)
        for str in formatSearchContent("".join(re.findall(r'(.+)@', stream.user))):
            trie.add(str, sid)
        updateTrie(trie)
    return trie


# Write the trie back to memcache, update the memcache
def updateTrie(trie):
    if not memcache.get('search'):
        memcache.add('search', trie)
        return
    client = memcache.Client()
    while True:
        client.gets('search')
        if client.cas('search', trie):
            break;


class TrieNode:
    def __init__(self):
        self.Next = dict()
        self.values = set()
        self.strings = set()

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def add(self, string, value):
        node = self.root
        i = 0
        while i < len(string):
            if not (string[i] in node.Next):
                node.Next[string[i]] = TrieNode()
            node = node.Next[string[i]]
            i = i + 1
        if len(node.values) == 0:
            for i in range(1, len(string)):
                self.__add_suffix__(string, i)
        node.strings.add(string)
        node.values.add(value)

    def __add_suffix__(self, string, i):
        node = self.root
        while i < len(string):
            if not (string[i] in node.Next):
                node.Next[string[i]] = TrieNode()
            node = node.Next[string[i]]
            i = i + 1
        node.strings.add(string)

    def get(self, string):
        node = self.root
        index = 0
        while index < len(string):
            if not (string[index] in node.Next):
                return []
            node = node.Next[string[index]]
            index = index + 1
        return node.values

    def searchSubstring(self, substring):
        node = self.root
        index = 0
        while index < len(substring):
            if not (substring[index] in node.Next):
                return []
            node = node.Next[substring[index]]
            index = index + 1
        return self.__traversal_strings__(node, set())

    def __traversal_strings__(self, node, result):
        for string in node.strings:
            result.add(string)
        for n in node.Next.values():
            self.__traversal_strings__(n, result)
        return result

    def remove(self, string, value):
        node = self.root
        index = 0
        while index < len(string):
            if not (string[index] in node.Next):
                return
            node = node.Next[string[index]]
            index = index + 1
        if value in node.values:
            node.values.remove(value)
        if len(node.values) == 0:
            for i in range(0, len(string)):
                self.__remove_suffix__(string, self.root, i)

    def __remove_suffix__(self, string, node, i):
        if node is None:
            return None
        if i == len(string):
            if string in node.strings:
                node.strings.remove(string)
        elif string[i] in node.Next:
            node.Next[string[i]] = self.__remove_suffix__(string, node.Next[string[i]], i+1)
            if node.Next[string[i]] is None:
                del(node.Next[string[i]])
        if len(node.Next) == 0 and len(node.strings) == 0 and len(node.values) == 0:
            return None
        else:
            return node

