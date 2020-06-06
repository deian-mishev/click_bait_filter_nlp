from pymongo import MongoClient

from http.server import HTTPServer, BaseHTTPRequestHandler, SimpleHTTPRequestHandler
from io import BytesIO
import socketserver
import os

# DB
CLIENT = MongoClient('mongodb://127.0.0.1')
MONGODB_RUNTIME = CLIENT[os.environ['MONGODB_RUNTIME']].datamodels
MONGODB_POSITIVE = CLIENT[os.environ['MONGODB_POSITIVE']].datamodels
MONGODB_NEGATIVE = CLIENT[os.environ['MONGODB_NEGATIVE']].datamodels

with open('../public/found.html', 'r') as myfile:
    HTML_FOUND = myfile.read()
    myfile.close()
with open('../public/not_found.html', 'r') as myfile:
    HTML_NOT_FOUND = myfile.read()
    myfile.close()

# Because the class gets destroyed
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

        if (src[0] and 'first' in src[0]):
            el = src[0]['first']
            html = HTML_FOUND.format(
                src=el['url'], tf_score=el['tf_score'])

            global PROCESSED_EL
            PROCESSED_EL = el
            PROCESSED_EL['domain'] = src[0]['domain']
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
        if body == 'ClickBait':
            del PROCESSED_EL['domain']
            MONGODB_POSITIVE.update(
                {'domain': domain},
                {'$push': {"links": PROCESSED_EL}},
                upsert=True
            )
        elif body == 'Not+ClickBait':
            del PROCESSED_EL['domain']
            MONGODB_NEGATIVE.update(
                {'domain': domain},
                {'$push': {"links": PROCESSED_EL}},
                upsert=True
            )
        MONGODB_RUNTIME.update_one(
            {'domain': domain}, {'$pull': {'links': {'url': PROCESSED_EL['url']}}})
        self.do_GET()


httpd = HTTPServer(('localhost', 8000), MyHttpRequestHandler)
httpd.serve_forever()
