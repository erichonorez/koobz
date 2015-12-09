'use strict';

var _ = require('lodash');
var request = require('request');

// Get list of things
exports.proxy = function(req, res) {
  var backend = 'http://localhost:8080';
  console.log(backend + req.url);
  request(backend + req.url).pipe(res);
};
