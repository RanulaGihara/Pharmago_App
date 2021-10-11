import re
import haversine as hs
import time


def current_milli_time():
    return round(time.time() * 1000)


# Function to extract all the numbers from the given string
def getNumbers(strx):
    array = re.findall(r'[0-9]+ days', strx)
    return array


# Driver code
strx = "adbv345hj43hvb9 days fgdgf 2 days"
array = getNumbers(strx)
print(*array)

print(current_milli_time())

coord_1 = (7.9310787933865186, 81.03248907260215)
coord_2 = (7.925208177018096, 80.99597075074084)
x = hs.haversine(coord_1, coord_2)
print(f'The distance is {x} km')
