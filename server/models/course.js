var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var courseSchema = mongoose.Schema({
	course_number: {type: Number, required: true},
	course_subject: {type: String, required: true}
});

var Course = mongoose.model('Course', courseSchema);
module.exports = Course;

module.exports.getCourse = (query, callback) => {
	Course.find(query, callback);
	// Course.findOne({course_number : course_number,course_subject: course_subject}).select('_id').exec(callback);
}