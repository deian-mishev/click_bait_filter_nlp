from keras import models
from keras import layers
from keras import optimizers
import matplotlib.pyplot as plt
import numpy as np
import json


def vectorize_sequences(sequences, dimension=10000):
    results = np.zeros((len(sequences), dimension))
    for i, sequence in enumerate(sequences):
        results[i, sequence] = 1.
    return results


def fillSet(unique, filename):
    with open(filename) as json_file:
        data = np.array(json.load(json_file))
        temp = np.unique(np.concatenate(data))
        unique = np.concatenate([temp, unique])
        return data, np.unique(unique)


def strToSetIndex(out_data_set, out_data):
    return np.array([[out_data_set[inel] for inel in el]
                     for el in out_data])


# Fetching Data and indexes
out_data_set = np.array([])
out_data_positive_raw, out_data_set = fillSet(
    out_data_set, 'out_data_positive.json')
out_data_negative_raw, out_data_set = fillSet(
    out_data_set, 'out_data_negative.json')
out_data_set = {out_data_set[i]: i for i in range(0, len(out_data_set))}

# To indexes
out_data_positive = strToSetIndex(
    out_data_set, out_data_positive_raw)
out_data_negative = strToSetIndex(
    out_data_set, out_data_negative_raw)

# Mixing
train_data = np.concatenate((
    out_data_positive, out_data_negative))
np.random.seed(1)
np.random.shuffle(train_data)

train_labels = np.concatenate((
    np.zeros(len(out_data_positive)),
    np.ones(len(out_data_negative))), axis=0)
np.random.seed(0)
np.random.shuffle(train_labels)

# Vectorizing
x_train = vectorize_sequences(train_data)
y_train = np.asarray(train_labels).astype('float32')

# Living training samples
aside = 100
x_val = x_train[:aside]
partial_x_train = x_train[aside:]
y_val = y_train[:aside]
partial_y_train = y_train[aside:]

# Model
model = models.Sequential()
model.add(layers.Dense(16, activation='relu', input_shape=(10000,)))
model.add(layers.Dense(16, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(optimizer='rmsprop', loss='binary_crossentropy',
              metrics=['acc'])


history = model.fit(partial_x_train, partial_y_train,
                    epochs=12,
                    batch_size=100,
                    validation_data=(x_val, y_val))

# Plotting the loss
history_dict = history.history

acc_values = history_dict['acc']
val_acc_values = history_dict['val_acc']
loss_values = history_dict['loss']
val_loss_values = history_dict['val_loss']

epochs = range(1, len(acc_values) + 1)

plt.plot(epochs, loss_values, 'bo', label='Training loss')
plt.plot(epochs, val_loss_values, 'b', label='Validation loss')

plt.title('Training and validation loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.show()

plt.clf()
acc_values = history_dict['acc']
val_acc_values = history_dict['val_acc']
plt.plot(epochs, acc_values, 'bo', label='Training acc')
plt.plot(epochs, val_acc_values, 'b', label='Validation acc')
plt.title('Training and validation accuracy')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.show()
