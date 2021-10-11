from math import sqrt

import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from sklearn.metrics import mean_squared_error


df = pd.read_csv('paracetamol.csv', index_col='Date', parse_dates=True)
print(df)
xyz = df['sale'].values
test = df.iloc[160:]
print(xyz)

arr1 = np.array([[1, 2], [3, 4]])
arr2 = np.array([[10, 20], [30, 40]])
#arr2 = np.array(xyz)

# no axis provided, array elements will be flattened
arr_flat = np.append(arr1, arr2)

print(arr_flat)
rmse = sqrt(mean_squared_error(arr1,arr2))
print(rmse)

plt.plot(arr_flat)
plt.show()
