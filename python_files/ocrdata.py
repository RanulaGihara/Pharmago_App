
#import files 
import easyocr
def read_image(image_path):
    value = []
    reader = easyocr.Reader(['en'])
    result = reader.readtext(image_path)

    image_values = []

    for x in result:
        # print(x[1])
        image_values.append(x[1])

    value.append(('Medicines', image_values[9:]))
    return value
