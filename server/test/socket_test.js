var should = require('should');
var io = require('socket.io-client');

var socketURL = 'http://40.78.64.46:3300';
var Main = require('../index');

var options = {
    transports: ['websocket'],
    'force new connection': true
};

var player1 = {
    'username': 'Caelin',
    'course': "CPEN321"
}
var player1_s = JSON.stringify(player1);

var player2 = {
    'username': 'Johnny',
    'course': "CPEN321"
}
var player2_s = JSON.stringify(player2);

var player3 = {
    'username': 'Richard',
    'course': "CIVL250"
}
var player3_s = JSON.stringify(player3);

var player4 = {
    'username': 'Yaash',
    'course': "CPEN321"
}
var player4_s = JSON.stringify(player4);

describe('Matchmaking', function() {
    it('should be able to recognize if two players can match', function(done) {
        var client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('queue_for_game', player1_s);
        });
        client1.on('game_made', function (msg) {
            msg.should.be.oneOf(player1.username + ' ' + player2.username, player2.username + ' ' + player1.username);
            client1.disconnect();
        });

        var client2 = io.connect(socketURL, options);
        client2.on('connect', function (data) {
            client2.emit('queue_for_game', player2_s);
        });
        client2.on('game_made', function(msg) {
            msg.should.be.oneOf(player1.username + ' ' + player2.username, player2.username + ' ' + player1.username);
            client2.disconnect();
            done();
        });
    });

    it('should not let two players match if they are not looking for the same course', function(done) {
        var client1, client3;
        var matched = 0;

        var completeTest = function() {
            matched.should.equal(0);
            client1.disconnect();
            client3.disconnect();
            done();
        }

        var client1 = io.connect(socketURL, options);
        client1.on('connect', function (data) {
            client1.emit('queue_for_game', player1_s);
        });

        var client3 = io.connect(socketURL, options);
        client3.on('connect', function (data) {
            client3.emit('queue_for_game', player3_s);
            client3.on('game_made', function(msg) {
                matched = 1;
            });
            setTimeout(completeTest, 100);
        });
    });

    it('should let a player stop waiting for a match', function(done) {
        var client1, client2;
        var matched = 0;

        var completeTest = function () {
            matched.should.equal(0);
            client1.disconnect();
            client2.disconnect();
            done();
        }

        client1 = io.connect(socketURL, options);
        client1.on('connect', function (data) {
            client1.emit('queue_for_game', player1_s);
            client1.emit('stop_waiting', 'stop waiting');
            client1.on('game_made', function (msg) {
                matched = 1;
            });
            client2 = io.connect(socketURL, options);
            client2.on('connect', function (data) {
                client2.emit('queue_for_game', player2_s);
            });
            setTimeout(completeTest, 100);
        });
    });

    it('should be able inform players about their opponents', function(done) {
        var count = 0;
        var completeTest = function() {
            if(count == 2)
                done();
        }

        var client1 = io.connect(socketURL, options);
        client1.on('connect', function (data) {
            client1.emit('queue_for_game', player1_s);
        });
        client1.on('game_made', function (msg) {
            client1.emit('send_json_opponent', 'send json opponent');
            client1.on('get_json_opponent', function(msg) {
                msg.should.equal(player2_s);
                client1.disconnect();
                count++;
                completeTest();
            });
        });

        var client2 = io.connect(socketURL, options);
        client2.on('connect', function (data) {
            client2.emit('queue_for_game', player2_s);
        });
        client2.on('game_made', function (msg) {
            client2.emit('send_json_opponent', 'send json opponent');
            client2.on('get_json_opponent', function (msg) {
                msg.should.equal(player1_s);
                client2.disconnect();
                count++;
                completeTest();
            });
        });
    });

    it('should only let two people match if three are looking for the same course', function(done) {
        var matches = 0;
        var dones = 0;
        var client1, client2, client4;
        var completeTest = function() {
            dones++;
            if(dones == 3) {
                matches.should.equal(2);
                client1.disconnect();
                client2.disconnect();
                client4.disconnect();
                done();
            }
        }

        client1 = io.connect(socketURL, options);
        client1.on('connect', function(data) {
            client1.emit('queue_for_game', player1_s);
            client1.on('game_made', function (msg) {
                matches++;
                completeTest();
            });
            setTimeout(completeTest, 100);
        });

        client2 = io.connect(socketURL, options);
        client2.on('connect', function (data) {
            client2.emit('queue_for_game', player2_s);
            client2.on('game_made', function (msg) {
                matches++;
                completeTest();
            });
            setTimeout(completeTest, 100);
        });

        client4 = io.connect(socketURL, options);
        client4.on('connect', function (data) {
            client4.emit('queue_for_game', player4_s);
            client4.on('game_made', function (msg) {
                matches++;
                completeTest();
            });
            setTimeout(completeTest, 100);
        });
    });
});