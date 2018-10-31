var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var questionSchema = mongoose.Schema({
	question_text: {type: String, required: true, unique: true},
	correct_answer: {type: String, required: true},
	incorrect_answer_1: {type: String, required: true},
	incorrect_answer_2: {type: String, required: true},
	incorrect_answer_3: {type: String, required: true},
	courseID: { type: mongoose.Schema.Types.ObjectId, ref: 'Course', required:true},
	difficulty: Number,
	creator_uID: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
	verified: Boolean,
	reported: Boolean
});

var ObjectId = require('mongodb').ObjectID;
var Question = mongoose.model('Question', questionSchema);
module.exports = Question;

module.exports.getGameQuestions = (courseID, callback) => {
	//User.find({courseID : courseID}, callback);
	Question.aggregate([
		//{ $match: { "courseID": ObjectId(courseID) }},
		{$sample: {size: 7}}]).exec(callback);
}