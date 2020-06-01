#!/bin/sh
mongoexport \
    --host localhost \
    --db click_bait_db \
    --collection datamodels \
    --out data.json \
    --jsonArray \
    --pretty \
    --query '{"links": { "$ne": null }}'

node > src/out_data_positive.json << EOF
var data = require('./data.json');
var letters = /^[a-z]+$/;
var link = /\/(?=[^/]*$)(.*?)(\.|\?|$)/;
var entries = [];
data.forEach(a => {
    a.links.forEach(a => {
        var found = a.url.match(link);
        if(found) {
            var el = found[1].split('-')
                .filter(Boolean)
                .filter(a => a.match(letters))
            if(el.length >= 2) { 
                entries.push(el);
            }
        }
    });
});
console.log(JSON.stringify(entries));
EOF

mongoexport \
    --host localhost \
    --db click_bait_db_negative \
    --collection datamodels \
    --out data.json \
    --jsonArray \
    --pretty \
    --query '{"links": { "$ne": null }}'

node > src/out_data_negative.json << EOF
var data = require('./data.json');
var letters = /^[a-z]+$/;
var link = /\/(?=[^/]*$)(.*?)(\.|\?|$)/;
var entries = [];
data.forEach(a => {
    a.links.forEach(a => {
        var found = a.url.match(link);
        if(found) {
            var el = found[1].split('-')
                .filter(Boolean)
                .filter(a => a.match(letters))
            if(el.length >= 2) { 
                entries.push(el);
            }
        }
    });
});
console.log(JSON.stringify(entries));
EOF

rm -rf ./data.json