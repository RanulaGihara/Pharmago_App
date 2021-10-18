import pandas as pd
import numpy as np
from statsmodels.tsa.seasonal import seasonal_decompose
from sklearn.preprocessing import MinMaxScaler
from keras.preprocessing.sequence import TimeseriesGenerator
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
from sklearn.metrics import mean_absolute_error
from sklearn.metrics import mean_absolute_percentage_error
from sklearn.metrics import matthews_corrcoef
from math import sqrt


#has made some contributions 

def getData(medicine_type, month_count):
    if medicine_type == 1:
        df = pd.read_csv('paracetamol.csv', index_col='Date', parse_dates=True)
    elif medicine_type == 2:
        df = pd.read_csv('vitaminc.csv', index_col='Date', parse_dates=True)
    else:
        df = pd.read_csv('amoxicillin.csv', index_col='Date', parse_dates=True)
    df.index.freq = 'MS'
    df.head()
    # df.plot(figsize=(12, 6))

    results = seasonal_decompose(df['sale'])
    # results.plot()

    # plt.plot(results)
    # plt.show()

    print(df)

    train = df.iloc[:174]
    # test = df.iloc[160:]
    scaler = MinMaxScaler()

    scaler.fit(train)
    scaled_train = scaler.transform(train)
    # scaled_test = scaler.transform(test)

    n_features = 1
    n_input = 12
    generator = TimeseriesGenerator(scaled_train, scaled_train, length=n_input, batch_size=1)

    # define model
    model = Sequential()
    model.add(LSTM(100, activation='relu', input_shape=(n_input, n_features)))
    model.add(Dense(1))
    model.compile(optimizer='adam', loss='mse')

    model.summary()
    model.fit(generator, epochs=25)

    loss_per_epoch = model.history.history['loss']
    # plt.plot(range(len(loss_per_epoch)),loss_per_epoch)
    last_train_batch = scaled_train[-12:]
    last_train_batch = last_train_batch.reshape((1, n_input, n_features))
    model.predict(last_train_batch)

    test_predictions = []

    first_eval_batch = scaled_train[-n_input:]
    current_batch = first_eval_batch.reshape((1, n_input, n_features))

    # print("length is " + str(len(test)))

    value = int(month_count)

    for i in range(value):
        # get the prediction value for the first batch
        current_pred = model.predict(current_batch)[0]

        # append the prediction into the array
        test_predictions.append(current_pred)

        # use the prediction to update the batch and remove the first value
        current_batch = np.append(current_batch[:, 1:, :], [[current_pred]], axis=1)

    # test.head()
    true_predictions = scaler.inverse_transform(test_predictions)

    # test_data = np.array(test['sale'].values)
    # prediction_data = np.array(true_predictions.reshape(-1))
    #
    # mse = mean_squared_error(test_data, prediction_data)
    # print("MSE :" + str(mse))
    # mae = mean_absolute_error(test_data, prediction_data)
    # print("MAE :" + str(mae))
    # mape = mean_absolute_percentage_error(test_data, prediction_data)
    # print("MAPE :" + str(mape))



    tt = true_predictions.reshape(-1)
    # following codes for plotting
    # tt = true_predictions.reshape(-1)
    # xx = train['sale'].values
    # yy = df['sale'].values
    # main_data_set = np.array(yy)
    # train_data_set = np.array(xx)
    # predicted_data_set = np.array(tt)
    # all_data = np.append(train_data_set, predicted_data_set)
    # plt.plot(all_data)
    # plt.plot(main_data_set)
    # plt.show()

    data = []
    i = 0
    for x in tt:
        obj = {'index': i, 'value': x}
        i = i + 1
        data.append(obj)

    return data
