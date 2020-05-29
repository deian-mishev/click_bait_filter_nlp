
from keras import optimizers
from keras import layers
from keras import models
from keras import callbacks

import matplotlib.pyplot as plt
import numpy as np
import json
import os


def vectorize_sequences(sequences, dimension=5000):
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

# Removing doubles ... happens
for i, A in np.ndenumerate(out_data_positive):
    for n, B in np.ndenumerate(out_data_negative):
        if np.array_equal(A, B):
            out_data_positive = np.delete(out_data_positive, i)
            out_data_negative = np.delete(out_data_negative, n)

# Mixing
train_data = np.concatenate((
    out_data_positive,
    out_data_negative))
np.random.seed(0)
np.random.shuffle(train_data)

train_labels = np.concatenate((
    np.ones(len(out_data_positive)),
    np.zeros(len(out_data_negative))))
np.random.seed(0)
np.random.shuffle(train_labels)

# Verification
# np.random.seed(10)
# a = np.random.randint(0, 399)
# reverse_word_index = dict(
#     [(value, key) for (key, value) in out_data_set.items()])
# decoded_link = ' '.join(
#     [reverse_word_index.get(i, '?') for i in train_data[a]])
# print(decoded_link)
# print(train_labels[a])

# Vectorizing
x_train = vectorize_sequences(train_data)
y_train = np.asarray(train_labels).astype('float32')

# Living training samples
aside = 100
x_val = x_train[:aside]
partial_x_train = x_train[aside:]
y_val = y_train[:aside]
partial_y_train = y_train[aside:]
# Checkpoints setup

checkpoint_path = "../checkpoints/cp.ckpt"
checkpoint_dir = os.path.dirname(checkpoint_path)
cp_callback = callbacks.ModelCheckpoint(filepath=checkpoint_path,
                                        save_weights_only=True,
                                        verbose=1)


# Model
model = models.Sequential()
model.add(layers.Dense(16, activation='relu', input_shape=(5000,)))
model.add(layers.Dense(32, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(optimizer='rmsprop', loss='binary_crossentropy',
              metrics=['acc'])

history = model.fit(partial_x_train, partial_y_train,
                    epochs=13,
                    batch_size=100,
                    validation_data=(x_val, y_val),
                    callbacks=[cp_callback])

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

# Plotting the actual training
plt.plot(epochs, acc_values, 'bo', label='Training acc')
plt.plot(epochs, val_acc_values, 'b', label='Validation acc')
plt.title('Training and validation accuracy')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.show()

model.save('../model/model')
with open('../mapping/mapping.json', 'w') as fp:
    json.dump(out_data_set, fp, sort_keys=True, indent=4)
