import os
import random
from http.server import HTTPServer, SimpleHTTPRequestHandler
from utils import response_html
from db import get_model_dbs, get_runtime_db

# DB
MONGODB_RUNTIME = get_runtime_db()
MONGODB_POSITIVE, MONGODB_NEGATIVE = get_model_dbs()

# HTML
HTML_FOUND, HTML_NOT_FOUND = response_html()

PROCESSED_EL = ''


class MyHttpRequestHandler(SimpleHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")

        self.end_headers()

        src = list(MONGODB_RUNTIME.aggregate([
            {
                '$project':
                {
                    'domain': 1,
                    'first': {'$arrayElemAt': ['$links', 0]}
                }
            }
        ]))

        if (len(src) > 0):
            randDomainInt = random.randint(0, len(src) - 1)
            if ('first' in src[randDomainInt]):
                el = src[randDomainInt]['first']
                html = HTML_FOUND.format(
                    src=el['url'], tf_score=el['tf_score'])

                global PROCESSED_EL
                PROCESSED_EL = el
                PROCESSED_EL['domain'] = src[randDomainInt]['domain']
            else:
                MONGODB_RUNTIME.delete_one({
                    'domain': src[0]['domain']
                })
                html = HTML_NOT_FOUND
        else:
            html = HTML_NOT_FOUND

        self.wfile.write(bytes(html, "utf8"))
        return

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        body = self.rfile.read(content_length).decode(
            "utf-8").replace('action=', '')
        global PROCESSED_EL
        domain = PROCESSED_EL['domain']
        del PROCESSED_EL['domain']
        del PROCESSED_EL['tf_score']

        if body == 'CLICKBAIT':
            MONGODB_POSITIVE.update(
                {'domain': domain},
                {'$addToSet': {"links": PROCESSED_EL}},
                upsert=True
            )
        elif body == 'NOTCLICKBAIT':
            MONGODB_NEGATIVE.update(
                {'domain': domain},
                {'$addToSet': {"links": PROCESSED_EL}},
                upsert=True
            )
        MONGODB_RUNTIME.update_one(
            {'domain': domain}, {'$pull': {'links': {'url': PROCESSED_EL['url']}}})
        self.do_GET()


httpd = HTTPServer(
    ('localhost', int(os.environ['PORT'])), MyHttpRequestHandler)
httpd.serve_forever()
