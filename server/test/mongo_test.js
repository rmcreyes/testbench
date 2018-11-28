var expect = require('chai').expect;
var sinon = require('sinon');
const test = require('sinon-test')(sinon);
var ObjectId = require('mongodb').ObjectID;

var User = require('../models/user');
var Course = require('../models/course');
var Question = require('../models/question');

describe('User', function() {
    beforeEach(function () {
        sinon.stub(User, 'findById');
        sinon.stub(User, 'find');
        sinon.stub(User, 'findOneAndUpdate');
        sinon.stub(User, 'remove');
        var mockAggregate = {
            where: function () {
                return this;
            },
            equals: function () {
                return this;
            },
            exec: function (callback) {
                callback(null, "some fake expected return value");
            }
        };
        sinon.stub(User, "aggregate").returns(mockAggregate);
    });

    afterEach(function () {
        User.findById.restore();
        User.find.restore();
        User.findOneAndUpdate.restore();
        User.remove.restore();
        User.aggregate.restore();
    });

    it('should be invalid if name is empty', function(done) {
        var user = new User();
        user.validate(function(err) {
            expect(err.errors.name).to.exist;
            done();
        });
    });

    it('should be invalid if email is empty', function (done) {
        var user = new User();
        user.validate(function(err) {
            expect(err.errors.email).to.exist;
            done();
        });
    });
    
    it('should be invalid if profile_photo_id is empty', function(done) {
        var user = new User();
        user.validate(function(err) {
            expect(err.errors.profile_photo_id).to.exist;
            done();
        });
    });

    it('should be able to find a user by their id', test(function() {
        var expected_id = '123';
        User.getUserById(expected_id, function(){});
        sinon.assert.calledWith(User.findById, '123');
    }))

    it('should be able to find a user by their email', test(function() {
        var expected_email = 'segfault@testbench.com';
        User.getUserByEmail(expected_email, function(){});
        sinon.assert.calledWith(User.find, {
            email: 'segfault@testbench.com'
        });
    }));

    it('should be able to add a course', test(function () {
        var id = 'some id';
        var user = {
            "name": "some name",
            "profile_photo_id": "some id"
        };
        var query = {_id: "some id"};
        var update = {
            name: "some name",
            profile_photo_id: "some id"
        }
        User.addCourse(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update);
    }));

    it('should be able to udpdate a user\'s course list', test(function() {
        var id = 'some id'
        var query = {_id: 'some id'};
        var user = {
            "courseID" : "1111111111"
        };
        var update = {
            $addToSet: {
                course_list: "1111111111",
            }
        };
        User.addCourseToUser(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, {});
    }));

    it('should be able to remove a user by id', test(function() {
        var id = 'some id';
        var query = {_id: 'some id'};
        User.removeUser(id), function(){};
        sinon.assert.calledWith(User.remove, query);
    }))

    it('should be able to update the user\'s profile', test(function() {
        var id = 'some id';
        var query = {_id: id};
        var user = {
            "alias" : "some alias",
            "username" : "some username"
        };
        var update = {
            alias: "some alias",
            username: "some username"
        };
        User.updateUserProfile(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, {});
    }));

    it('should be able to add a statistic to a user', test(function() {
        var id = 'some id';
        var user = {
            "courseID" : 'some id',
            "add_response_time" : 2.2,
            "add_correctness_rate" : 0.58,
            "level_progress" : 2
        };
        var query = {_id: 'some id'};
        var update = {
            $addToSet: {
                stats_list: {
                    course_code: 'some id',
                    rank: 1,
                    avg_response_time: 2.2,
                    correctness_rate: 0.58,
                    num_stat_contributions: 1,
                    level_progress: 2,
                    level_max: 8
                }
            }
        };
        User.addStatToUser(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, {});
    }));

    it('should be able to delete a course from a user\'s course list', test(function() {
        var id = 'some id';
        var user = {
            "courseID" : "AAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var query = { _id: 'some id' };
        var update = {
            $pull: {
                course_list: {
                    $in: [ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA")]
                }
            }
        };
        User.deleteCourseFromUser(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, {}); 
    }));

    it('should be able to delete a stat from a user\'s statistics', test(function () {
        var id = 'some id';
        var user = {
            "courseID": "AAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var query = { _id: 'some id' };
        var update = {
            $pull: {
                stats_list: {
                    course_code: {
                        $in: [ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA")]
                    }
                }
            }
        };
        User.deleteStatFromUser(id, user, {}, function () {});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, {});
    }));

    it('should be able to get a user\'s course stat', test(function(){
        var user = {
            "id": "BBBBBBBBBBBBBBBBBBBBBBBB",
            "courseID": "AAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var query = {
            _id: "BBBBBBBBBBBBBBBBBBBBBBBB",
            stats_list: {
                $elemMatch: { course_code: ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA")}
            }
        }
        var select = {
            "stats_list.$": "AAAAAAAAAAAAAAAAAAAAAAAA"
        }
        User.getUserStatByCourse(user, function(){});
        sinon.assert.calledWith(User.find, query, select);
    }));

    it('should be able to update a user\'s statistics', test(function() {
        var user = {
            "id": "some id",
            "courseID": "AAAAAAAAAAAAAAAAAAAAAAAA",
            "new_correctness_rate": 0.7,
            "new_response_time": 1,
            "level_progress": 3
        };

        var query = {
            _id: "some id",
            stats_list: {
                $elemMatch: {
                    course_code: "AAAAAAAAAAAAAAAAAAAAAAAA"
                }
            }
        }
        var update = {
            $set: {
                'stats_list.$.correctness_rate': 0.7,
                'stats_list.$.avg_response_time': 1
            },
            $inc: {
                'stats_list.$.level_progress': 3,
                'stats_list.$.num_stat_contributions': 1
            }
        }
        var options = { "stats_list.$": "AAAAAAAAAAAAAAAAAAAAAAAA" }
        User.UpdateStatData(user, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, options);
    }));

    it('should be able to update a user\'s rank', test(function() {
        var user = {
            "id" : "some id",
            "courseID" : "some course"
        };
        var query = {
            _id: 'some id',
            stats_list: {
                $elemMatch: {
                    course_code: "some course"
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
        var options = { "stats_list.$": "some course"}
        User.UpdateRankData(user, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update, options);
    })); 

    it('should be able to update a user\'s username', test(function() {
        var id = 'some id';
        var user = {
            "username": "some username"
        };
        var query = {_id: "some id"};
        var update = {
            username: "some username"
        };
        User.updateUsername(id, user, {}, function(){});
        sinon.assert.calledWith(User.findOneAndUpdate, query, update);
    }));

    it('should be able to update a user\'s email', test(function () {
        var id = 'some id';
        var user = {
            "email": "some email"
        };
        var query = { _id: "some id" };
        var update = {
            email: "some email"
        };
        User.updateEmail(id, user, {}, function () { });
        sinon.assert.calledWith(User.findOneAndUpdate, query, update);
    }));

    it('should be able to get a user\'s course id', test(function () {
        var id = 'some id';
        var user = {                        
            "courseID": "AAAAAAAAAAAAAAAAAAAAAAAA",
        };
        var query = {
            _id: "some id",
            course_list: {
                $in: [ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA")]
            }
        }
        User.getUserCourse(id, user, function(){});
        sinon.assert.calledWith(User.find, query);
    }));

    it('should be able to get the top 3 highest rank players in a course', test(function () {
        var courseID = "AAAAAAAAAAAAAAAAAAAAAAAA";
        User.returnHighestRank(courseID, function(){});
        sinon.assert.calledWith(User.aggregate,
            [
                { $match: { "stats_list.course_code": ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA") } },
                { $unwind: '$stats_list' },
                { $match: { 'stats_list.course_code': { $eq: ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA") } } },
                { $sort: { "stats_list.rank": -1, "stats_list.level_progress": -1 } },
                { $limit: 3 }
            ]);
    }));

    it('should be able to return the user\'s rank', test(function() {
        var user = {
            "courseID": "AAAAAAAAAAAAAAAAAAAAAAAA",
            "level_progress": 1,
            "rank": 15
        };
        User.returnRank(user, function(){});
        sinon.assert.calledWith(User.aggregate, 
            [
                { $match: { "stats_list.course_code": ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA") } },
                { $unwind: '$stats_list' },
                { $match: { 'stats_list.course_code': { $eq: ObjectId("AAAAAAAAAAAAAAAAAAAAAAAA") } } },
                {
                    $match: {
                        $or: [
                            { "stats_list.rank": { $gt: 15 } },
                            {
                                $and: [
                                    { "stats_list.rank": { $eq: 15 } },
                                    { "stats_list.level_progress": { $gt: 1 } }
                                ]
                            }
                        ]
                    }
                },
                {
                    $count: "current_rank"
                }
            ])
    }));
});

describe('Course', function() {
    beforeEach(function () {
        sinon.stub(Course, 'find');
        sinon.stub(Course, 'remove');
    });

    afterEach(function () {
        Course.find.restore();
        Course.remove.restore();
    });

    it('should be invalid if course_number is empty', function(done) {
        var course = new Course();
        course.validate(function (err) {
            expect(err.errors.course_number).to.exist;
            done();
        });
    });

    it('should be invalid if course_subject is empty', function (done) {
        var course = new Course();
        course.validate(function (err) {
            expect(err.errors.course_subject).to.exist;
            done();
        });
    });

    it('should be able to find courses by their subject', test(function() {
        var course = {
            "course_subject": "CPEN"
        }
        var query = {
            course_subject: "CPEN"
        }
        Course.getCourseBySubject(course, function(){});
        sinon.assert.calledWith(Course.find, query);
    }));

    it('should be able to find a specific course', test(function () {
        var course = {
            "course_subject": "CPEN",
            "course_number": 321
        }
        var query = {
            course_subject: "CPEN",
            course_number: 321
        }
        Course.getCourse(course, function () { });
        sinon.assert.calledWith(Course.find, query);
    }));

    it('should be able to find user\'s course list', test(function () {
        var idArr = ["5be8a1d9bd936daae4188c4c", "5be8a1d9bd936daae4188c4b", "5bd920f81d73ab5431db2fcc"];
        Course.getUserCoursesById(idArr, function(){});
        sinon.assert.calledWith(Course.find, { '_id': { $in: idArr.map(ObjectId) } });
    }));

});

describe('Question', function() {
    beforeEach(function () {
        sinon.stub(Question, 'find');
        var mockAggregate = {
            where: function () {
                return this;
            },
            equals: function () {
                return this;
            },
            exec: function (callback) {
                callback(null, "some fake expected return value");
            }
        };
        sinon.stub(Question, "aggregate").returns(mockAggregate);
        sinon.stub(Question, 'remove');
    });

    afterEach(function () {
        Question.aggregate.restore();
        Question.find.restore();
        Question.remove.restore();
    });

    it('should be invalid if question_text is empty', function(done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.question_text).to.exist;
            done();
        });
    });

    it('should be invalid if correct_answer is empty', function (done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.correct_answer).to.exist;
            done();
        });
    });

    it('should be invalid if incorrect_answer_1 is empty', function (done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.incorrect_answer_1).to.exist;
            done();
        });
    });

    it('should be invalid if incorrect_answer_2 is empty', function (done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.incorrect_answer_2).to.exist;
            done();
        });
    });

    it('should be invalid if incorrect_answer_3 is empty', function (done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.incorrect_answer_3).to.exist;
            done();
        });
    });

    it('should be invalid if courseID is empty', function (done) {
        var question = new Question();
        question.validate(function (err) {
            expect(err.errors.courseID).to.exist;
            done();
        });
    });

    it('should be able to fetch 7 questions from a course via courseID', test(function() {
        var courseID = "5bd2b67601db5d121920c48d";
        Question.getGameQuestions(courseID, function(){});
        sinon.assert.calledWith(Question.aggregate, [
            {
                $match:
                {
                    $and: [
                        { 'courseID': ObjectId("5bd2b67601db5d121920c48d") },
                        { 'reported': false }
                    ]
                }
            },
            { $sample: { size: 7 } }
        ]);
    }));

    it('should be able to remove a question from the db', test(function() {
        var id = "5be8a1d9bd936daae4188c4c";
        var query = { _id: "5be8a1d9bd936daae4188c4c" };
        Question.removeQuestion(id, function () { });
        sinon.assert.calledWith(Question.remove, query);
    }));
});