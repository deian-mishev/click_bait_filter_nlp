import tensorflowjs as tfjs
from keras import callbacks
from shutil import copyfile
import json
import os
import io
import random
import numpy as np


def split_data(SOURCE, TRAINING, TESTING, SPLIT_SIZE):
    files = os.listdir(SOURCE)
    filesS = len(files)
    random.sample(files, filesS)
    a = int(filesS*SPLIT_SIZE)
    for i in files[:a]:
        copyfile(os.path.join(SOURCE, i), os.path.join(TRAINING, i))
    for i in files[a:]:
        copyfile(os.path.join(SOURCE, i), os.path.join(TESTING, i))


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


def save_embeddings(model, reverse_word_index, vocab_size):
    weights = model.layers[0].get_weights()[0]
    out_v = io.open('../emb/vecs.tsv', 'w', encoding='utf-8')
    out_m = io.open('../emb/meta.tsv', 'w', encoding='utf-8')
    for word_num in range(1, vocab_size):
        word = reverse_word_index[word_num]
        embeddings = weights[word_num]
        out_m.write(word + "\n")
        out_v.write('\t'.join([str(x) for x in embeddings]) + "\n")
    out_v.close()
    out_m.close()


def teach_model_k_fold(model, x_train, y_train, folds, epochs, batch_size, save_points=False):
    avg_history = None
    all_scores = []
    num_value_samples = len(x_train) // folds
    for i in range(folds):
        print('processing fold #', i + 1)
        val_data = x_train[i *
                           num_value_samples: (i + 1) * num_value_samples]
        val_targets = y_train[i *
                              num_value_samples: (i + 1) * num_value_samples]

        partial_train_data = np.concatenate(
            [x_train[:i * num_value_samples],
             x_train[(i + 1) * num_value_samples:]], axis=0)
        partial_train_targets = np.concatenate(
            [y_train[:i * num_value_samples],
             y_train[(i + 1) * num_value_samples:]], axis=0)

        history = model.fit(partial_train_data, partial_train_targets,
                            epochs=epochs,
                            batch_size=batch_size,
                            validation_data=(val_data, val_targets),
                            callbacks=save_checkpoints(save_points),
                            verbose=1)

        if not avg_history:
            avg_history = history
        else:
            for key in history.history:
                for epoch in range(epochs):
                    avg_history.history[key][epoch] += history.history[key][epoch]

        val_mse, val_mae = model.evaluate(val_data, val_targets, verbose=0)
        all_scores.append(val_mae)

    for key in avg_history.history:
        avg_history.history[key] = [avg_history.history[key][i]/folds
                                    for i in range(epochs)]

    return avg_history, model, all_scores


def teach_model_hold_out(model, x_train, y_train, aside, epochs, batch_size, save_points=False):
    partial_x_train = x_train[aside:]
    partial_y_train = y_train[aside:]

    x_val = x_train[:aside]
    y_val = y_train[:aside]

    history = model.fit(partial_x_train, partial_y_train,
                        epochs=epochs,
                        batch_size=batch_size,
                        validation_data=(x_val, y_val),
                        callbacks=save_checkpoints(save_points),
                        verbose=1)

    return history, model, model.evaluate(x_val, y_val, verbose=0)
