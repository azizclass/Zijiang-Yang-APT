# No need to process files and manipulate strings - we will
# pass in lists (of equal length) that correspond to 
# sites views. The first list is the site visited, the second is
# the user who visited the site.

# See the test cases for more details.

def highest_affinity(site_list, user_list, time_list):
    # Returned string pair should be ordered by dictionary order
    # I.e., if the highest affinity pair is "foo" and "bar"
    # return ("bar", "foo").
    siteViewers = {}
    for x,y in zip(site_list, user_list):
        if not x in siteViewers:
            siteViewers[x] = set()
        siteViewers[x].add(y)
    Pair = None
    affinity = -1
    for i in sorted(siteViewers.keys()):
        for j in sorted(siteViewers.keys()):
            if i == j: continue
            curAffinity = len(siteViewers[i] & siteViewers[j])
            if affinity < curAffinity:
                affinity = curAffinity
                Pair = (i, j)

    return Pair
