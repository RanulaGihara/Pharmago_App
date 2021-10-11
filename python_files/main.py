import json
import pickle
import random

import nltk
import numpy
from nltk.stem import LancasterStemmer
from tensorflow.python.keras.layers import Dense
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.models import model_from_yaml
import sqlite3
import re
import haversine as hs

nltk.download('punkt')

stemmer = LancasterStemmer()

with open("intents.json") as file:
    data = json.load(file)

try:
    with open("chatbot.pickle", "rb") as file:
        words, labels, training, output = pickle.load(file)

except:
    words = []
    labels = []
    docs_x = []
    docs_y = []

    for intent in data["intents"]:
        for pattern in intent["patterns"]:
            wrds = nltk.word_tokenize(pattern)
            words.extend(wrds)
            docs_x.append(wrds)
            docs_y.append(intent["tag"])

        if intent["tag"] not in labels:
            labels.append(intent["tag"])

    words = [stemmer.stem(w.lower()) for w in words if w != "?"]
    words = sorted(list(set(words)))

    labels = sorted(labels)

    training = []
    output = []

    output_empty = [0 for _ in range(len(labels))]

    for x, doc in enumerate(docs_x):
        bag = []

        wrds = [stemmer.stem(w.lower()) for w in doc]

        for w in words:
            if w in wrds:
                bag.append(1)
            else:
                bag.append(0)

        output_row = output_empty[:]
        output_row[labels.index(docs_y[x])] = 1

        training.append(bag)
        output.append(output_row)

    training = numpy.array(training)
    output = numpy.array(output)

    with open("chatbot.pickle", "wb") as file:
        pickle.dump((words, labels, training, output), file)

try:
    yaml_file = open('chatbotmodel.json', 'r')
    loaded_model_yaml = yaml_file.read()
    yaml_file.close()
    myChatModel = model_from_yaml(loaded_model_yaml)
    myChatModel.load_weights("chatbotmodel.h5")
    print("Loaded model from disk")

except:
    # Make our neural network
    myChatModel = Sequential()
    myChatModel.add(Dense(8, input_shape=[len(words)], activation='relu'))
    myChatModel.add(Dense(len(labels), activation='softmax'))

    # optimize the model
    myChatModel.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

    # train the model
    history = myChatModel.fit(training, output, epochs=1000, batch_size=8)

    print("his")
    # print(history.history)

    # plt.figure(figsize=(12, 12))
    # plt.plot(history.history['accuracy'])
    # plt.plot(history.history['loss'])
    # plt.title('model accuracy')
    # plt.ylabel('value')
    # plt.xlabel('epoch')
    # plt.legend(['accuracy', 'loss'], loc='upper left')
    # plt.show()

    # serialize model to yaml and save it to disk
    model_yaml = myChatModel.to_json()
    with open("chatbotmodel.json", "w") as y_file:
        y_file.write(model_yaml)

    # serialize weights to HDF5
    myChatModel.save_weights("chatbotmodel.h5")
    print("Saved model from disk")


def bag_of_words(s, words):
    bag = [0 for _ in range(len(words))]

    s_words = nltk.word_tokenize(s)
    s_words = [stemmer.stem(word.lower()) for word in s_words]

    for se in s_words:
        for i, w in enumerate(words):
            if w == se:
                bag[i] = 1

    return numpy.array(bag)


def chatWithBot(inputText, userId):
    user = getUsernameById(userId)

    currentText = bag_of_words(inputText, words)
    currentTextArray = [currentText]
    numpyCurrentText = numpy.array(currentTextArray)

    if numpy.all((numpyCurrentText == 0)):
        return {"type": 0, "content": "I didn't get that, try again","originalContent": inputText}

    result = myChatModel.predict(numpyCurrentText[0:1])
    result_index = numpy.argmax(result)
    tag = labels[result_index]

    if result[0][result_index] > 0.7:
        typez = 1
        dataz = []
        for tg in data["intents"]:
            if tg['tag'] == tag:
                responses = tg['responses']
                val = random.choice(responses)
                if tg['tag'] == "greeting":
                    val = val + user
                if tg['tag'] == "medicines":
                    typez = 2
                    val = "Wait your request is processing"
                    dataz = create_order_using_string(inputText)
                if tg['tag'] == "prescription":
                    typez = 3

        return {"type": typez, "content": val, "data": dataz, "originalContent": inputText}


    else:
        return {"type": 0, "content": "I didn't get that, try again","originalContent": inputText}


def chat():
    print("Start talking with the chatbot (try quit to stop)")

    while True:
        inp = input("You: ")
        if inp.lower() == "quit":
            break

        print(chatWithBot(inp))


# chat()

def getUsernameById(userId):
    name = ""
    conn = sqlite3.connect('project.db')
    cursor = conn.execute("select name FROM tbl_user WHERE userId = " + userId)
    for row in cursor:
        name = row[0]

    conn.close()
    return name


def create_order_using_string(string_med):
    conn = sqlite3.connect('project.db')
    cursor = conn.execute(
        "SELECT * from tbl_medicine").fetchall()

    medicine_data = []
    order_detail = []
    for row in cursor:
        medicine_data.append(row)

    print(medicine_data)

    for med in medicine_data:
        if string_med.casefold().find(med[2].casefold()) != -1:
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


def getDaysCounts(string_val):
    array = re.findall(r'[0-9]+ days', string_val)
    return array


def getNumbers(string_val):
    array = re.findall(r'[0-9]+', string_val)
    return array
