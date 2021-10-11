import easyocr
import cv2
from matplotlib import pyplot as plt

IMAGE_PATH = 'new/3.jpeg'

reader = easyocr.Reader(['en'])
result = reader.readtext(IMAGE_PATH)
# print(result)

image_values = []

for x in result:
    # print(x[1])
    image_values.append(x[1])

final_result = []

final_result.append(('Date', str(image_values[4].replace('Date:', ''))))
final_result.append(('Name', str(image_values[7].replace('Your Name:', ''))))
final_result.append(('Address', str(image_values[8].replace('Address:', ''))))
final_result.append(('Medicines', image_values[9:]))

# out final result
for f in final_result:
    print(f)

img = cv2.imread(IMAGE_PATH)
font = cv2.FONT_HERSHEY_SIMPLEX

# Date
top_left = tuple(result[4][0][0])
bottom_right = tuple(result[4][0][2])
date_text = result[4][1]
img = cv2.rectangle(img, top_left, bottom_right, (0, 255, 0), 5)
img = cv2.putText(img, date_text, top_left, font, 0.5, (0, 0, 0), 3, cv2.LINE_AA)

# Name
top_left = tuple(result[7][0][0])
bottom_right = tuple(result[7][0][2])
text = result[7][1]
font = cv2.FONT_HERSHEY_SIMPLEX
img = cv2.rectangle(img, top_left, bottom_right, (0, 255, 0), 5)
img = cv2.putText(img, text, top_left, font, 0.5, (0, 0, 0), 3, cv2.LINE_AA)

# Address
top_left = tuple(result[8][0][0])
bottom_right = tuple(result[8][0][2])
text = result[8][1]
font = cv2.FONT_HERSHEY_SIMPLEX
img = cv2.rectangle(img, top_left, bottom_right, (0, 255, 0), 5)
img = cv2.putText(img, text, top_left, font, 0.5, (0, 0, 0), 3, cv2.LINE_AA)

# Medicine
for i in range(9, len(result) - 1):
    top_left = tuple(result[i][0][0])
    bottom_right = tuple(result[i][0][2])
    text = result[i][1]
    font = cv2.FONT_HERSHEY_SIMPLEX
    img = cv2.rectangle(img, top_left, bottom_right, (0, 255, 0), 5)
    img = cv2.putText(img, text, top_left, font, 0.5, (0, 0, 0), 3, cv2.LINE_AA)

plt.imshow(img)
plt.show()
