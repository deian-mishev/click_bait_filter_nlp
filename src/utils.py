import matplotlib.pyplot as plt
import tensorflowjs as tfjs
from keras import callbacks

import numpy as np
import json
import os


def save_checkpoints(should_save=True):
    # Checkpoints setup
    if should_save:
        checkpoint_path = "../checkpoints/cp.ckpt"
        checkpoint_dir = os.path.dirname(checkpoint_path)
        cp_callback = callbacks.ModelCheckpoint(filepath=checkpoint_path,
                                                save_weights_only=True,
                                                verbose=1)
        return [cp_callback]
    else:
        return None


def save_model(model, out_data_set):
    tfjs.converters.save_keras_model(model, '../model')
    with open('../model/mapping.json', 'w') as fp:
        json.dump(out_data_set, fp, sort_keys=True, indent=4)


def validated_rand(model, out_data_set, valSet, train_labels, times=10):
    reverse_word_index = dict(
        [(value, key) for (key, value) in out_data_set.items()])
    fig, ax = plt.subplots()

    fig.patch.set_visible(False)
    ax.axis('off')
    ax.axis('tight')

    collabel = ("cont", "flag", "res")
    clust_data = np.empty((0, 3), int)

    for i in range(times):
        a = np.random.randint(0, len(valSet))
        sample = valSet[a]
        decoded_link = ' '.join([reverse_word_index.get(
            i, '?') for i in un_vectorize_sequences(sample)])

        clust_data = np.concatenate((clust_data,
                                     [[
                                         decoded_link,
                                         int(train_labels[a]),
                                         '{:.2f}'.format(model.predict(
                                             np.array([valSet[a]]))[0][0])
                                     ]]))

    the_table = ax.table(
        cellText=clust_data,
        colLabels=collabel,
        colWidths=[0.8, 0.1, 0.1],
        loc='center')
    the_table.auto_set_font_size(False)
    the_table.set_fontsize(6)
    plt.show()


def plot_results(history, loss=True, training=True):
    history_dict = history.history

    acc_values = history_dict['acc']
    val_acc_values = history_dict['val_acc']

    loss_values = history_dict['loss']
    val_loss_values = history_dict['val_loss']

    epochs = range(1, len(acc_values) + 1)

    if loss:
        plt.plot(epochs, loss_values, 'bo', label='Training loss')
        plt.plot(epochs, val_loss_values, 'b', label='Validation loss')

        plt.title('Training and validation loss')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()
        plt.show()
        plt.clf()

    if training:
        plt.plot(epochs, acc_values, 'bo', label='Training acc')
        plt.plot(epochs, val_acc_values, 'b', label='Validation acc')
        plt.title('Training and validation accuracy')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()
        plt.show()


def vectorize_sequences(sequences, dimension=3500):
    results = np.zeros((len(sequences), dimension))
    for i, sequence in enumerate(sequences):
        results[i, sequence] = 1.
    return results


def un_vectorize_sequences(sequences):
    results = np.array([])
    for i, el in enumerate(sequences):
        if el > 0:
            results = np.append(results, [i])
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
