
import matplotlib.pyplot as plt
import numpy as np
from keras.datasets import mnist
from keras.utils import to_categorical
from keras.models import load_model

from keras import models
from keras import layers
from random import randint
(train_images, train_labels), (test_images, test_labels) = mnist.load_data()

network = models.Sequential()
network.add(layers.Dense(512, activation='relu', input_shape=(28 * 28,)))
network.add(layers.Dense(10, activation='softmax'))
network.compile(optimizer='rmsprop', loss='categorical_crossentropy',
                metrics=['accuracy'])

train_images = train_images.reshape((60000, 28 * 28))
train_images = train_images.astype('float32') / 255

val_images = test_images
# test_images = test_images.reshape((10000, 28 * 28))
# test_images = test_images.astype('float32') / 255

# train_labels = to_categorical(train_labels)
# test_labels = to_categorical(test_labels)

# network.fit(train_images, train_labels, epochs=10, batch_size=128)
# Export the model to a SavedModel
# network.save('./mnimist_model')


# Recreate the exact same model
new_model = load_model('./mnimist_model')

# Check that the state is preserved
for x in np.random.randint(6000, size=10):
    digit = val_images[x]
    plt.imshow(digit, cmap=plt.cm.binary)
    plt.show()

    digit = digit.reshape(1, 28 * 28)
    digit = digit.astype('float32') / 255

    new_predictions = new_model.predict(digit)
    print(new_predictions)
    pass

# Regression Example With Boston Dataset: Standardized and Wider
# from pandas import read_csv
# from keras.models import Sequential
# from keras.layers import Dense
# from keras.wrappers.scikit_learn import KerasRegressor
# from sklearn.model_selection import cross_val_score
# from sklearn.model_selection import KFold
# from sklearn.preprocessing import StandardScaler
# from sklearn.pipeline import Pipeline
# # load dataset
# dataframe = read_csv("housing.data.txt", delim_whitespace=True, header=None)
# dataset = dataframe.values
# # split into input (X) and output (Y) variables
# X = dataset[:,0:13]
# Y = dataset[:,13]
# # define wider model
# def wider_model():
# 	# create model
# 	model = Sequential()
# 	model.add(Dense(20, input_dim=13, kernel_initializer='normal', activation='relu'))
# 	model.add(Dense(1, kernel_initializer='normal'))
# 	# Compile model
# 	model.compile(loss='mean_squared_error', optimizer='adam')
# 	return model
# # evaluate model with standardized dataset
# estimators = []
# estimators.append(('standardize', StandardScaler()))
# estimators.append(('mlp', KerasRegressor(build_fn=wider_model, epochs=100, batch_size=5, verbose=0)))
# pipeline = Pipeline(estimators)
# kfold = KFold(n_splits=10)
# results = cross_val_score(pipeline, X, Y, cv=kfold)
# print("Wider: %.2f (%.2f) MSE" % (results.mean(), results.std()))
