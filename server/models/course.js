var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var courseSchema = mongoose.Schema({
	course_number: {type: Number, required: true},
	course_subject: {type: String, required: true}
});

var Course = mongoose.model('Course', courseSchema);
module.exports = Course;

var ObjectId = require('mongodb').ObjectID;
module.exports.getCourseBySubject = (course, callback) => {
	var query = {
		course_subject:course.course_subject
	}
	Course.find(query, callback);
}

module.exports.getCourse = (course, callback) => {
	var query = {
		course_subject:course.course_subject,
		course_number:course.course_number
	}
	Course.find(query, callback);
}

module.exports.getUserCoursesById = (idArr, callback) => {
	 Course.find({ '_id': { $in: idArr.map(ObjectId) }},callback);
}

module.exports.removeCourse = (id, callback) => {
	var query = {_id: id};
	Course.remove(query, callback);
}