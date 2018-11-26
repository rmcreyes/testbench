// const axios = require('axios');
//  var Main = require('../index');
// var should = require('should');

// var ep = 'http://40.78.64.46:3300/';
// var jwt = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJDb2RlV29ya3IiLCJzdWIiOiI1YmVkMjNkODIyNWUyYjdiM2MyYTBkZWEiLCJpYXQiOjE1NDI0ODg2NzUxNjYsImV4cCI6MTU0NTA4MDY3NTE2Nn0.HFjfsSAV8oaIVI6-FzbZl8mVxOA9hFdJEtZRKx3MBZE';

// describe('APIs', function() {
//     it('should be able to get a user by email', function(done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/user/email/?email=hm@bob.ca',
//             { headers: { Authorization: jwt}})
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data[0];
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
//             pass.should.equal(1);
//             if(pass == 1) {
//                 g_response.name.should.equal('Bob #3');
//                 g_response.email.should.equal('hm@bob.ca');
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get a user by user id', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/user/?id=5bed23d8225e2b7b3c2a0dea&fbclid=IwAR3yEGy-ERYrtNU84m8Io32YAbu-i5rgZ7jqIgQt6Knv47Hti4V-RjVbqco',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//                 g_response.name.should.equal('Andrea Mah');
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });


//     it('should be able to authorize to get a JWT token', function(done) {
//         var pass = 0;
//         var g_response;
//         axios.post(ep + 'users/oauth/facebook',
//             {
//                 //this access token expires within hours, safe to post to github
//                 'access_token': 'EAAOCp2lOdtMBAHoPcFAVzAlOcFECAwncKFdP9iyNofZC9RhQj9f95WqWkutgUoRu3TVNWZBFXPDv3LzwpFh1HgJxuD9mv7rgqVPF1dBWe2K2Ij3YK4ZCCeZAtZANGQLZCZBdgIksGxf0ToPEq4Knee6DZBySlCiyRgP6GwfJMJ86oiS9KdSCOjnaw9IR6DmSiM7KWMGaVRwpUgZDZD'
//             }
            
//             )
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
//             pass.should.equal(1);
//             if(pass == 1) {
//                 g_response.token.should.not.equal('')
//             }
//             done();
//         }
//         setTimeout(completeTest, 400);
//     });


    
//     it('should be able to make a question', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.post(ep + 'api/question',
//             {
//             'question_text':'Test?',
//             'correct_answer':'correct_ans',
//             'incorrect_answer_1':'incorrect_ans_1',
//             'incorrect_answer_2':'incorrect_ans_2',
//             'incorrect_answer_3':'incorrect_ans_3',
//             'course_subject':'ELEC',
//             'course_number':221,
//             'creator_uID':'5bd542d5d786bb617630f1ad',
//             'verified':'true'
//             },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
                
            
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);
//              if(pass == 1) {
//                  //ensure fields are correct
//                 g_response.question.correct_answer.should.equal('correct_ans');
//                 g_response.question.incorrect_answer_1.should.equal('incorrect_ans_1');
//                 g_response.question.incorrect_answer_2.should.equal('incorrect_ans_2');
//                 g_response.question.incorrect_answer_3.should.equal('incorrect_ans_3');
                                
//              }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to make a course', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.post(ep + 'api/course',
//         {
//             "course_subject":"CPEN",
//             "course_number":"100"
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
                
            
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);
//             if(pass == 1) {
//                 //ensure fields are correct
//                g_response.user.course_subject.should.equal('CPEN');
//                g_response.user.course_number.should.equal(100);        
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('Should not be able to delete a course with invalid id', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.delete(ep + 'api/course/5bf08c8f497c6a52b979299b'
//             ,{ headers: { Authorization: jwt}})
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
                
            
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);

//             if(pass == 1) {
//                 //ensure it didn't go through
//                 g_response.n.should.equal(0);                 

                                
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });
    

//     it('Should not be able to delete a question with invalid id', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.delete(ep + 'api/question/5bf08c8f497c6a52b979299b'
//             ,{ headers: { Authorization: jwt}})
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
                
            
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);

//             if(pass == 1) {
//                 //ensure it didn't go through
//                 g_response.n.should.equal(0);                 

                                
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('Should not be able to delete a user with invalid id', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.delete(ep + 'api/user/5bf08c8f497c6a52b979299b'
//             ,{ headers: { Authorization: jwt}})
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
                
            
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);

//             if(pass == 1) {
//                 //ensure it didn't go through
//                 g_response.n.should.equal(0);                 

                                
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get the top 3 users of a course', function(done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/sortedusers/?id=5bd6587ff1cd757d7655b66a&course_subject=CPEN&course_number=321',
//             { headers: { Authorization: jwt}})
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
//             pass.should.equal(1);
//             if(pass == 1) {
//                 g_response.length.should.be.belowOrEqual(3);
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get at most 7 questions to play with', function(done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/getgame/?course_subject=CPEN&course_number=321',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//                 g_response.length.should.be.belowOrEqual(7);
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should not be able to get game questions with invalid course', function(done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/getgame/?course_subject=CPEN&course_number=322',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(-1);

//             done();
//         }
//         setTimeout(completeTest, 100);            
//     });


//     it('should be able to update username', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/user/username/5bed23d8225e2b7b3c2a0dea',
//         {
//             "username":"foob"
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });


//     it('should be not able to update username with existing one', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/user/username/5bed23d8225e2b7b3c2a0dea',
//         {
//             "username":"test"
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(-1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

    


//     it('should be able to add a course', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
//         {
//             "course_subject": "MATH",
//             "course_number": 121
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be not able to add a course twice', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
//         {
//             "course_subject": "MATH",
//             "course_number": 121
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(-1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to delete a course', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
//         {
//             "course_subject": "MATH",
//             "course_number": 121
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should not be able to delete a course twice', function(done) {
//         var pass = 0;
//         var g_response;

//         axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
//         {
//             "course_subject": "MATH",
//             "course_number": 121
//         },{ headers: { Authorization: jwt}})
            
            
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function() {
            
//             pass.should.equal(-1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get a course by its subject and number', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/course/?course_number=253&course_subject=MATH',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data[0];
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//                 g_response.course_number.should.equal(253);
//                 g_response.course_subject.should.equal('MATH');
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get all courses of a certain subject', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/course/subject/?course_subject=CPEN',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//                 g_response.data.length.should.be.aboveOrEqual(64);
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to add a stat', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/addnewstat/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "course_subject": "CPEN",
//                 "course_number": 321
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to get the stat object of a user', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.get(ep + 'api/getstats/?id=5bed23d8225e2b7b3c2a0dea&course_subject=CPEN&course_number=321',
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response.data[0];
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//                 g_response.stats_list.length.should.equal(1);
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to update stat', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/updatestat/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "add_correctness_rate":0.33,
//                 "add_response_time":8,
//                 "level_progress":2,
//                 "course_subject":"CPEN",
//                 "course_number":321
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to update stat again', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/updatestat/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "add_correctness_rate":0.33,
//                 "add_response_time":8,
//                 "level_progress":20,
//                 "course_subject":"CPEN",
//                 "course_number":321
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to add a course to the user', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "course_subject": "MATH",
//                 "course_number": 121
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });

//     it('should be able to delete a course to the user', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "course_subject": "MATH",
//                 "course_number": 121
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             if (pass == 1) {
//             }
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });


 
//     it('should not be able to delete a nonexitent course to the user', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "course_subject": "MAT",
//                 "course_number": 121
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(-1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });
    
    


//     it('should not be able to delete a stat from a user', function (done) {
//         var pass = 0;
//         var g_response;
//         axios.put(ep + 'api/deletestat/5bed23d8225e2b7b3c2a0dea',
//             {
//                 "course_subject": "CPEN",
//                 "course_number": 321
//             },
//             { headers: { Authorization: jwt } })
//             .then(response => {
//                 pass = 1;
//                 g_response = response;
//             })
//             .catch(error => {
//                 pass = -1;
//             });
//         var completeTest = function () {
//             pass.should.equal(1);
//             done();
//         }
//         setTimeout(completeTest, 100);
//     });      

// });