var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var mongoose = require('mongoose');
var bodyParser = require('body-parser');

const jwt = require('express-jwt');
const jwtAuthz = require('express-jwt-authz');
const jwksRsa = require('jwks-rsa');


var dbHost = 'mongodb://localhost:27017/TestBenchDB';
mongoose.connect(dbHost, { useNewUrlParser: true });
var User = require('./models/user.js');
var Question = require('./models/question.js');
var Course = require('./models/course.js');

mongoose.set('useCreateIndex', true);
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());


var db = mongoose.connection;

var ObjectId = require('mongodb').ObjectID;

const checkJwt = jwt({
  // Dynamically provide a signing key
  // based on the kid in the header and 
  // the signing keys provided by the JWKS endpoint.
  secret: jwksRsa.expressJwtSecret({
    cache: true,
    rateLimit: true,
    jwksRequestsPerMinute: 5,
    jwksUri: `https://test-bench.auth0.com/.well-known/jwks.json`
  }),

  // Validate the audience and the issuer.
  audience: 'iuucDr9fKeqptDK_r2AApYEy9vksGlUF',
  issuer: `https://test-bench.auth0.com`,
  algorithms: ['RS256']
});

// app.get('/api/user/', function(req, res) {
// 	//var name = req.query.name;
// 	res.json(req);
// 	// User.find({ name: name}, function (err, docs) {
// 	// 	if(err) throw err;
// 	// 	res.json(docs);
//  //    });
//  });
/////////////////////////////////////////////////////////////////////////
//get user by id
app.get('/api/user/', function(req, res) {
	User.getUserById(req.query._id, function(err, doc) {
		if(err){
			throw err;
		}
		res.json(doc);
	});
});

//get user by email
app.get('/api/user/email/', function(req, res) {
	console.log('getting user by email');
	User.getUserByEmail(req.query.email, function(err, doc) {
		if(err){
			throw err;
		}
		res.json(doc);
	});
});

//create new user
app.post('/api/user', function(req, res) {

	console.log('create a new user');
    var user = new User( {
    name : req.body.name,
    email : req.body.email,
    profile_photo_id : req.body.profile_photo_id,
    is_professor: false,
    reported: false
    });
    user.save(function(err, result) {
      if ( err ) throw err;
      res.json( {
        message:"Successfully added user",
        user:result
      });
    });
});

//create new question
app.post('/api/question', function(req, res) {

	console.log('create a new question');
    var question = new Question( {
		question_text: req.body.question_text,
		correct_answer: req.body.correct_answer,
		incorrect_answer_1: req.body.incorrect_answer_1,
		incorrect_answer_2: req.body.incorrect_answer_2,
		incorrect_answer_3: req.body.incorrect_answer_3,
		courseID: req.body.courseID,
		difficulty: null,
		creator_uID: req.body.userID,
		verified: req.body.verified,
		reported: false
    });
    question.save(function(err, result) {
      if ( err ) throw err;
      res.json( {
        message:"Successfully added question",
        user:result
      });
    });
});

//test question randomizer
app.get('/api/getgame/', function(req, res) {
    var courseID = req.query.courseID;
    console.log(courseID);
	Question.getGameQuestions(courseID, function(err, game) {
		if(err){
			throw err;
		}
		res.json(game);
	});
});

/////////////////////////////////////////////////////////
//delete user by email
app.delete('/api/user/email/:email', function(req, res) {
	var email = req.params.email;
	User.removeUserByEmail(email, function(err, book) {
		if(err){
			throw err;
		}
		res.json(book);
	});
});

//edit user profile by email
app.put('/api/user/email/:email', function(req, res) {
	var email = req.params.email;

    // var name = ;
    // var profile_photo_id = ;
	User.updateUserByEmail(email, {
		name: req.body.name,
		profile_photo_id: req.body.profile_photo_id
	}, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

//delete user by id
app.delete('/api/user/:_id', function(req, res) {
	var id = req.params._id;
	User.removeUser(id, function(err, book) {
		if(err){
			throw err;
		}
		res.json(book);
	});
});

//edit user profile by id
app.put('/api/user/:_id', function(req, res) {
	var id = req.params._id;

    // var name = ;
    // var profile_photo_id = ;
	User.updateUser(id, {
		name: req.body.name,
		profile_photo_id: req.body.profile_photo_id
	}, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

app.get('/api/course/:course_subject/', function(req, res) {
	Course.getCourse({course_number:req.query.course_number,course_subject:req.params.course_subject}, function(err, doc) {
	if(err){
		throw err;
	} 
	res.json(doc);
	console.log(doc);
	});
});

app.get('/api/course/', function(req, res) {
	Course.getCourse({course_subject:req.query.course_subject}, function(err, doc) {
	if(err){
		throw err;
	} 
	res.json(doc);
	console.log(doc);
	});
});

app.post('/api/course/', function(req, res) {

    var course = new Course( {
    course_number:req.body.course_number,
    course_subject:req.body.course_subject
    });
    course.save(function(err, result) {
      if ( err ) throw err;
      res.json( {
        message:"Successfully added course",
        user:result
      });
    });
});




//get a course
//add a course using courseID
// app.put('/api/addcourse/:email', function(req, res) {
// 	//var id = req.params._id;

//     //var courseID = req.query.courseID;
//     //console.log(courseID);
    
// 	console.log(req.query.course_number);
// 	Course.getCourse({course_number:req.query.course_number,course_subject:req.query.course_subject}, function(err, doc) {
// 	if(err){
// 		throw err;
// 	} else {
// 	var idd= JSON.parse(doc) ;
// 	// callback = function() {
//  //    // Do something with arguments:
//  //    idd = argugments[0];
//  //    console.log('bleh' + idd);
// 	// };
// 	console.log(doc);
// 	User.updateUserByEmail(req.params.email, { $push: { course_list: idd['_id']} }, {}, function(err, user) {
// 		if(err){
// 			throw err;
// 		}
// 		res.json(user);
// 	});
// 	};
// 	//res.json(doc);
// 	console.log(doc);
// 	});
// });

//add a course using courseID using user email
app.put('/api/addcourse/email/:email', function(req, res) {
	var email = req.params.email;
    var courseID = req.body.courseID;
    console.log(courseID);
	User.updateUserByEmail(email, { $push: { course_list: req.body.courseID } }, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

//add a course using courseID using user id
app.put('/api/addcourse/:_id', function(req, res) {
	var id = req.params._id;
    var courseID = req.body.courseID;
    console.log(courseID);
	User.updateUser(id, { $push: { course_list: req.body.courseID } }, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

//add a stats object by email
app.put('/api/addnewstat/email/:email/:courseID', function(req, res) {
	var email = req.params.email;
    var courseID = req.body.courseID;
    console.log(courseID);
	User.updateUserByEmail(email, { $push: {stats_list: {course_code: courseID,rank: 1, 
		avg_response_time: null, correctness_rate:null,num_stat_contributions:0}}}, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});




app.put('/api/addnewstatID/:_id', function(req, res) {
	var id = req.params._id;
    var courseID = req.body.courseID;
    console.log(courseID);
	User.updateUser(id, { $push: {stats_list: {course_code: courseID,rank: 1, 
		avg_response_time: null, correctness_rate:null,num_stat_contributions:0}}}, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});


//LOL this is here for emergency
//delete all stats for a user
app.put('/api/deleteallstats/:_id', function(req, res) {
	var id = req.params._id;
    console.log(courseID);
	User.updateUser(id, { $pull : { stats_list : {correctness_rate : {$in : [null]}} } }, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

//delete a course from a user
//also for emergencies I guess
app.put('/api/deletecourse/:_id/:courseID', function(req, res) {
	var id = req.params._id;
    var courseID = req.params.courseID;
    console.log(courseID);
	User.updateUser(id, { $pull : { course_list : {$in:[ObjectId(courseID)]} }}, {}, function(err, user) {
		if(err){
			throw err;
		}
		res.json(user);
	});
});

//sequence of updating a stat object





////////////////////////////////////////////////////////////////////////////
// app.get('/api/getstats/:email', function(req, res) {
// 	//var id = req.params._id;

//     var courseID = req.query.courseID;
//     var email = req.params.email;

//     // var new_total_rate = req.query.new_total_rate;
//     // var new_total_time = req.query.new_total_time;

//     console.log(courseID);
// 	User.getUserByEmail({email:email,stats_list: {$elemMatch: {course_code: courseID}}}, function(err, user) {
// 		if(err){
// 			throw err;
// 		} else {
// 			res.json(user);
// 		}
// 		//else {
// 			// User.updateUser(email, { $inc: {stats_list.num_stat_contributions:1}}, {}, function(err, user) {
// 			// 	if(err){
// 			// 		throw err;
// 			// 	}
// 			// 	res.json(user);
// 			// });
// 		//}
// 	});



//  });

//get the information from the specific stat object.
//client: use the information to calculate new values
//write new values while adding 1 to num entries
//increase level_progress
//return upgraded json
//if level_progress >= level_max, send request to set level_progress to 0 and level_max *=1.2
//increase rank by 1
app.get('/api/getstats/', function(req, res) {
	//var id = req.params._id;

    var courseID = req.query.courseID;
    var email = req.query.email;

    console.log(courseID);
	User.getUser({email:email,stats_list: {$elemMatch: {course_code: courseID}}}, { "stats_list.$": courseID }, function(err, user) {
		if(err){
			throw err;
		} else {
			res.json(user);
		}
	});

 });
//

//update the stats by email
app.put('/api/updatestats/:email/:courseID', function(req, res) {
	//var id = req.params._id;

    var courseID = req.params.courseID;
    var email = req.params.email;
    var new_correctness_rate = req.body.new_correctness_rate;
    var new_response_time = req.body.new_response_time;
    var level_progress = req.body.level_progress;

    console.log(courseID);
	User.updateUser0({email:email,stats_list: {$elemMatch: {course_code: courseID}}},
	 {	$set: {'stats_list.$.correctness_rate': new_correctness_rate,'stats_list.$.avg_response_time': new_response_time,'stats_list.$.level_max': 8}, 
		$inc: {'stats_list.$.level_progress': level_progress,'stats_list.$.num_stat_contributions': 1}
	}, { "stats_list.$": courseID }, function(err, user) {
		if(err){
			throw err;
		} else {
		res.json(user);
		}
	});

 });


app.put('/api/increaserank/:email/:courseID', function(req, res) {
	//var id = req.params._id;

    var courseID = req.params.courseID;
    var email = req.params.email;

    console.log(courseID);
	User.updateUser0({email:email,stats_list: {$elemMatch: {course_code: courseID}}},
	 {	$set: {'stats_list.$.level_progress': 0}, 
		$inc: {'stats_list.$.rank': 1},
		$mul: {'stats_list.$.level_max': 1.2}
	}, { "stats_list.$": courseID }, function(err, user) {
		if(err){
			throw err;
		} else {
		res.json(user);
		}
	});

 });

//,{'stats_list.$.avg_response_time': new_response_time}]
 app.get('/user', function(req, res) {
    User.find({}, function(err, result) {
      if ( err ) throw err;
      res.json(result);
    });
  });

app.get('/question', function(req, res) {
	Question.find({}, function(err, result) {
	  if ( err ) throw err;
	  res.json(result);
	});
});

app.get('/course', function(req, res) {
	Course.find({}, function(err, result) {
	  if ( err ) throw err;
	  res.json(result);
	});
});

    app.get('/', function(req, res) {

      res.send('id: ' + req.query.id);
  });

app.get('/api/getcourses/email/', function(req, res) {
	//var id = req.params._id;

    var email = req.query.email;

    // var new_total_rate = req.query.new_total_rate;
    // var new_total_time = req.query.new_total_time;

	User.getUser({email:email}, 'course_list', function(err, user) {
		if(err){
			throw err;
		} else {
			res.json(user);
		}
	});

 });

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
		if((room_population % 2 === 0) && (room_population !== 0)) {
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


			console.log('game made by ' + p1_socket.username + ' and ' + p2_socket.username);

			io.nsps['/'].connected[p1].leave(course_room_name);
		    io.nsps['/'].connected[p2].leave(course_room_name);
		    io.in(priv_room_name).emit('game_made', p1_socket.username + ' ' + p2_socket.username);

		    console.log('sockets ' + p1 + ' and ' + p2 + 'have left ' + course_room_name);

		}

		// remove the player from the waiting room - happens when they want to leave or client times them out
		socket.on('stop_waiting', function(msg) {
			socket.leave(course_room_name);
		});

		socket.on('send_json_opponent',function(msg) {

			var in_rooms = Object.keys(socket.rooms);
			//find the last room it has been in 
			socket.broadcast.to(in_rooms[in_rooms.length-1]).emit('get_json_opponent',socket.player_json);

		});
		socket.on('send_questions',function(courseID) {
			Question.getGameQuestions(courseID, function(err, doc) {
				if(err){
					throw err;
				} else {
					var in_rooms = Object.keys(socket.rooms);
					//find the last room it has been in 
					socket.broadcast.to(in_rooms[in_rooms.length-1]).emit('get_questions',JSON.stringify(doc));
				}
			});


			
		});
	});
});




http.listen(3300, function() {
	console.log('listening on *:3300');
});