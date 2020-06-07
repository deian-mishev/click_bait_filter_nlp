from utils import fillSet, strToSetIndex, vectorize_sequences
from model import save_checkpoints, save_model
from validate import validated_rand, plot_results
from db import remove_db_duplicates

from keras import layers
from keras import models
import subprocess
import numpy as np
import os

# REMOVING DB DUPLICATES
remove_db_duplicates()

# Exporting data
process = subprocess.run(
    ['./get_data.sh', os.environ['MONGODB_POSITIVE']], cwd=r'./../')
process = subprocess.run(
    ['./get_data.sh', os.environ['MONGODB_NEGATIVE']], cwd=r'./../')

# Fetching Data and indexes
out_data_set = np.array([])
out_data_positive_raw, out_data_set = fillSet(
    out_data_set, '../data/click_bait_db.json')
out_data_negative_raw, out_data_set = fillSet(
    out_data_set, '../data/click_bait_db_negative.json')
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
seedInt = np.random.randint(0, 100)
train_data = np.concatenate((
    out_data_positive,
    out_data_negative))
np.random.seed(seedInt)
np.random.shuffle(train_data)

train_labels = np.concatenate((
    np.ones(len(out_data_positive)),
    np.zeros(len(out_data_negative))))
np.random.seed(seedInt)
np.random.shuffle(train_labels)

# Vectorizing Data and Labels
x_train = vectorize_sequences(train_data)
y_train = np.asarray(train_labels).astype('float32')

# Living training samples
aside = 0
partial_x_train = x_train[aside:]
partial_y_train = y_train[aside:]

x_val = x_train[:aside]
y_val = y_train[:aside]


# Model
model = models.Sequential()
model.add(layers.Dense(16, activation='relu', input_shape=(3500,)))
model.add(layers.Dense(32, activation='relu'))
model.add(layers.Dense(16, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(optimizer='rmsprop', loss='binary_crossentropy',
              metrics=['acc'])

history = model.fit(partial_x_train, partial_y_train,
                    epochs=10,
                    batch_size=100,
                    # validation_data=(x_val, y_val),
                    callbacks=save_checkpoints(False))

# VALIDATIONS
# print('--------------------------')
# print(model.evaluate(x_val, y_val))
# print('--------------------------')
# plot_results(history)
# validated_rand(model, out_data_set, x_val, train_labels)

save_model(model, out_data_set)
