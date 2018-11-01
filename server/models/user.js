var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var ObjectId = require('mongodb').ObjectID;

//var User = require('./course_stat');
// create a schema
var userSchema = mongoose.Schema({
	name: {type: String, required: true},
	alias: String,
	email: {type: String, required: true, unique: true},
	facebook_id: String,
	course_list: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Course'} ],
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

// Get Book
module.exports.getUserById = (id, callback) => {
	User.findById(id, callback);
}

//blank get user
// module.exports.getUser = (query, select, callback) => {

// 	User.find(query,select,callback);
// }

//blank get user
// module.exports.aggregateUser = (query, callback) => {
// 	User.find(query, callback);
// }

module.exports.getUserByEmail = (email, callback) => {
	User.find({email : email}, callback);
}

// module.exports.getUser0 = (email_a, callback) => {
// 	User.find({email : email_a}, callback);
// }


// module.exports.addUser = (user, callback) => {
// 	User.create(user, callback);
// }

// module.exports.updateUser = (id, update, options, callback) => {
// 	var query = {_id: id};
// 	User.findOneAndUpdate(query, update, options, callback);
// }

// module.exports.updateUser0 = (query, update, options, callback) => {
// 	User.findOneAndUpdate(query, update, options, callback);
// }


// module.exports.updateUserByEmail = (email, update, options, callback) => {
// 	var query = {email: email};
// 	User.findOneAndUpdate(query, update, options, callback);
// }


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
		name: user.name,
		profile_photo_id: user.profile_photo_id
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
				avg_response_time: 5, 
				correctness_rate:5,
				num_stat_contributions:0,
				level_progress:0,
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

//blank get user
module.exports.getUserStatByCourse = (user, callback) => {
	var query = {
		_id: user.id,
		stats_list: {
			$elemMatch: {course_code: user.courseID}
		}
	}
	var select = { 
		"stats_list.$": user.courseID 
	}
	User.find(query,select,callback);
}

module.exports.getUserStatByCourse = (user, callback) => {
	var query = {
		_id: user.id,
		stats_list: {
			$elemMatch: {course_code: user.courseID}
		}
	}
	var select = { 
		"stats_list.$": user.courseID 
	}
	User.find(query,select,callback);
}

module.exports.UpdateStatData = (user, callback) => {
	var user_params = user.params;
	var user_body = user.body;
	var query = {
		_id: user_params.id,
		stats_list: {
			$elemMatch: {
				course_code: user_params.courseID
			}
		}
	}
	var update = {	
		$set: {
			'stats_list.$.correctness_rate': user_body.new_correctness_rate,
			'stats_list.$.avg_response_time': user_body.new_response_time
		}, 
		$inc: {
			'stats_list.$.level_progress': user_body.level_progress,
			'stats_list.$.num_stat_contributions': 1
		}
	}
	var options = { "stats_list.$": user_params.courseID }
	
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


// module.exports.removeUserByEmail = (email, callback) => {
// 	var query = {email: email};
// 	User.remove(query, callback);
// }

// userSchema.pre('save', async function(next) {
//   try {
//     console.log('entered');
//     if (this.method !== 'local') {
//       next();
//     }

//     // Generate a salt
//     const salt = await bcrypt.genSalt(10);
//     // Generate a password hash (salt + hash)
//     const passwordHash = await bcrypt.hash(this.local.password, salt);
//     // Re-assign hashed version over original, plain text password
//     this.local.password = passwordHash;
//     console.log('exited');
//     next();
//   } catch(error) {
//     next(error);
//   }
// });

// userSchema.methods.isValidPassword = async function(newPassword) {
//   try {
//     return await bcrypt.compare(newPassword, this.local.password);
//   } catch(error) {
//     throw new Error(error);
//   }
// }