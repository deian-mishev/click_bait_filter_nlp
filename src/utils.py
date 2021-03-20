from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
import numpy as np
import json
import csv


def get_csv_data(filename):
    images = []
    labels = []
    with open(filename) as training_file:
        reader = csv.reader(training_file, delimiter=',')
        next(reader, None)
        for line in reader:
            images.append(np.array_split(line[1:785], 28))
            labels.append(line[0])
    return np.array(images).astype('float'), np.array(labels).astype('float')


def response_html():
    with open('../public/found.html', 'r') as myfile:
        HTML_FOUND = myfile.read()
        myfile.close()
    with open('../public/not_found.html', 'r') as myfile:
        HTML_NOT_FOUND = myfile.read()
        myfile.close()
    return HTML_FOUND, HTML_NOT_FOUND


def un_vectorize_sequences(sequences):
    results = np.array([])
    for i, el in enumerate(sequences):
        if el > 0:
            results = np.append(results, [i])
    return results


def tokenise_data(pos, neg, dimension):
    posSequence = [' '.join(i) for i in pos]
    negSequence = [' '.join(i) for i in neg]
    tokenizer = Tokenizer(num_words=dimension, oov_token="@@@")
    tokenizer.fit_on_texts(np.concatenate((posSequence, negSequence)))
    return tokenizer.word_index, tokenizer.texts_to_sequences(posSequence), tokenizer.texts_to_sequences(negSequence)


def vectorize_sequences(sequences, dimension):
    results = np.zeros((len(sequences), dimension))
    for i, sequence in enumerate(sequences):
        results[i, sequence] = 1.
    return results


def pad_sequence(sequences, max_length):
    return pad_sequences(sequences, maxlen=max_length, padding='post')


def fetchData(filename):
    with open(filename) as json_file:
        return np.array(json.load(json_file), dtype="object")


def fillSet(unique, filename):
    data = fetchData(filename)
    temp = np.unique(np.concatenate(data))
    unique = np.concatenate([temp, unique])
    return data, np.unique(unique)


def strToSetIndex(out_data_set, out_data):
    return np.array([[out_data_set[inel] for inel in el]
                     for el in out_data])
