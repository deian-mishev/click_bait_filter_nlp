import tensorflowjs as tfjs
from keras import callbacks
import json
import os
import numpy as np


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


def teach_model_k_fold(model, x_train, y_train, folds, epochs, batch_size, save_points=False):
    all_acc_history = []
    all_scores = []
    num_value_samples = len(x_train) // folds
    for i in range(folds):
        print('processing fold #', i)
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

        acc_history = history.history['loss']
        all_acc_history.append(acc_history)
        val_mse, val_mae = model.evaluate(val_data, val_targets, verbose=0)
        all_scores.append(val_mae)

    average_acc_history = [np.mean([x[i] for x in all_acc_history])
                           for i in range(epochs)]

    return average_acc_history, model, all_scores


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
