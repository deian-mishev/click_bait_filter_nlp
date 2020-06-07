import os

from pymongo import MongoClient
CLIENT = MongoClient('mongodb://127.0.0.1')


def remove_db_duplicates():
    MONGODB_POSITIVE, MONGODB_NEGATIVE = get_model_dbs()
    positive_data = list(MONGODB_POSITIVE.find({}))
    negative_data = list(MONGODB_NEGATIVE.find({}))
    for p_d in positive_data:
        for n_d in negative_data:
            if p_d['domain'] == n_d['domain']:
                # SAME DOMAINS
                for p_l in p_d['links']:
                    for n_l in n_d['links']:
                        if p_l == n_l:
                            MONGODB_POSITIVE.update_one(
                                {'domain': p_d['domain']}, {'$pull': {'links': {'url': p_l}}})
                            MONGODB_NEGATIVE.update_one(
                                {'domain': n_d['domain']}, {'$pull': {'links': {'url': n_l}}})


def get_runtime_db():
    return CLIENT[os.environ['MONGODB_RUNTIME']].datamodels


def get_model_dbs():
    MONGODB_POSITIVE = CLIENT[os.environ['MONGODB_POSITIVE']].datamodels
    MONGODB_NEGATIVE = CLIENT[os.environ['MONGODB_NEGATIVE']].datamodels
    return MONGODB_POSITIVE, MONGODB_NEGATIVE
