var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

io.on('connection', function(socket) {
	console.log("socket " + socket.id + " has connected");

	// event `queue_for_game` occurs when player has selected the course they want
	// to be quizzed on, emitted with the corresponding `course_code`
	socket.on('queue_for_game', function(queue_info_json) {
		var queue_info = JSON.parse(queue_info_json);
		socket.username = queue_info.username;
		socket.course = queue_info.course;
		socket.player_json = queue_info_json;

		// all players who want to play in the same course are put in the same room
		var course_room_name = 'RM' + queue_info.course;
		socket.join(course_room_name);
		console.log(queue_info.username + ' has joined ' + course_room_name);

		// a match can happen if the number of people in the room is even and not 0
		var room_population = io.nsps['/'].adapter.rooms[course_room_name].length;
		if((room_population % 2 == 0) && (room_population != 0)) {
			console.log('found a match in ' + course_room_name);

			// connect the two players in the room by making them join a private room
			// also make them leave the current waiting room
			var p1 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[0];
			var p2 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[1];
			var priv_room_name = 'RM' + p1 + p2;
			var p1_socket = io.nsps['/'].connected[p1];
			p1_socket.join(priv_room_name);
			var p2_socket = io.nsps['/'].connected[p2];
			p2_socket.join(priv_room_name);
			io.in(priv_room_name).emit('game_made', p1_socket.username + ' ' + p2_socket.username);


			console.log('game made by ' + p1_socket.username + ' and ' + p2_socket.username);

			io.nsps['/'].connected[p1].leave(course_room_name);
		    io.nsps['/'].connected[p2].leave(course_room_name);
		    console.log('sockets ' + p1 + ' and ' + p2 + 'have left ' + course_room_name);

		}

		// remove the player from the waiting room - happens when they want to leave or client times them out
		socket.on('stop_waiting', function(msg) {
			socket.leave(course_room_name);
		});

		socket.on('send_json_opponent',function(msg) {
			socket.broadcast.emit('get_json_opponent',socket.player_json);
		});

	});
});




http.listen(3300, function() {
	console.log('listening on *:3300');
});