import matplotlib.pyplot as plt
import numpy as np
from utils import un_vectorize_sequences


def validated_rand(model, out_data_set, valSet, train_labels):
    reverse_word_index = dict(
        [(value, key) for (key, value) in out_data_set.items()])
    fig, ax = plt.subplots()

    fig.patch.set_visible(False)
    ax.axis('off')
    ax.axis('tight')

    collabel = ("cont", "flag", "res")
    clust_data = np.empty((0, 3), int)
    times = len(train_labels)
    for i in range(times):
        sample = valSet[i]
        decoded_link = ' '.join([reverse_word_index.get(
            i, '?') for i in un_vectorize_sequences(sample)])

        clust_data = np.concatenate((clust_data,
                                     [[
                                         decoded_link,
                                         int(train_labels[i]),
                                         '{:.4f}'.format(model.predict(
                                             np.array([valSet[i]]))[0][0])
                                     ]]))

    the_table = ax.table(
        cellText=clust_data,
        colLabels=collabel,
        colWidths=[0.8, 0.1, 0.1],
        loc='center')
    the_table.auto_set_font_size(False)
    the_table.set_fontsize(6)
    plt.show()


def plot_k_fold_res(history):
    plt.plot(range(1, len(history) + 1), history)
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.show()


def plot_hold_out_res(history, loss=True, training=True):
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
