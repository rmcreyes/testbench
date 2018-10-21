var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

// create a schema
var userSchema = mongoose.Schema({
	user_id: { type: Number, required: true, unique: true},
	name: {type: String, required: true},
	player_stats: String,
	course_list: String,
	is_professor: Boolean,
	reported: Boolean
});

var User = mongoose.model('User', userSchema);
module.exports = User;

// Get Book
module.exports.getUserById = (id, callback) => {
	User.findById(id, callback);
}