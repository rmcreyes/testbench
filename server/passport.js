const passport = require('passport');
const JwtStrategy = require('passport-jwt').Strategy;
const { ExtractJwt } = require('passport-jwt');
const LocalStrategy = require('passport-local').Strategy;
const FacebookTokenStrategy = require('passport-facebook-token');
const config = require('./configuration');
const User = require('./models/user');

// JSON WEB TOKENS STRATEGY
passport.use(new JwtStrategy({
  jwtFromRequest: ExtractJwt.fromHeader('authorization'),
  secretOrKey: config.JWT_SECRET
}, async (payload, done) => {
  try {
    // Find the user specified in token
    const user = await User.findById(payload.sub);

    // If user doesn't exists, handle it
    if (!user) {
      return done(null, false);
    }

    // Otherwise, return the user
    done(null, user);
  } catch(error) {
    done(error, false);
  }
}));



passport.use('facebookToken', new FacebookTokenStrategy({
  clientID: config.oauth.facebook.clientID,
  clientSecret: config.oauth.facebook.clientSecret
}, async (accessToken, refreshToken, profile, done) => {
  try {
    console.log('profile', profile);
    console.log('accessToken', accessToken);
    console.log('refreshToken fb', refreshToken);

    const existingUser = await User.findOne({ "facebook_id": profile.id });
    if (existingUser) {
      return done(null, existingUser);
    }

    //create new user if user does not exist
    const newUser = new User({
      name : profile.displayName,
      alias: null,
      email : profile.emails[0].value,
      profile_photo_id : 0,
      is_professor: false,
      reported: false,
      facebook_id: profile.id
    });

    await newUser.save();
    done(null, newUser);
  } catch(error) {
    done(error, false, error.message);
  }
}));

// LOCAL STRATEGY
// passport.use(new LocalStrategy({
//   usernameField: 'email'
// }, async (email, password, done) => {
//   try {
//     // Find the user given the email
//     const user = await User.findOne({ "local.email": email });

//     // If not, handle it
//     if (!user) {
//       return done(null, false);
//     }

//     // Check if the password is correct
//     const isMatch = await user.isValidPassword(password);

//     // If not, handle it
//     if (!isMatch) {
//       return done(null, false);
//     }

//     // Otherwise, return the user
//     done(null, user);
//   } catch(error) {
//     done(error, false);
//   }
// }));
