var app = require('express')();
var htpp = require('http').Server(app);
var io = require('socket.io')(http);
