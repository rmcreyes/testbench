var mongoose = require('mongoose');
var userSchema = mongoose.Schema;

var questionSchema = mongoose.Schema({
	question_text: {type: String, required: true},
	correct_answer: {type: String, required: true},
	incorrect_answer_1: {type: String, required: true},
	incorrect_answer_2: {type: String, required: true},
	incorrect_answer_3: {type: String, required: true},
	courseID: { type: mongoose.Schema.Types.ObjectId, ref: 'Course', required:true},
	rating: Number,
	rating_count: Number,
	creator_uID: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
	verified: Boolean,
	reported: Boolean
});

var ObjectId = require('mongodb').ObjectID;
var Question = mongoose.model('Question', questionSchema);
module.exports = Question;

module.exports.getGameQuestions = (courseID, callback) => {
	Question.aggregate([   
		{ $match: 
			{ $and: [
				{'courseID': ObjectId(courseID)},
				{'reported': false}
			]}},
		{ $sample: {size: 7} } 
	]
	).exec(callback);
}

module.exports.getCourseQuestions = (courseID, callback) => {
	Question.aggregate([   
		{ $match: {'courseID': ObjectId(courseID)} }
	]
	).exec(callback);
}

module.exports.removeQuestion = (id, callback) => {
	var query = {_id: id};
	Question.remove(query, callback);
}

module.exports.getQuestionById = (id,select, callback) => {
	Question.findById(id, select,callback);
}


module.exports.updateQuestionReportedStatus = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		reported: user.reported
	}
	Question.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateVerifiedStatus = (id, user, options, callback) => {
	var query = {_id: id};
	var update = {
		verified: user.verified
	}
	Question.findOneAndUpdate(query, update, options, callback);
}

module.exports.updateRating = (id, user, options, callback) => {
	
	var query = {_id: id};
	var update = {
		rating: user.new_rating,
		rating_count: user.rating_count
	}
	Question.findOneAndUpdate(query, update, options, callback);
}

module.exports.returnQuestionNum = (user, callback) => {
	Question.aggregate([
		{ $match: 
			{ $and: [ 
				{'courseID': ObjectId(user.courseID)},
				{"reported": false }
			]}},
    	{
      		$count: "current_count"
    	}
	]).exec(callback); 
}