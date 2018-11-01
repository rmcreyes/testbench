var expect = require('chai').expect;
var sinon = require('sinon');
const test = require('sinon-test')(sinon);

var User = require('../models/user');

describe('User', function() {
    beforeEach(function () {
        sinon.stub(User, 'findById');
        sinon.stub(User, 'find');
    });

    afterEach(function () {
        User.findById.restore();
        User.find.restore();
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
        sinon.assert.calledWith(User.findById, expected_id);
    }))

    it('should be able to find a user by their email', test(function() {
        var expected_email = 'segfault@testbench.com';
        User.getUserByEmail(expected_email, function(){});
        sinon.assert.calledWith(User.find, {
            email: expected_email
        });
    }));

    it('should be able to find a user by any query model', test(function() {
        var expected_name = 'some_name';
        var expected_email = 'some_email';
        var query = {name : expected_name, email : expected_email};
        User.aggregateUser(query, function(){});
        sinon.assert.calledWith(User.find, query);
    }));

    
});