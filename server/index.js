const app = require('express')();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const passport = require('passport');
const passportJWT = passport.authenticate('jwt', { session: false });

var User = require('./models/user.js');
var Question = require('./models/question.js');
var Course = require('./models/course.js');

var dbHost = 'mongodb://localhost:27017/TestBenchDB';
var ObjectId = require('mongodb').ObjectID;
mongoose.connect(dbHost, { useNewUrlParser: true });
mongoose.set('useCreateIndex', true);
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());

var db = mongoose.connection;

//for auth
app.use('/users', require('./routes/users'));


// //create new user
// app.post('/api/user', function(req, res) {

// 	console.log('create a new user');
//     var user = new User( {
//     name : req.body.name,
//     email : req.body.email,
//     profile_photo_id : req.body.profile_photo_id,
//     is_professor: false,
//     reported: false
//     });
//     user.save(function(err, result) {
//       if ( err ) {
// 		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
//       }
//       else 
//       res.json( {
//         message:"Successfully added user",
//         user:result
//       });
//     });
// });

//set username for user given userid 
app.put('/api/user/username/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateUsername(id, req.body, {}, function(err, doc) {
		if(err){
      	console.log(err.code);
      		res.status(err.code == 11000 ? 409 : (err.code >= 100 && err.code < 600 ? err.code : 500)).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});

// //set username for user given userid 
// app.put('/api/user/email/:_id', passportJWT, function(req, res) {
// 	var id = req.params._id;
// 	User.updateEmail(id, req.body, {}, function(err, doc) {
// 		if(err){
//       		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
//       	} 
// 		res.json(doc);
		
// 	});
// });

//create new question
app.post('/api/question', passportJWT, getCourseByName,function(req, res) {
	var ans = [req.body.correct_answer,req.body.incorrect_answer_1,req.body.incorrect_answer_2,req.body.incorrect_answer_3];
	var i;
	var j;
	for (i = 0;i<ans.length;i++)
	{
		for (j = 0;j<ans.length;j++)
		{
			if ( ans[i] ===  ans[j] && i !== j) {
				res.status(400).send({ success: false, message: 'Identical Answers' });
			}
		}
	}
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
      	if ( err ) {
	      	if (err.name === 'MongoError' && err.code === 11000) {
	        	res.status(500).send({ success: false, message: 'Question already exist!' });
	      	} else {
	      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	      	}
      	} else {
	      	res.json( {
		        message:"Successfully added question",
		        question:result
      		});
	    }
    });
});

//make new course
app.post('/api/course/', function(req, res) {
    var course = new Course( {
		course_number:req.body.course_number,
		course_subject:req.body.course_subject
    });
	course.save(function(err, result) {
    	if ( err ) {
	      	if (err.name === 'MongoError' && err.code === 11000) {
				res.status(500).send({ success: false, message: 'User already exist!' });
	      	} else {
	      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: { success: false, message: err.message } });
	      	}
		} else 
      	res.json( {
      		message:"Successfully added course",
      		user:result
      	});
    });
});

//test question randomizer
app.get('/api/getgame/', passportJWT, getCourseByNameQuery, function(req, res) {
    var courseID = req.query.courseID;
    console.log(courseID);
	Question.getGameQuestions(courseID, function(err, game) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(game);
		}
	});
});

//get sorted array
app.get('/api/sortedusers/', passportJWT, getCourseByNameQuery, function(req, res) {

    var courseID = req.query.courseID;
    console.log(courseID);
	User.returnHighestRank(courseID, function(err, game) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(game);
		}
	});
});

//add a course to a user using course name
function getCourseByNameQuery(req, res, next) {
	if(req.query.courseID == null) {
		Course.getCourse(req.query, function(err, courses) {
			if(err){
		  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
			} 
			//res.json(doc);
			if(courses[0] == null)
			{
				res.status(400).send({ success: false, message: "course does not exist" });
			} else {
			var course_entry = courses[0].toObject();
			//console.log(course_entry);
	      	req.query.courseID = course_entry._id;
			//console.log(req.body.courseID);
			next();
			}
		});
	} else {
		next();
	}

}

function retrieveUserStat(req, res, next) {
	User.getUserStatByCourse(req.query, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	} 

		if(user[0] == null)
		{
			res.status(400).send({ success: false, message: "stat not availible for this course" });
		}  else {
	 		var rec_user = user[0].toObject().stats_list[0];
	      	req.query.rank = rec_user.rank;
	      	req.query.level_progress = rec_user.level_progress;
	      	console.log(req.query.rank);
	      	console.log(req.query.level_progress);
	      	next();
		}
	});

}

app.get('/api/rank/', passportJWT, getCourseByNameQuery,retrieveUserStat,function(req, res) {

	User.returnRank(req.query, function(err, game) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(game);
		}
	});
});



//get user by email
app.get('/api/user/email/', passportJWT, function(req, res) {
	var email = req.query.email;

	User.getUserByEmail(email, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		}
		res.json(doc);
	});
});

//get user by id
app.get('/api/user/', passportJWT, function(req, res) {
    var id= req.query.id;

	User.getUserById(id,{}, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		res.json(doc);

	});
});

//get user courses with subject and number
function findCourseEntries(req, res, next) {
	
    var id = req.query.id;

	User.getUserById(id, 'course_list', function(err, user) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
		//res.json(user);
		req.body.courses = user.toObject().course_list;
		console.log("\n" + req.body.courses + "\n");
		next();
	});
}

app.get('/api/getcourses/', passportJWT,findCourseEntries, function(req, res) {
    var id = req.query.id;
	Course.getUserCoursesById(req.body.courses, function(err, user) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
		res.json(user);
	});
});


// //get a user's courseID's by user id
// app.get('/api/getcourses/id/', passportJWT, function(req, res) {
//     var id = req.query.id;

// 	User.getUserById(id, 'course_list', function(err, user) {
// 		if(err){
// 			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
// 	    } 
// 		res.json(user);
// 	});

// });

// //get a user's statss by user id
// app.get('/api/getallstats/id/', function(req, res) {
//     var id = req.query.id;

// 	User.getUserById1(id, function(err, user) {
// 		if(err){
// 			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
// 	    } 
// 		res.json(user);
// 	});

// });



//get course by subject and number
app.get('/api/course/', function(req, res) {
	Course.getCourse(req.query, function(err, doc) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		res.json(doc);
		
	});
});

//get course by subject
app.get('/api/course/subject', passportJWT, function(req, res) {
	Course.getCourseBySubject(req.query, function(err, doc) {
		if(err){
	      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		res.json(doc);
	});
});

// //delete user by email
// app.delete('/api/user/email/:email', function(req, res) {
// 	var email = req.params.email;
// 	User.removeUserByEmail(email, function(err, book) {
// 		if(err){
//       	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
//       } else 
// 		res.json(book);
// 	});
// });

// //edit user profile by email
// app.put('/api/user/email/:email', function(req, res) {
// 	var email = req.params.email;

//     // var name = ;
//     // var profile_photo_id = ;
// 	User.updateUserByEmail(email, {
// 		name: req.body.name,
// 		profile_photo_id: req.body.profile_photo_id
// 	}, {}, function(err, user) {
// 		if(err){
//       	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
//       } else 
// 		res.json(user);
// 	});
// });

//delete user by id
app.delete('/api/user/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.removeUser(id, function(err, doc) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});

//delete question by id
app.delete('/api/question/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	Question.removeQuestion(id, function(err, doc) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});

//delete course by id
app.delete('/api/course/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	Course.removeCourse(id, function(err, doc) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});


//edit user profile by id
app.put('/api/user/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateUserProfile(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code == 11000 ? 409 : (err.code >= 100 && err.code < 600 ? err.code : 500)).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});



//add a course to a user using course name
function getCourseByName(req, res, next) {
	
	Course.getCourse(req.body, function(err, courses) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		//res.json(doc);
		if(courses[0] == null)
		{
			res.status(400).send({ success: false, message: "course does not exist" });
		} else {
		var course_entry = courses[0].toObject();
		//console.log(course_entry);
      	req.body.courseID = course_entry._id;
		//console.log(req.body.courseID);
		next();
		}
	});

}

function checkUserCourse(req, res, next) {

	User.getUserCourse(req.params._id,req.body, function(err, doc) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		//res.json(doc);
		if(doc[0] != null)
		{
			res.status(409).send({ success: false, message: "you already have this course" });
		} else {
		next();
		}
	});

}

//add a course using courseID using user id
app.put('/api/addcourse/:_id', passportJWT,getCourseByName,checkUserCourse, function(req, res) {
	var id = req.params._id;

		console.log(req.body);
	User.addCourseToUser(id, req.body, {}, function(err, user) {
		if(err) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      } 
		res.json(user);
	});
});


//helper to show if course already exists
function ensureStatNotPresent(req, res, next) {
	req.body.id = req.params.id;
	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	} 

		if(user[0] != null)
		{
			res.status(409).send({ success: false, message: "user already has stat for this course" });
		}  else {
	      	next();
		}
	});
}

//add a stats object by id
app.put('/api/addnewstat/:id/', passportJWT, getCourseByName,ensureStatNotPresent, function(req, res) {
	var id = req.params.id;

	User.addStatToUser(id, req.body, {}, function(err, user) {
		if(err) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      }
		res.json(user);
	});
});


// app.put('/api/addnewstatID/:_id', function(req, res) {
// 	var id = req.params._id;
//     var courseID = req.body.courseID;
//     console.log(courseID);
// 	User.updateUser(id, { $addToSet: {stats_list: {course_code: courseID,rank: 1, 
// 		avg_response_time: null, correctness_rate:null,num_stat_contributions:0}}}, {}, function(err, user) {
// 		if(err) {
//       	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
//       }
// 		res.json(user);
// 	});
// });



//small function to check if user stat exists
function retrieveUserStatBody(req, res, next) {
	req.body.id = req.params.id;
	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	} 

		if(user[0] == null)
		{
			res.status(400).send({ success: false, message: "stat not availible for this course" });
		}  else {
	      	next();
		}
	});
}
//delete stat for a specific course for a user
app.put('/api/deletestat/:id', passportJWT,getCourseByName, retrieveUserStatBody, function(req, res) {
	var id = req.params.id;

	User.deleteStatFromUser(id, req.body, {}, function(err, user) {
		if(err) {
	    	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		}
		res.json(user);
	});
});

//delete a course from a user

function checkCourseNotPresent(req, res, next) {

	User.getUserCourse(req.params._id,req.body, function(err, doc) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		//res.json(doc);
		if(doc[0] == null)
		{
			res.status(409).send({ success: false, message: "course not found with user" });
		} else {
		next();
		}
	});

}
app.put('/api/deletecourse/:_id', passportJWT,getCourseByName,checkCourseNotPresent, function(req, res) {
	var id = req.params._id;

	User.deleteCourseFromUser(id, req.body, {}, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
    	}
		res.json(user);
	});     
});


//get a stat given courseID
app.get('/api/getstats/', passportJWT,getCourseByNameQuery, function(req, res) {
	
	User.getUserStatByCourse(req.query, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	}
		res.json(user);
		
	});

 });

// }
 

//update stats progression:
//get the information from the specific stat object.
//client: use the information to calculate new values
//write new values while adding 1 to num entries
//increase level_progress
//return upgraded json
//if level_progress >= level_max, send request to set level_progress to 0 and level_max *=1.2
//increase rank by 1

//put together the middleware
app.put('/api/updatestat/:id', passportJWT, getCourseByName, retrieveOldResults, newResultCalc, setVals, checkRank, function(req, res) {

	//update rank if applicable
	User.UpdateRankData(req.body, function(err, user) {
		if(err) {
	      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		}

		//mark the return value to indicate rank increase
		var userForm = user.toObject();
		userForm.ranked_up = true;
		res.json(userForm);
	});
});

function retrieveOldResults(req, res, next) {
	
	req.body.id = req.params.id;
	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		if (user[0] == null) {
			res.status(400).send({ success: false, message: "stat not availible for this course" });
		} else {
			var old_user = user[0].toObject().stats_list[0];
			req.body.old_correctness_rate = old_user.correctness_rate;
			req.body.old_response_time = old_user.avg_response_time;
			req.body.num_stat_contributions = old_user.num_stat_contributions;
			next();
		}
	});

}

function newResultCalc(req, res, next) {
	var old_correctness_rate = req.body.old_correctness_rate;
    var old_response_time = req.body.old_response_time;

	var add_correctness_rate = req.body.add_correctness_rate;
    var add_response_time = req.body.add_response_time;

    var num_stat_contributions = req.body.num_stat_contributions;

    //calculate new values for average response time and correctness rate
    var resp_total = ((old_response_time * num_stat_contributions) + add_response_time) / (num_stat_contributions + 1);
    var corr_total = ((old_correctness_rate * num_stat_contributions) + add_correctness_rate) / (num_stat_contributions + 1);

    req.body.new_correctness_rate = corr_total;
    req.body.new_response_time = resp_total;

    next();
}

function setVals(req, res, next) {

	User.UpdateStatData(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
	    next();
	});

}


function checkRank(req, res, next) {
 	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	}

		req.body.level_progress = user[0].toObject().stats_list[0].level_progress;
		req.body.level_max = user[0].toObject().stats_list[0].level_max;
		if(req.body.level_progress >= req.body.level_max) {
			next();
  		} else {
			//mark the return value to indicate no rank increase
	  		var userForm = user[0].toObject();
			userForm.ranked_up = false;
	  		return res.json(userForm);
  		}
	});
}




//basic functions to get database contents (for debugging)
 app.get('/user',passportJWT, function(req, res) {
    User.find({}, function(err, result) {
      if ( err ) {
	      	res.status(err.code).send({ success: false, message: err.message });
	      } else {
      res.json(result);
  	}
    });
  });

app.get('/question', passportJWT, function(req, res) {
	Question.find({}, function(err, result) {
	  if ( err ) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	      } else 
	  res.json(result);
	});
});

app.get('/course',passportJWT, function(req, res) {
	Course.find({}, function(err, result) {
	  if ( err ) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	      } else 
	  res.json(result);
	});
});



//socket connections
io.on('connection', function(socket) {
	// console.log("socket " + socket.id + " has connected");

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
		// console.log(queue_info.username + ' has joined ' + course_room_name);

		// a match can happen if the number of people in the room is even and not 0
		var room_population = io.nsps['/'].adapter.rooms[course_room_name].length;
		if((room_population % 2 === 0) && (room_population !== 0)) {
			// console.log('found a match in ' + course_room_name);

			// connect the two players in the room by making them join a private room
			// also make them leave the current waiting room
			var p1 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[0];
			var p2 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[1];
			var priv_room_name = 'RM' + p1 + p2;
			var p1_socket = io.nsps['/'].connected[p1];
			p1_socket.join(priv_room_name);
			var p2_socket = io.nsps['/'].connected[p2];
			p2_socket.join(priv_room_name);


			// console.log('game made by ' + p1_socket.username + ' and ' + p2_socket.username);

			io.nsps['/'].connected[p1].leave(course_room_name);
		    io.nsps['/'].connected[p2].leave(course_room_name);
		    io.in(priv_room_name).emit('game_made', p1_socket.username + ' ' + p2_socket.username);

		    // console.log('sockets ' + p1 + ' and ' + p2 + 'have left ' + course_room_name);

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