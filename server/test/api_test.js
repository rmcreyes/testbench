const axios = require('axios');
var Main = require('../index');
var should = require('should');

var ep = 'http://40.78.64.46:3300/';
var jwt = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJDb2RlV29ya3IiLCJzdWIiOiI1YmVkMjNkODIyNWUyYjdiM2MyYTBkZWEiLCJpYXQiOjE1NDI0ODg2NzUxNjYsImV4cCI6MTU0NTA4MDY3NTE2Nn0.HFjfsSAV8oaIVI6-FzbZl8mVxOA9hFdJEtZRKx3MBZE';

describe('APIs', function() {
    it('should be able to get a user by email', function(done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/user/email/?email=hm@bob.ca',
            { headers: { Authorization: jwt}})
            .then(response => {
                pass = 1;
                g_response = response.data[0];
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            pass.should.equal(1);
            if(pass == 1) {
                g_response.name.should.equal('Bob #3');
                g_response.email.should.equal('hm@bob.ca');
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to get a user by user id', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/user/?id=5bed23d8225e2b7b3c2a0dea&fbclid=IwAR3yEGy-ERYrtNU84m8Io32YAbu-i5rgZ7jqIgQt6Knv47Hti4V-RjVbqco',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
                g_response.name.should.equal('Andrea Mah');
            }
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to authorize to get a JWT token', function(done) {
        var pass = 0;
        var g_response;
        axios.post(ep + 'users/oauth/facebook',
            {
                //this access token expires within hours, safe to post to github
                'access_token': 'EAAOCp2lOdtMBANye9IEjlfBTNFwbQ1icabG6ze3t9meTQUPQCWYLlk3Jzz9bIyfhrJvkWTYZBbautNEvXzHitLN5IheEeB7Tvd3ZBnvFZCklteEdhKlp79NgXzRss1DFNtOMDk0vq6AWghwH4VNcihrkg3LLRlgO1OnMWDAFjfEotRZAwZCsvBXrYO8bOjoHWxlPJAok1DoJB88sZCISiR7R5UQyqWZAmoZD'
            }
            
            )
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            pass.should.equal(1);
            if(pass == 1) {
                g_response.token.should.not.equal('')
            }
            done();
        }
        setTimeout(completeTest, 400);
    });


    
    it('should not be able to make a question', function(done) {
        var pass = 0;
        var g_response;

        axios.post(ep + 'api/question',
            {
            'question_text':'Test?',
            'correct_answer':'correct_ans',
            'incorrect_answer_1':'correct_ans',
            'incorrect_answer_2':'incorrect_ans_2',
            'incorrect_answer_3':'incorrect_ans_3',
            'course_subject':'ELEC',
            'course_number':221,
            'creator_uID':'5bd542d5d786bb617630f1ad',
            'verified':'true'
            },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
                
            
            });
        var completeTest = function() {
            //ensure that question cannot be made
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    // it('should be able to make a course', function(done) {
    //     var pass = 0;
    //     var g_response;

    //     axios.post(ep + 'api/course',
    //     {
    //         "course_subject":"CPEN",
    //         "course_number":"100"
    //     },{ headers: { Authorization: jwt}})
            
            
    //         .then(response => {
    //             pass = 1;
    //             g_response = response.data;
    //         })
    //         .catch(error => {
    //             pass = -1;
                
            
    //         });
    //     var completeTest = function() {
            
    //         pass.should.equal(1);
    //         if(pass == 1) {
    //             //ensure fields are correct
    //            g_response.user.course_subject.should.equal('CPEN');
    //            g_response.user.course_number.should.equal(100);        
    //         }
    //         done();
    //     }
    //     setTimeout(completeTest, 100);
    // });

    // it('Should not be able to delete a course with invalid id', function(done) {
    //     var pass = 0;
    //     var g_response;

    //     axios.delete(ep + 'api/course/5bf08c8f497c6a52b979299b'
    //         ,{ headers: { Authorization: jwt}})
            
    //         .then(response => {
    //             pass = 1;
    //             g_response = response.data;
    //         })
    //         .catch(error => {
    //             pass = -1;
                
            
    //         });
    //     var completeTest = function() {
            
    //         pass.should.equal(1);

    //         if(pass == 1) {
    //             //ensure it didn't go through
    //             g_response.n.should.equal(0);                 

                                
    //         }
    //         done();
    //     }
    //     setTimeout(completeTest, 100);
    // });
    

    it('Should not be able to delete a question with invalid id', function(done) {
        var pass = 0;
        var g_response;

        axios.delete(ep + 'api/question/5bf08c8f497c6a52b979299b'
            ,{ headers: { Authorization: jwt}})
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
                
            
            });
        var completeTest = function() {
            
            pass.should.equal(1);

            if(pass == 1) {
                //ensure it didn't go through
                g_response.n.should.equal(0);             
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    // it('Should not be able to delete a user with invalid id', function(done) {
    //     var pass = 0;
    //     var g_response;

    //     axios.delete(ep + 'api/user/5bf08c8f497c6a52b979299b'
    //         ,{ headers: { Authorization: jwt}})
            
    //         .then(response => {
    //             pass = 1;
    //             g_response = response.data;
    //         })
    //         .catch(error => {
    //             pass = -1;
                
            
    //         });
    //     var completeTest = function() {
            
    //         pass.should.equal(1);

    //         if(pass == 1) {
    //             //ensure it didn't go through
    //             g_response.n.should.equal(0);                 

                                
    //         }
    //         done();
    //     }
    //     setTimeout(completeTest, 100);
    // });

    it('should be able to get the top 3 users of a course', function(done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/sortedusers/?id=5bd6587ff1cd757d7655b66a&course_subject=CPEN&course_number=321',
            { headers: { Authorization: jwt}})
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            pass.should.equal(1);
            if(pass == 1) {
                g_response.length.should.be.belowOrEqual(3);
                g_response[0].stats_list.rank.should.be.aboveOrEqual(g_response[1].stats_list.rank);
                g_response[1].stats_list.rank.should.be.aboveOrEqual(g_response[2].stats_list.rank);
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    // it('should be able to get at most 7 questions to play with', function(done) {
    //     var pass = 0;
    //     var g_response;
    //     axios.get(ep + 'api/getgame/?course_subject=CPEN&course_number=321',
    //         { headers: { Authorization: jwt } })
    //         .then(response => {
    //             pass = 1;
    //             g_response = response.data;
    //         })
    //         .catch(error => {
    //             pass = -1;
    //         });
    //     var completeTest = function () {
    //         pass.should.equal(1);
    //         if (pass == 1) {
    //             g_response.length.should.be.belowOrEqual(7);
    //         }
    //         done();
    //     }
    //     setTimeout(completeTest, 100);
    // });

    // it('should not be able to get game questions with invalid course', function(done) {
    //     var pass = 0;
    //     var g_response;
    //     axios.get(ep + 'api/getgame/?course_subject=CPEN&course_number=322',
    //         { headers: { Authorization: jwt } })
    //         .then(response => {
    //             pass = 1;
    //             g_response = response.data;
    //         })
    //         .catch(error => {
    //             pass = -1;
    //         });
    //     var completeTest = function () {
    //         pass.should.equal(-1);

    //         done();
    //     }
    //     setTimeout(completeTest, 100);            
    // });


    it('should be able to update username', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/user/username/5bed23d8225e2b7b3c2a0dea',
        {
            "username":"newuser"
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            //put request only relies on response
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be not able to update username with existing one', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/user/username/5bed23d8225e2b7b3c2a0dea',
        {
            "username":"test"
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            //should not be able to be successful
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });



    it('should be able to add a course to a user', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
        {
            "course_subject": "MATH",
            "course_number": 121
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            //should pass
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be not able to add a course twice', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
        {
            "course_subject": "MATH",
            "course_number": 121
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            //should fail, as previous test added the same course
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to delete a course', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
        {
            "course_subject": "MATH",
            "course_number": 121
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            //should be able to delete the course we just made
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should not be able to delete a course twice', function(done) {
        var pass = 0;
        var g_response;

        axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
        {
            "course_subject": "MATH",
            "course_number": 121
        },{ headers: { Authorization: jwt}})
            
            
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function() {
            
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to get questions by searching course by its subject and number', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/getcoursequestions/?course_number=253&course_subject=MATH',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data[0];
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
                //ensure that all questions have required fields 
                for(var g_resp in g_response)
                {
                    g_resp.question_text !== undefined;
                    g_resp.correct_answer !== undefined;
                    g_resp.incorrect_answer_1 !== undefined;
                    g_resp.incorrect_answer_2 !== undefined;
                    g_resp.incorrect_answer_3 !== undefined;
                    g_resp.courseID !== undefined;
                }
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    // it('should be able to get all courses of a certain subject', function (done) {
    //     var pass = 0;
    //     var g_response;
    //     axios.get(ep + 'api/course/subject/?course_subject=CPEN',
    //         { headers: { Authorization: jwt } })
    //         .then(response => {
    //             pass = 1;
    //             g_response = response;
    //         })
    //         .catch(error => {
    //             pass = -1;
    //         });
    //     var completeTest = function () {
    //         pass.should.equal(1);
    //         if (pass == 1) {
    //             g_response.data.length.should.be.aboveOrEqual(64);
    //         }
    //         done();
    //     }
    //     setTimeout(completeTest, 100);
    // });

    it('should be able to add a stat', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/addnewstat/5bed23d8225e2b7b3c2a0dea',
            {
                "add_correctness_rate":0.33,
                "add_response_time":8,
                "level_progress":1,
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should not be able to add a stat twice', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/addnewstat/5bed23d8225e2b7b3c2a0dea',
            {
                "add_correctness_rate":0.33,
                "add_response_time":8,
                "level_progress":2,
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to get the stat object of a user', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/getstats/?id=5bed23d8225e2b7b3c2a0dea&course_subject=MATH&course_number=184',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
                g_response[0].stats_list.length.should.equal(1);
                g_response[0].stats_list[0].rank.should.equal(1);
                g_response[0].stats_list[0].avg_response_time.should.equal(8);
                g_response[0].stats_list[0].correctness_rate.should.equal(0.33);
                g_response[0].stats_list[0].level_progress.should.equal(1);

            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to update stat without ranking up', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/updatestat/5bed23d8225e2b7b3c2a0dea',
            {
                "add_correctness_rate":0.33,
                "add_response_time":8,
                "level_progress":2,
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
                g_response.ranked_up.should.equal(false);
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to update stat again and rank up', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/updatestat/5bed23d8225e2b7b3c2a0dea',
            {
                "add_correctness_rate":0.33,
                "add_response_time":8,
                "level_progress":20,
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
                g_response.ranked_up.should.equal(true);
            }
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to delete a stat from a user', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/deletestat/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });  

    it('should not be able to delete a stat from a user again', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/deletestat/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 184
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });  
///////////////////////////////////////////

    it('should be able to add a course to the user', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/addcourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
            }
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to delete a course to the user', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if (pass == 1) {
            }
            done();
        }
        setTimeout(completeTest, 100);
    });


 
    it('should not be able to delete a nonexitent course to the user', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MAT",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });
    
    
    it('should not be able to find a badly formatted course', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/deletecourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MAT",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(-1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to find a correctly formatted course', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/course/?course_number=121&course_subject=MATH',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            if(pass == 1) {
                g_response[0]._id.should.equal('5be8a1d9bd936daae4189628');
            }
            done();
        }
        setTimeout(completeTest, 100);
    });
    it('adds to a question rating', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/updaterating/5bf74f787a021335da3f717a',
            {
                "new_rating": 4
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('makes a user a professor', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/professor/5bed23d8225e2b7b3c2a0dea',
            {
                "is_professor":true
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });
    it('mark a users reported boolean', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/user/reported/5bed23d8225e2b7b3c2a0dea',
            {
                "reported":false
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('mark a questions reported boolean', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/question/reported/5bf74f787a021335da3f717a',
            {
                "reported":false
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('mark a questions verified boolean', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/question/verified/5bf74f787a021335da3f717a',
            {
                "reported":false
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to add a course to the users professor list', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/addprofessorcourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to delete a course from the users professor list', function (done) {
        var pass = 0;
        var g_response;
        axios.put(ep + 'api/deleteprofcourse/5bed23d8225e2b7b3c2a0dea',
            {
                "course_subject": "MATH",
                "course_number": 121
            },
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });



    it('should be able to find courses that you are a professor of', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/getprofcourses/?id=5bed23d8225e2b7b3c2a0dea',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to find a users courses', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/getcourses/?id=5bed23d8225e2b7b3c2a0dea',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });


    it('should be able to find the questions in a course', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/getcoursequestions/?course_subject=CPEN&course_number=321',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

    it('should be able to find the number of questions in a course', function (done) {
        var pass = 0;
        var g_response;
        axios.get(ep + 'api/questioncount/?course_subject=CPEN&course_number=311',
            { headers: { Authorization: jwt } })
            .then(response => {
                pass = 1;
                g_response = response.data;
            })
            .catch(error => {
                pass = -1;
            });
        var completeTest = function () {
            pass.should.equal(1);
            done();
        }
        setTimeout(completeTest, 100);
    });

});