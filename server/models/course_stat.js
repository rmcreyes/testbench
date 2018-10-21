var mongoose = require('mongoose');
var courseStatSchema = mongoose.Schema;

var courseStatSchema = mongoose.Schema({
	course_code: {type: String, required: true, unique: true},
	rank: {type: Number, required: true},
	avg_response_time: Number,
	correctness_rate: Number

});

var CourseStat = mongoose.model('CourseStat', courseStatSchema);
model.exports = CourseStat;
