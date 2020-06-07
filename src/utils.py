
import numpy as np
import json


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


def vectorize_sequences(sequences, dimension=3500):
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
