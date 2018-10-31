var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

//var User = require('./course_stat');
// create a schema
var userSchema = mongoose.Schema({
	name: {type: String, required: true},
	email: {type: String, required: true, unique: true},
	course_list: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Course', unique: true} ],
	profile_photo_id: {type: Number, required: true},
	is_professor: Boolean,
	reported: Boolean,
	stats_list:[ { course_code: { type: mongoose.Schema.Types.ObjectId, ref: 'Course', unique: true },
	rank: Number,
	avg_response_time: Number,
	correctness_rate: Number, 
	num_stat_contributions: Number,
	level_max: Number,
	level_progress: Number
}]
	// course_code: {type: String, required: true, unique: true},
	// rank: {type: Number, required: true},
	// avg_response_time: Number,
	// correctness_rate: Number
});

var User = mongoose.model('User', userSchema);
module.exports = User;

// Get Book
module.exports.getUserById = (id, callback) => {
	User.findById(id, callback);
}

//blank get user
module.exports.getUser = (query, select, callback) => {
	User.find(query,select,callback);
}

//blank get user
module.exports.aggregateUser = (query, callback) => {
	User.find(query, callback);
}


module.exports.getUserByEmail = (email_a, callback) => {
	User.find({email : email_a}, callback);
}
module.exports.getUser0 = (email_a, callback) => {
	User.find({email : email_a}, callback);
}


module.exports.addUser = (user, callback) => {
	User.create(user, callback);
}

module.exports.updateUser = (id, update, options, callback) => {
	var query = {_id: id};
	User.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateUser0 = (query, update, options, callback) => {
	User.findOneAndUpdate(query, update, options, callback);
}


module.exports.updateUserByEmail = (email, update, options, callback) => {
	var query = {email: email};
	User.findOneAndUpdate(query, update, options, callback);
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

module.exports.removeUserByEmail = (email, callback) => {
	var query = {email: email};
	User.remove(query, callback);
}

