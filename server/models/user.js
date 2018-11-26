var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var ObjectId = require('mongodb').ObjectID;

var Course = require('./course');
// create a schema
var userSchema = mongoose.Schema({
	name: {type: String, required: true},
	alias: String,
	username: {type: String, unique: true, sparse: true},
	email: {type: String, required: true, unique: true},
	facebook_id: String,
	course_list: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Course'} ],
	teaching_course_list: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Course'} ],
	profile_photo_id: {type: Number, required: true},
	is_professor: Boolean,
	reported: Boolean,
	stats_list:[{ 
		course_code: { type: mongoose.Schema.Types.ObjectId, ref: 'Course'},
		rank: Number,
		avg_response_time: Number,
		correctness_rate: Number, 
		num_stat_contributions: Number,
		level_max: Number,
		level_progress: Number
	}]
});

var User = mongoose.model('User', userSchema);
module.exports = User;

module.exports.getUserById = (id,select, callback) => {
	User.findById(id, select,callback);
}

module.exports.getUserByEmail = (email, callback) => {
	User.find({email : email}, callback);
}

module.exports.addCourse = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		name: user.name,
		profile_photo_id: user.profile_photo_id
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.removeUser = (id, callback) => {
	var query = {_id: id};
	User.remove(query, callback);
}

//////////
module.exports.updateUserProfile = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		alias: user.alias,
		username: user.username
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateProfessorStatus = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		is_professor: user.is_professor
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateUserReportedStatus = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		reported: user.reported
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateUsername = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		username: user.username,
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateEmail = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		email: user.email,
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.addCourseToUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$addToSet: { 
				course_list: user.courseID 
		} 
	}
	User.findOneAndUpdate(query, update, options, callback);
}



module.exports.addStatToUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$addToSet: {
			stats_list: {
				course_code: user.courseID,
				rank: 1, 
				avg_response_time: user.add_response_time, 
				correctness_rate:user.add_correctness_rate,
				num_stat_contributions:1,
				level_progress:user.level_progress,
				level_max:8
			}
		}
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.deleteCourseFromUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$pull : { 
			course_list : {
				$in: [ObjectId(user.courseID)]
			} 
		}
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.deleteStatFromUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$pull : { 
			stats_list : {
				course_code : {
					$in : [ObjectId(user.courseID)]
				}
			} 
		} 
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.getUserStatByCourse = (user, callback) => {
	var query = {
		_id: user.id,
		stats_list: {
			$elemMatch: {course_code: ObjectId(user.courseID)}
		}
	}
	var select = { 
		"stats_list.$": user.courseID 
	}
	User.find(query,select,callback);
}


module.exports.getUserCourse = (id, user, callback) => {
	var query = {
		_id: id,
			course_list : {
				$in: [ObjectId(user.courseID)]
			}  
	}
	User.find(query,{},callback);
}

module.exports.UpdateStatData = (user, callback) => {
	var query = {
		_id: user.id,
		stats_list: {
			$elemMatch: {
				course_code: user.courseID
			}
		}
	}
	

	var update = {	
		$set: {
			'stats_list.$.correctness_rate': user.new_correctness_rate,
			'stats_list.$.avg_response_time': user.new_response_time
		}, 
		$inc: {
			'stats_list.$.level_progress': user.level_progress,
			'stats_list.$.num_stat_contributions': 1
		}
	}
	var options = { "stats_list.$": user.courseID }
	
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.UpdateRankData = (user, callback) => {
	var query = {
		_id: user.id,
		stats_list: {
			$elemMatch: {
				course_code: user.courseID
			}
		}
	}
	var update = {	
		$set: {
			'stats_list.$.level_progress': 0
		}, 
		$inc: {
			'stats_list.$.rank': 1
		},
		$mul: {
			'stats_list.$.level_max': 1.2
		}
	}
	var options = { "stats_list.$": user.courseID }
	
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.returnHighestRank = (courseID, callback) => {
	User.aggregate([
		{ $match: { "stats_list.course_code": ObjectId(courseID) }},
    	{ $unwind: '$stats_list'},
    	{ $match: {'stats_list.course_code': {$eq: ObjectId(courseID)}}},
  		{ $sort: { "stats_list.rank": -1,"stats_list.level_progress":-1}},
  		{ $limit : 3}
	]).exec(callback);
}

module.exports.returnRank = (user, callback) => {
	User.aggregate([
		{ $match: { "stats_list.course_code": ObjectId(user.courseID) }},
    	{ $unwind: '$stats_list'},
    	{ $match: {'stats_list.course_code': {$eq: ObjectId(user.courseID)}}},
		{ $match: {
			$or: [
				{"stats_list.rank":{$gt:user.rank}}, 
				{ $and: [
					{"stats_list.rank":{$eq:user.rank}},
					{"stats_list.level_progress":{$gt:user.level_progress}}
				]}
			]
		}},
    	{
      		$count: "current_rank"
    	}
	]).exec(callback);
}

module.exports.addProfessorCourseToUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$addToSet: { 
			teaching_course_list: user.courseID 
		} 
	}
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.getProfessorCourse = (id, user, callback) => {
	var query = {
		_id: id,
			teaching_course_list : {
				$in: [ObjectId(user.courseID)]
			}  
	}
	User.find(query,{},callback);
}

module.exports.deleteProfessorCourseFromUser = (id, user, options, callback) => {
	var query = {_id: id};
	var update = { 
		$pull : { 
			teaching_course_list : {
				$in: [ObjectId(user.courseID)]
			} 
		}
	}
	User.findOneAndUpdate(query, update, options, callback);
}

