from flask import Flask
from flask import request, jsonify
import sqlite3
import json
import base64
import time

import haversine as hs
from main import chatWithBot, getDaysCounts, getNumbers
from predictor import getData
from ocrdata import read_image

app = Flask(__name__)


@app.route('/')
def hello_world():
    return "<h1>Medicine data</h1><p></p>"


def getNearestPharmacies(lat, lon):
    users = []

    conn = sqlite3.connect('project.db')
    print("Opened database successfully")
    #

    cursorx = conn.execute(
        "SELECT * from tbl_user where type=1").fetchall()

    for cursor in cursorx:
        user = {
            'userId': cursor[0],
            'username': cursor[1],
            'password': cursor[2],
            'name': cursor[3],
            'type': cursor[4],
            'lat': cursor[5],
            'lon': cursor[6],
            'address': cursor[7],
            'pharmacyType': cursor[8],
            'rate': cursor[9]
        }

        coord_1 = (float(str(lat)), float(str(lon)))
        coord_2 = (float(str(user['lat'])), float(str(user['lon'])))

        distance = findDistance(coord_1, coord_2)
        print('distance is ' + str(distance))
        if distance < 10:
            users.append(user)

    conn.close()

    return users


@app.route('/api/v1/resources/pharmacies', methods=['POST'])
def get_pharmacies():
    lat = request.form['lat']
    lon = request.form['lon']

    users = getNearestPharmacies(lat, lon)

    status = True
    message = ""
    results = {
        'result': status,
        'users': users,
        'message': message
    }

    return jsonify(results)


@app.route('/api/v1/resources/user', methods=['POST'])
def get_login():
    user = {}
    status = True

    username = request.form['username']
    password = request.form['password']
    print(username)
    print(password)

    conn = sqlite3.connect('project.db')
    print("Opened database successfully")
    #
    message = "Username or password is wrong"
    cursor = conn.execute(
        "SELECT * from tbl_user where username='" + username + "' AND password='" + password + "'").fetchall()

    # for row in cursor:
    #     results.append(row)
    try:
        message = ""

        user = {
            'userId': cursor[0][0],
            'username': cursor[0][1],
            'password': cursor[0][2],
            'name': cursor[0][3],
            'type': cursor[0][4],
            'lat': cursor[0][5],
            'lon': cursor[0][6],
            'address': cursor[0][7],
            'pharmacyType': cursor[0][8],
            'rate': cursor[0][9]
        }
        # cursor[0]
        print(cursor[0])
    except:
        print("An exception occurred")
        status = False

    conn.close()

    results = {
        'result': status,
        'user': user,
        'message': message
    }

    return jsonify(results)


@app.route('/api/v1/resources/create_user', methods=['POST'])
def create_user():
    conn = sqlite3.connect('project.db')
    # Creating a cursor object using the cursor() method

    data = request.form['jsonString']
    resp = json.loads(data)
    # print(resp['name'])
    print(resp)

    user_id = resp['userId']
    name = resp['name']
    password = resp['password']
    username = resp['username']
    address = resp['address']
    type = resp['type']
    rate = resp['rate']
    lat = resp['rate']
    lon = resp['rate']
    pharmacy_type = resp['pharmacyType']

    cursor = conn.cursor()
    query = "insert into tbl_user ( username,password,name,type,lat,lon,address,pharmacyType,rate)  " \
            "VALUES ('" + username + "','" + password + "','" + name + "'," + str(type) + "," + str(lat) + "," + str(
        lon) + ",'" + address + "'," + str(pharmacy_type) + "," + str(rate) + ")"

    cursor.execute(query)
    conn.commit()
    print("Records inserted........")

    # Closing the connection
    conn.close()

    results = {
        'result': True
    }
    return jsonify(results)


@app.route('/api/v1/resources/chat', methods=['GET', 'POST'])
def chatBot():
    chatInput = request.form['chatInput']
    userId = request.form['userId']
    return jsonify(chatBotReply=chatWithBot(chatInput, userId))


@app.route('/api/v1/resources/prediction', methods=['POST'])
def prediction():
    medicine_type = request.form['medicineType']
    month_count = request.form['monthCount']
    int_type = int(medicine_type)
    data = getData(int_type, month_count)
    results = {
        'result': True,
        'data': data
    }
    return jsonify(results)


@app.route('/api/v1/resources/imageUpload', methods=['POST'])
def image_upload():
    encoded_string = request.form['imageString']
    userId = request.form['userId']
    timestamp = request.form['timestamp']

    imgdata = base64.b64decode(encoded_string)
    filename = str(userId) + "_" + str(timestamp) + ".jpg"  # I assume you have a way of picking unique filenames
    with open(filename, 'wb') as f:
        f.write(imgdata)
    val = read_image(filename)
    data = create_order_using_string(create_string_using_result_array(val))
    originalContent = create_string_using_result_array(val)
    results = {
        'result': True,
        'data': data,
        'originalContent': originalContent
    }
    return jsonify(results)


@app.route('/api/v1/resources/getNotifications', methods=['POST'])
def get_notification():
    conn = sqlite3.connect('project.db')
    print("Opened database successfully")
    userId = request.form['userId']
    seenIds = request.form['seenIds']
    idData = json.loads(str(seenIds))
    print(idData)

    for d in idData:
        conn.execute("UPDATE tbl_notification set seen = 1 where id = " + str(d))

    query = "SELECT * FROM tbl_notification WHERE seen = 0 and user_id = " + str(userId)

    print(query)

    cursor = conn.execute(query).fetchall()
    data = []
    for row in cursor:
        if row[2] == 1:
            seen = True
        else:
            seen = False

        obj = {
            'id': row[0],
            'seen': seen,
            'content': row[3],
            'title': row[4],
            'time': row[5],
            'type': row[6],
            'additional': row[7]
        }
        data.append(obj)
        print(row)
    conn.commit()
    print("Records inserted........")

    # Closing the connection
    conn.close()
    results = {
        'result': True,
        'notifications': data
    }
    return jsonify(results)


@app.route('/api/v1/resources/findLowestCostPharmacy', methods=['POST'])
def findLowestCostPharmacy():
    userId = request.form['userId']
    detailArray = request.form['jsonArray']
    medData = json.loads(str(detailArray))
    print(medData)
    # load all pharmacies
    conn = sqlite3.connect('project.db')
    cursor = conn.execute(
        "SELECT userId from tbl_user where type = 1").fetchall()

    arrayMedVal = []
    lat = request.form['lat']
    lon = request.form['lon']

    parmacies = getNearestPharmacies(lat, lon)

    for row in parmacies:
        pharmacyId = row['userId']
        totalValue = 0.0
        for med_ty in medData:
            strQuery = "select price FROM tbl_stock WHERE pharmacy_id = " + str(
                pharmacyId) + " and medicine_type_id = " + str(
                med_ty.get('typeId'))

            cursorMedicine = conn.execute(strQuery).fetchall()
            try:
                totalValue = totalValue + int(str(med_ty.get('quantity'))) * float(cursorMedicine[0][0])
            except:
                print("no val in table")
        obj = {"pharmacyId": pharmacyId, "value": totalValue, "name": row['name']}
        if totalValue > 0.0:
            arrayMedVal.append(obj)
        print(totalValue)
    print(arrayMedVal)
    pharmacy = 0
    try:
        minValobject = arrayMedVal[0]
        for minObj in arrayMedVal:
            if minValobject.get('value') > minObj.get('value'):
                minValobject = minObj
        pharmacy = int(str(minValobject.get('pharmacyId')))
        pharmacyName = str(minValobject.get('name'))

        results = {
            'result': True,
            'pharmacyId': pharmacy,
            'pharmacyName': pharmacyName
        }
    except:
        print("no val in table")
        results = {
            'result': False
        }

    print(results)
    return jsonify(results)


@app.route('/api/v1/resources/findMostRatedPharmacy', methods=['POST'])
def findMostRatedPharmacy():
    userId = request.form['userId']
    detailArray = request.form['jsonArray']
    medData = json.loads(str(detailArray))
    print(medData)
    # load all pharmacies
    conn = sqlite3.connect('project.db')
    cursor = conn.execute(
        "SELECT userId from tbl_user where type = 1").fetchall()

    lat = request.form['lat']
    lon = request.form['lon']

    parmacies = getNearestPharmacies(lat, lon)

    rate = 0
    pham = {}

    pharmacyId = 0;

    for row in parmacies:

        if rate < row['rate']:
            rate = row['rate']
            pham = row

    try:
        pharmacyId = pham['userId']
        pharmacyName = pham['name']
        results = {
            'result': True,
            'pharmacyId': pharmacyId,
            'pharmacyName': pharmacyName
        }
    except:
        results = {
            'result': False

        }

    print(results)
    return jsonify(results)


@app.route('/api/v1/resources/placeOrder', methods=['POST'])
def placeOrder():
    userId = request.form['userId']
    pharmacyId = request.form['pharmacyId']
    detailArray = request.form['jsonArray']
    originalContent = request.form['originalContent']
    medData = json.loads(str(detailArray))

    conn = sqlite3.connect('project.db')
    cursor = conn.cursor()

    orderTime = current_milli_time()

    orderNo = str(pharmacyId) + "/" + str(userId) + "/" + str(orderTime)

    query = "insert into tbl_order (order_no,order_time,user_id,pharmacy_id,originalContent) VALUES ('" + orderNo + "'," + str(
        orderTime) + "," + str(userId) + "," + str(pharmacyId) + ",'" + str(originalContent) + "')"
    print(query)
    cursor.execute(query)

    orderId = cursor.lastrowid

    content = "Order Id " + str(orderNo)
    total = 0

    for medicine in medData:
        strQuery = "SELECT medicine_id,price,name from tbl_stock ts INNER JOIN tbl_medicine tm ON ts.medicine_id = " \
                   "tm.id  WHERE pharmacy_id = " + \
                   str(pharmacyId) + " and medicine_type_id = " + str(medicine.get('typeId'))
        print(strQuery)
        cursorMedicine = conn.execute(strQuery).fetchall()
        medicineId = cursorMedicine[0][0]
        quantity = str(medicine.get('quantity'))

        content = content + "<br>" + str(cursorMedicine[0][2]) + " " + str(cursorMedicine[0][1]) + " x " + str(quantity)

        total = total + (float(cursorMedicine[0][1]) * float(quantity))

        queryDetail = "insert into tbl_order_detail (order_id,medicine_id,qty) VALUES (" + str(orderId) + "," + str(
            medicineId) + "," + str(quantity) + ") "
        cursor.execute(queryDetail)
    content = content + "<br>" + " Total : " + str(total)

    queryNotification = "insert into tbl_notification (user_id,content,title,notification_time,type,additional) " \
                        "VALUES (" + str(
        pharmacyId) + ",'" + content + "','New Order'," + str(
        orderTime) + ",1,'" + str(
        orderId) + "') "
    print(queryNotification)
    cursor.execute(queryNotification)

    conn.commit()
    print("Records inserted........")

    # Closing the connection
    conn.close()

    results = {
        'result': True
    }
    return jsonify(results)


@app.route('/api/v1/resources/approveOrder', methods=['POST'])
def approveOrder():
    userId = request.form['userId']
    orderId = request.form['orderId']
    status = request.form['status']
    conn = sqlite3.connect('project.db')
    cursor = conn.cursor()

    selectOrderQuery = "select user_id,order_no,originalContent from tbl_order where order_id = " + str(orderId)
    cursorMedicine = conn.execute(selectOrderQuery).fetchall()

    useruId = cursorMedicine[0][0]
    orderNo = cursorMedicine[0][1]
    prescription = cursorMedicine[0][2]
    orderTime = current_milli_time()
    orderDetail = " Order No:" + orderNo
    total = 0

    if int(str(status)) == 1:
        queryOrder = "UPDATE tbl_order SET status = 1 WHERE order_id = " + str(orderId)
        cursor.execute(queryOrder)

        selectOrderDetailQuery = "select medicine_id,qty from tbl_order_detail where order_id = " + str(orderId)

        cursorMedicine = conn.execute(selectOrderDetailQuery).fetchall()

        for med in cursorMedicine:
            medicineId = med[0]
            quantity = med[1]

            selectStockDetailQuery = "SELECT stock,medicine_id,price,name from tbl_stock ts INNER JOIN tbl_medicine tm ON ts.medicine_id = " \
                                     "tm.id  WHERE pharmacy_id = " + \
                                     str(userId) + " and ts.medicine_id = " + str(medicineId)

            cursorStock = conn.execute(selectStockDetailQuery).fetchall()

            orderDetail = orderDetail + "\n" + str(cursorStock[0][3]) + " " + str(cursorStock[0][2]) + " x " + str(
                quantity)
            total = total + (float(cursorStock[0][2]) * float(quantity))

            newQty = int(cursorStock[0][0]) - int(quantity)

            if newQty < 200:
                queryNotificationx = "insert into tbl_notification (user_id,content,title,notification_time,type," \
                                     "additional) " \
                                     "VALUES (" + str(userId) + ",'your " + str(
                    cursorStock[0][3]) + " is low , only " + str(newQty) + " left', " \
                                                                           "'Low Stock'," + str(
                    orderTime) + ",3,'"  "') "
                print(queryNotificationx)
                cursor.execute(queryNotificationx)

            queryOrder = "UPDATE tbl_stock SET stock = " + str(newQty) + " WHERE medicine_id = " + str(
                medicineId) + " AND medicine_id = " + str(medicineId)

            cursor.execute(queryOrder)

        orderDetail = orderDetail + "\n" + " Total : " + str(total)
        queryNotification = "insert into tbl_notification (user_id,content,title,notification_time,type,additional) " \
                            "VALUES (" + str(
            useruId) + ",'Pharmacy accepted your order. Order no. is " + orderNo + "','Order Accepted'," + str(
            orderTime) + ",2,'" + str(
            orderId) + "') "
        print(queryNotification)
        cursor.execute(queryNotification)

    elif int(str(status)) == 2:
        queryOrder = "UPDATE tbl_order SET status = 2 WHERE order_id = " + str(orderId)
        cursor.execute(queryOrder)
        queryNotification = "insert into tbl_notification (user_id,content,title,notification_time,type,additional) " \
                            "VALUES (" + str(
            useruId) + ",'Your order is rejected','Order Rejected'," + str(
            orderTime) + ",2,'" + str(
            orderId) + "') "
        print(queryNotification)
        cursor.execute(queryNotification)
    conn.commit()
    conn.close()

    results = {
        'result': True,
        'prescription': prescription,
        'orderDetail': orderDetail
    }
    return jsonify(results)


def create_string_using_result_array(string_array):
    array1 = string_array[0][1]
    string_med = ""
    for item in array1:
        string_med = string_med + item + " "
    print(string_med)
    return string_med


def create_order_using_string(string_med):
    conn = sqlite3.connect('project.db')
    cursor = conn.execute(
        "SELECT * from tbl_medicine").fetchall()

    medicine_data = []
    order_detail = []
    for row in cursor:
        medicine_data.append(row)

    print(medicine_data)

    # comparing with DB
    for med in medicine_data:
        if string_med.casefold().find(med[2].casefold()) != -1:  # ignore case sensitivity
            order_detail.append(med)
            print("Contains given substring ")

    print(order_detail)

    day_array = getDaysCounts(string_med.casefold())

    size = len(day_array)

    quantity = 0

    if size > 0:
        word = day_array[0]
        num = getNumbers(word)
        if len(num) > 0:
            quantity = int(num[0]) * 3

    details = []
    for med in order_detail:
        obj = {"typeId": med[1], "quantity": quantity}
        details.append(obj)
    return details


def current_milli_time():
    return round(time.time() * 1000)


def findDistance(coord_1, coord_2):
    distance = hs.haversine(coord_1, coord_2)
    return distance


if __name__ == "__main__":
    app.run(host='0.0.0.0')
