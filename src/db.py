import os

from pymongo import MongoClient
LOCAL_CLIENT = MongoClient(os.environ['MONGO_CLIENT'])


def remove_same_group_duplicated(groupData, db):
    for data in groupData:
        for data_else in (data_else for data_else in groupData if data_else['domain'] != data['domain']):
            for p_l in data['links']:
                for n_l in data_else['links']:
                    if p_l['url'] == n_l['url']:
                        db.update_one(
                            {'domain': data['domain']}, {'$pull': {'links': {'url': p_l['url']}}})
                        db.update_one(
                            {'domain': data_else['domain']}, {'$pull': {'links': {'url': n_l['url']}}})


def remove_mixxed_duplicated(data1, data2, db1, db2):
    for p_d in data1:
        for n_d in data2:
            # SAME DOMAINS
            # if p_d['domain'] == n_d['domain']:
            for p_l in p_d['links']:
                for n_l in n_d['links']:
                    if p_l['url'] == n_l['url']:
                        db1.update_one(
                            {'domain': p_d['domain']}, {'$pull': {'links': {'url': p_l['url']}}})
                        db2.update_one(
                            {'domain': n_d['domain']}, {'$pull': {'links': {'url': n_l['url']}}})


def remove_db_duplicates():
    MONGODB_POSITIVE, MONGODB_NEGATIVE = get_model_dbs()
    positive_data = list(MONGODB_POSITIVE.find({}))
    negative_data = list(MONGODB_NEGATIVE.find({}))
    remove_same_group_duplicated(positive_data, MONGODB_POSITIVE)
    remove_same_group_duplicated(negative_data, MONGODB_NEGATIVE)
    remove_mixxed_duplicated(positive_data, negative_data,
                             MONGODB_POSITIVE, MONGODB_NEGATIVE)


def get_runtime_db():
    if 'MONGO_CLIENT_REMOTE' in os.environ:
        REMOTE_CLIENT = MongoClient(os.environ['MONGO_CLIENT_REMOTE'])
        return REMOTE_CLIENT[os.environ['MONGODB_RUNTIME']].datamodels
    else:
        return LOCAL_CLIENT[os.environ['MONGODB_RUNTIME']].datamodels


def get_model_dbs():
    MONGODB_POSITIVE = LOCAL_CLIENT[os.environ['MONGODB_POSITIVE']].datamodels
    MONGODB_NEGATIVE = LOCAL_CLIENT[os.environ['MONGODB_NEGATIVE']].datamodels
    return MONGODB_POSITIVE, MONGODB_NEGATIVE


def findLink(db, domain, link):
    return db.find_one({'domain': domain, 'links.url': link})
