#!/bin/sh
mongoexport \
    --host localhost \
    --db temp \
    --collection datamodels \
    --out data.json \
    --jsonArray \
    --pretty \
    --query '{"links": { "$ne": null }}'

node > src/temp.json << EOF
var data = require('./data.json');
var entries = [];
    data.forEach(a => {
    a.links.forEach(a => {
        var found = a.url.match(/\/(?=[^/]*$)(.*?)(\.|\?|$)/);
        if(found) {
            var el = found[1].split('-').filter(Boolean);
            if(el.length > 2) { 
                entries.push(el);
            }
        }
    });
});
console.log(JSON.stringify(entries));
EOF
rm -rf ./data.json