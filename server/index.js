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

//for auth using facebook to get JWT key
app.use('/users', require('./routes/users'));

app.get('/question', passportJWT, function(req, res) {
	Question.find({}, function(err, result) {
	  if ( err ) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	      } else 
	  res.json(result);
	});
});

app.get('/user',passportJWT, function(req, res) {
    User.find({}, function(err, result) {
      if ( err ) {
	      	res.status(err.code).send({ success: false, message: err.message });
	      } else {
      res.json(result);
  	}
    });
  });

//set username for user given userid 
app.put('/api/user/username/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateUsername(id, req.body, {}, function(err, doc) {
		if(err){
      		res.status(err.code == 11000 ? 409 : (err.code >= 100 && err.code < 600 ? err.code : 500)).send({ success: false, message: err.message });
      	} 
		res.json(doc);
	});
});


//create new question
app.post('/api/question', passportJWT, getCourseByName,function(req, res) {
	//check for identical answers
	var ans = [req.body.correct_answer,req.body.incorrect_answer_1,req.body.incorrect_answer_2,req.body.incorrect_answer_3];
	var i;
	var j;
	for (i = 0;i<ans.length;i++)
	{
		for (j = 0;j<ans.length;j++)
		{
			if ( ans[i] ===  ans[j] && i !== j) {
				res.status(400).send({ success: false, message: 'Identical Answers' });
				return;
			}
		}
	}

    var question = new Question( {
		question_text: req.body.question_text,
		correct_answer: req.body.correct_answer,
		incorrect_answer_1: req.body.incorrect_answer_1,
		incorrect_answer_2: req.body.incorrect_answer_2,
		incorrect_answer_3: req.body.incorrect_answer_3,
		courseID: req.body.courseID,
		rating: null,
		rating_count:0,
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


//Get all questions given course
app.get('/api/getcoursequestions/', passportJWT, getCourseByNameQuery, function(req, res) {
    var courseID = req.query.courseID;
	Question.getCourseQuestions(courseID, function(err, ques) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(ques);
		}
	});
});


//get top 3 users in a course
app.get('/api/sortedusers/', passportJWT, getCourseByNameQuery, function(req, res) {

    var courseID = req.query.courseID;
	User.returnHighestRank(courseID, function(err, game) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(game);
		}
	});
});

//get rank-1 value of user
app.get('/api/rank/', passportJWT, getCourseByNameQuery,retrieveUserStat,function(req, res) {

	User.returnRank(req.query, function(err, game) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(game);
		}
	});  
}); 

//helper function to translate course name and number to courseID
//and catch whether it doesn't exist
//(this is the version for GET methods)
function getCourseByNameQuery(req, res, next) {
	if(req.query.courseID == null) {
		Course.getCourse(req.query, function(err, courses) {
			if(err){
		  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
			} 
			if(courses[0] == null)
			{
				res.status(400).send({ success: false, message: "course does not exist" });
			} else {
			var course_entry = courses[0].toObject();
	      	req.query.courseID = course_entry._id;
			next();
			}
		});
	} else {
		next();
	}

}

//helper function to ensure that a user has a stat for a certain course
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
	      	next();
		}
	});

}

//get number of quesitons in a subject
app.get('/api/questioncount/', passportJWT, getCourseByNameQuery,function(req, res) {

	Question.returnQuestionNum(req.query, function(err, doc) {
		if(err){
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} else {
			res.json(doc);
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
app.get('/api/getcourses/', passportJWT,findCourseEntries, function(req, res) {
    var id = req.query.id;
	Course.getUserCoursesById(req.body.courses, function(err, user) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
		res.json(user);
	});
});

//helper function that stores a user's course list and professor list in the body of the request
//(to use to query courses)
function findCourseEntries(req, res, next) {
	
    var id = req.query.id;

	User.getUserById(id,{}, function(err, user) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
		//res.json(user);
		req.body.courses = user.toObject().course_list;
		req.body.prof_courses = user.toObject().teaching_course_list;
		next();
	});
}

//get the courses that a prof user reviews
app.get('/api/getprofcourses/', passportJWT,findCourseEntries, function(req, res) {
    var id = req.query.id;
	Course.getUserCoursesById(req.body.prof_courses, function(err, user) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
		res.json(user);
	});
});

//get course by subject and number
app.get('/api/course/', function(req, res) {
	Course.getCourse(req.query, function(err, doc) {
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

//edit user profile (alias and username) by id
app.put('/api/user/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateUserProfile(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code == 11000 ? 409 : (err.code >= 100 && err.code < 600 ? err.code : 500)).send({ success: false, message: err.message });
      	} 
		res.json(doc);
		
	});
});

//set professor boolean
app.put('/api/professor/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateProfessorStatus(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
	});
});

//set reported boolean
app.put('/api/user/reported/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	User.updateUserReportedStatus(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
	});
});

//set reported boolean
app.put('/api/question/reported/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	Question.updateQuestionReportedStatus(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
	});
});

//set reported boolean
app.put('/api/question/verified/:_id', passportJWT, function(req, res) {
	var id = req.params._id;
	Question.updateVerifiedStatus(id, req.body, {}, function(err, doc) {
		if(err){
			res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      	} 
		res.json(doc);
	});
});

//add a course to a user using course name
app.put('/api/addcourse/:_id', passportJWT,getCourseByName,checkUserCourse, function(req, res) {
	var id = req.params._id;

	User.addCourseToUser(id, req.body, {}, function(err, user) {
		if(err) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      } 
		res.json(user);
	});
});

//helper function that uses course subject and number to translate into courseID. 
//also ensures that it exists
//(this is the version for POST and PUT requests)
function getCourseByName(req, res, next) {
	
	Course.getCourse(req.body, function(err, courses) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		if(courses[0] == null)
		{
			res.status(400).send({ success: false, message: "course does not exist" });
		} else {
		var course_entry = courses[0].toObject();
      	req.body.courseID = course_entry._id;
		next();
		}
	});

}

//helper function that ensures that a user does not have a course in their course list
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

//add a course to professor teaching list with user id and course name
app.put('/api/addprofessorcourse/:_id', passportJWT,getCourseByName,checkProfessorCourse, function(req, res) {
	var id = req.params._id;

	User.addProfessorCourseToUser(id, req.body, {}, function(err, user) {
		if(err) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      } 
		res.json(user);
	});
});

//checks whether a professor already has a course
function checkProfessorCourse(req, res, next) {

	User.getProfessorCourse(req.params._id,req.body, function(err, doc) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		if(doc[0] != null)
		{
			res.status(409).send({ success: false, message: "you already have this course" });
		} else {
		next();
		}
	});
}


//helper to show if stat already exists before making it
//(for POST and PUT methods)
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
	console.log('add new stat');
	var id = req.params.id;

	User.addStatToUser(id, req.body, {}, function(err, user) {
		if(err) {
      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
      }
		res.json(user);
	});
});



//helper function to ensure that a stat exists before attempting to return it
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


//delete a course from professor
app.put('/api/deleteprofcourse/:_id', passportJWT,getCourseByName,checkProfCourseNotPresent, function(req, res) {
	var id = req.params._id;

	User.deleteProfessorCourseFromUser(id, req.body, {}, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
    	}
		res.json(user);
	});     
});

function checkProfCourseNotPresent(req, res, next) {
	User.getProfessorCourse(req.params._id,req.body, function(err, doc) {
		if(err){
	  		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		if(doc[0] == null)
		{
			res.status(409).send({ success: false, message: "course not found with user" });
		} else {
		next();
		}
	});
}

//delete a course from a user
app.put('/api/deletecourse/:_id', passportJWT,getCourseByName,checkCourseNotPresent, function(req, res) {
	var id = req.params._id;

	User.deleteCourseFromUser(id, req.body, {}, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
    	}
		res.json(user);
	});     
});

//ensure that a user has a course before deleting it.
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

//get a stat given courseID
app.get('/api/getstats/', passportJWT,getCourseByNameQuery, function(req, res) {
	
	User.getUserStatByCourse(req.query, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	}
		res.json(user);
		
	});

 });

 //get question by id
function retrieveOldQuestion(req, res, next) {
	
	Question.getQuestionById(req.params.id,{}, function(err, question) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		} 
		
		if (question == null) {
			res.status(400).send({ success: false, message: "question not availible" });
		} else {
			var old_question = question.toObject().rating;
			req.rating_count = question.toObject().rating_count;
			if(req.rating_count != null)
			{
				req.new_rating = ((old_question * req.rating_count) + req.body.new_rating )/(req.rating_count + 1);
				req.rating_count++;
			} else {
				req.new_rating = req.body.new_rating;
				req.rating_count = 1;
			}
			next();
		}
	});

}

 //update the question rating of a question given question id
app.put('/api/updaterating/:id', passportJWT, retrieveOldQuestion, function(req, res) {

	Question.updateRating(req.params.id, req, function(err, question) {
		if(err) {
	      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		}
		res.json(question);
	});
});



//update the user's stat and advance/develop them as needed
app.put('/api/updatestat/:id', passportJWT, getCourseByName, retrieveOldResults, newResultCalc,setVals,checkRank, function(req, res) {

	User.UpdateRankData(req.body, function(err, user) {
		if(err) {
	      	res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
		}
		else {
		//mark the return value to indicate rank increase
		var userForm = user.toObject();
		userForm.ranked_up = true;
		return res.json(userForm);
		}
	});
});

//helper function to get old stat of user
function retrieveOldResults(req, res, next) {
	
	req.body.id = req.params.id;
	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message+2 });
		} 
		if (user[0] == null) {
			res.status(400).send({ success: false, message: "stat not availible for this course" });
		} else {
			var old_user = user[0].toObject().stats_list[0];
			req.body.old_correctness_rate = old_user.correctness_rate;
			req.body.old_response_time = old_user.avg_response_time;
			req.body.num_stat_contributions = old_user.num_stat_contributions;
			return next();
		}
	});

}

//calculate new values for response time and correctness rate
function newResultCalc(req, res, next) {
	var old_correctness_rate = req.body.old_correctness_rate;
    var old_response_time = req.body.old_response_time;

	var add_correctness_rate = req.body.add_correctness_rate;
    var add_response_time = req.body.add_response_time;

    var num_stat_contributions = req.body.num_stat_contributions;

	var resp_total = ((old_response_time * num_stat_contributions) + add_response_time) / (num_stat_contributions + 1);
	var corr_total = ((old_correctness_rate * num_stat_contributions) + add_correctness_rate) / (num_stat_contributions + 1);
	
    req.body.new_correctness_rate = corr_total;
    req.body.new_response_time = resp_total;

	return next();
}

//update database values with new calculated values
function setVals(req, res, next) {

	User.UpdateStatData(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
	    } 
	    return next();
	});

}

//check if a user should rank up
function checkRank(req, res, next) {
 	User.getUserStatByCourse(req.body, function(err, user) {
		if(err) {
      		res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
     	}

		req.body.level_progress = user[0].toObject().stats_list[0].level_progress;
		req.body.level_max = user[0].toObject().stats_list[0].level_max;
		
		if(req.body.level_progress >= req.body.level_max) {
			return next();
  		} else {
			//mark the return value to indicate no rank increase
			var userForm = user[0].toObject();
			userForm.ranked_up = false;
			return res.send(userForm);
  		}
	});
}


//socket connections
io.on('connection', function(socket) {
	 console.log("socket " + socket.id + " has connected");
	 socket.ready = false;
	 socket.answered = false;
	// event `queue_for_game` occurs when player has selected the course they want
	// to be quizzed on, emitted with the corresponding `course_code`
	socket.on('queue_for_game', function(queue_info_json) {
		var queue_info = JSON.parse(queue_info_json);
		socket.username = queue_info.username; 
		socket.alias = queue_info.alias; 
		queue_info.course = queue_info.course_subject + queue_info.course_number;
	
		socket.player_json = queue_info_json;
		
		// all players who want to play in the same course are put in the same room
		var course_room_name = 'RM' + queue_info.course;
		socket.join(course_room_name);
		//  console.log(queue_info.username + ' has joined ' + course_room_name);

		// a match can happen if the number of people in the room is even and not 0
		var room_population;
		if(io.nsps['/'].adapter.rooms[course_room_name] !== undefined)
		{
			room_population = io.nsps['/'].adapter.rooms[course_room_name].length;
		} else {
			room_population = 0;
		}
		// console.log("the room population is " +room_population );
		if((room_population % 2 === 0) && (room_population !== 0)) {
			 console.log('found a match in ' + course_room_name);
			socket.isSecond = true;
			// connect the two players in the room by making them join a private room
			// also make them leave the current waiting room
			var p1 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[0];
			var p2 = Object.keys(io.sockets.adapter.rooms[course_room_name].sockets)[1];
			var priv_room_name = 'RM' + p1 + p2;
			var p1_socket = io.nsps['/'].connected[p1];
			p1_socket.join(priv_room_name);
			var p2_socket = io.nsps['/'].connected[p2];
			p2_socket.join(priv_room_name);
			io.sockets.adapter.rooms[priv_room_name].questionComplete = false;

			console.log('game made by ' + p1_socket.username + ' and ' + p2_socket.username);

			io.nsps['/'].connected[p1].leave(course_room_name);
			io.nsps['/'].connected[p2].leave(course_room_name);
			
			var course = {course_subject:queue_info.course_subject, course_number:queue_info.course_number};

			Course.getCourse(course, function(err, courses) {
				if(err){
					//res.status(err.code >= 100 && err.code < 600 ? err.code : 500).send({ success: false, message: err.message });
					throw err;
				} 
				//res.json(doc);
				if(courses[0] == null)
				{
					throw err;
				} else {
					var course_entry = courses[0].toObject();
					courseID = course_entry._id;

					Question.getGameQuestions(courseID, function(err, doc) {
						if(err){
							throw err;
						} else {
							console.log("game made is called in " + priv_room_name + " with " + p1_socket.username + " and " + p2_socket.username);
		    				io.in(priv_room_name).emit('game_made', JSON.stringify(doc));
						}
					});
				}
			});

		}


		// remove the player from the waiting room - happens when they want to leave or client times them out
		socket.on('stop_waiting', function(msg) {
			socket.leave(course_room_name);
			console.log("socket" + socket.id + "has stopped waiting");
		});

		socket.on('send_json_opponent',function(course_subject,course_number) {
			// console.log("send-json_oponnent called");
			
			var in_rooms = Object.keys(socket.rooms);
			//find the last room it has been in 
			console.log("json opponent called to " + in_rooms[in_rooms.length-1]);	
			socket.broadcast.to(in_rooms[in_rooms.length-1]).emit('get_json_opponent',socket.player_json);

		});
		socket.on('ready_next',function() {
 			console.log("ready_next called");
			socket.ready = true;
			var in_rooms = Object.keys(socket.rooms);
			var latest_room = io.sockets.adapter.rooms[in_rooms[in_rooms.length-1]];
			// var i = 0;
			while (latest_room === undefined)
			{
				io.in(in_rooms[in_rooms.length-1]).emit('start_question',"READY");
				return;
			}
			var p1 = Object.keys(latest_room.sockets)[0];
			var p2 = Object.keys(latest_room.sockets)[1];


			while(p2 === undefined || p1 === undefined) {
				io.in(in_rooms[in_rooms.length-1]).emit('start_question',"READY");
				return;
			}

			if(io.sockets.connected[p1].ready && io.sockets.connected[p2].ready) {
				console.log("Both users are ready!");
				io.in(in_rooms[in_rooms.length-1]).emit('start_question',"READY");
				io.sockets.connected[p1].ready = false;
				io.sockets.connected[p2].ready = false;
				io.sockets.connected[p1].answered = false;
				io.sockets.connected[p2].answered = false;
				io.sockets.adapter.rooms[in_rooms[in_rooms.length-1]].questionComplete = false;
			}
		 });

		 //called when a question is answered by either player
		 socket.on('on_answer',function(answer, pts_incr) {
			 console.log("answer: " + answer);
			 console.log("pts: " + pts_incr);
			var return_string;
			var in_rooms = Object.keys(socket.rooms);
			var cur_room = io.sockets.adapter.rooms[in_rooms[in_rooms.length-1]];
			console.log("question answered: " + cur_room.questionComplete);
			if(!socket.answered && !cur_room.questionComplete) {
				socket.answered = true;
				if(answer === 'ANSWER_RIGHT')
				{
					cur_room.questionComplete = true;
					return_string = {correct: true, user: socket.alias, points: pts_incr};
					console.log("broadcast: " +JSON.stringify(return_string));
					io.in(in_rooms[in_rooms.length-1]).emit('turn_over',JSON.stringify(return_string));
				}
				else if(answer === 'ANSWER_WRONG')
				{
					return_string = {correct: false, user: socket.alias, points: 0};
					console.log("broadcast: " +JSON.stringify(return_string));
					io.in(in_rooms[in_rooms.length-1]).emit('turn_over',JSON.stringify(return_string));
				}
			}
		 });

		 //send an emoji enumeration to your opponent
		 socket.on('send_emoji',function(emoji_int) {
			var in_rooms = Object.keys(socket.rooms);
			console.log('entered');
			socket.broadcast.to(in_rooms[in_rooms.length-1]).emit('broadcast_emoji',emoji_int);
		 });

		 //signal other player that you have left early
		 socket.on('leave_early',function() {
			var in_rooms = Object.keys(socket.rooms);
			console.log('entered');
			socket.broadcast.to(in_rooms[in_rooms.length-1]).emit('broadcast_leave',"OPPONENT LEFT");
		 });
	});
	socket.on('disconnect', function() {
		console.log(socket.username + " has left.");
	});
});

http.listen(3300, function() {
	console.log('listening on *:3300');
});