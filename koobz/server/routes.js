/**
 * Main application routes
 */

'use strict';

var errors = require('./components/errors');
var request = require('request');

module.exports = function(app) {

  var kanbanApiPath = '/api/kanban';
  app.all('/api/kanban*', function(req, res, next) {
    var method, r;
    method = req.method.toLowerCase();
    var url = req.url.substring(kanbanApiPath.length);
    switch (method) {
      case "get":
        r = request.get({
          uri: 'http://localhost:8080' + url,
          json: req.body
        });
        break;
      case "put":
        r = request.put({
          uri: 'http://localhost:8080' + url,
          json: req.body
        });
        break;
      case "post":
        r = request.post({
          uri: 'http://localhost:8080' + url,
          json: req.body
        });
        break;
      case "delete":
        r = request.del({
          uri: 'http://localhost:8080' + url,
          json: req.body
        });
        break;
      default:
        return res.send("invalid method");
    }
    return req.pipe(r).pipe(res);
  });

  // All undefined asset or api routes should return a 404
  app.route('/:url(api|auth|components|app|bower_components|assets)/*')
   .get(errors[404]);

  // All other routes should redirect to the index.html
  app.route('/*')
    .get(function(req, res) {
      res.sendfile(app.get('appPath') + '/index.html');
    });
};
