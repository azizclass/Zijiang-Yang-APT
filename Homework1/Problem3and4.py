# This file contains answers of Problem 3 and Problem 4 in HW1. Author: Zijiang Yang

# Problem 3 ------------------------------------------------------------------------------------------------------------
print 'Following is Problem 3'


class Student:
    """Student class"""

    def __init__(self, name, gpa, age):
        self.name = name
        self.gpa = gpa
        self.age = age

    def __str__(self):
        return self.name + ': ' + str(self.age) + ' years old, gpa is ' + str(self.gpa)

    def __lt__(self, other):
        if self.gpa < other.gpa:
            return True
        elif self.gpa > other.gpa:
            return False
        elif self.name < other.name:
            return True
        elif self.name > other.name:
            return False
        elif self.age < other.age:
            return True
        elif self.age > other.age:
            return False
        else:
            return False

    def __eq__(self, other):
        return self.name == other.name and self.gpa == other.gpa and self.age == other.age

    def __hash__(self):
        return (self.name + str(self.gpa) + str(self.age)).__hash__()

    __repr__ = __str__

# Create 7 student objects
s1 = Student('Josh', 3.6, 20)
s2 = Student('John', 4.0, 19)
s3 = Student('Mason', 3.6, 20)
s4 = Student('Mary', 3.2, 24)
s5 = Student('Mary', 3.2, 24)
s6 = Student('Josh', 3.6, 25)
s7 = Student('Mason', 3.6, 22)

# Test sorted
print '\nTesting sorted...'
l = [s1, s2, s3, s4, s5, s6, s7]
print '\nBefore sort, the list is:'
for student in l:
    print student
print '\nAfter sort, the list is:'
for student in sorted(l):
    print student

# Test dict
print '\nTesting dict...\n'
d = {x: x.gpa for x in l}
for i in d:
    print str(i) + '   :   ' + str(d[i])
print s1 in d
print s2 in d
print s3 in d
print s4 in d
print s5 in d
print s6 in d
print s7 in d
print Student('Jack', 3.5, 20) in d  # Student is not in the dict, should return False
print Student('Josh', 3.6, 25) in d  # Student is in the dict, should return True
print Student('Lucy', 2.5, 42) in d  # Student is not in the dict, should return False
print Student('Jim', 4.0, 29) in d   # Student is not in the dict, should return False

# Problem 4 ------------------------------------------------------------------------------------------------------------
print '-'*40
print '\nFollowing is Problem 4'
array = [s1, s2, s3, s4, s5]
print '\nBefore sort, the array is:'
for i in array:
    print i
array.sort(lambda x, y: -1 if x < y else 0 if x == y else 1)
print '\nAfter sort, the array is:'
for i in array:
    print i
