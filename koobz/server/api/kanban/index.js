'use strict';

var express = require('express');
var kanban = require('./kanban.controller');

var router = express.Router();

router.get('/boards*', kanban.proxy);

module.exports = router;
