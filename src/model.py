import tensorflowjs as tfjs
from keras import callbacks
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
