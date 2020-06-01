#!/bin/sh
mongoexport \
    --host localhost \
    --db temp \
    --collection datamodels \
    --out data.json \
    --jsonArray \
    --pretty \
    --query '{"links": { "$ne": null }}'

node > data/temp.json << EOF
const data = require('./data.json');
const { getUrl } 
    = require('../click_bait_filter_be/api/url_get');

const entries = [];
data.forEach(a => {
    a.links.forEach(a => {
        const el = getUrl(a.url);
        if (el) {
            entries.push(el);
        }
    });
});
console.log(JSON.stringify(entries));
EOF

rm -rf ./data.json