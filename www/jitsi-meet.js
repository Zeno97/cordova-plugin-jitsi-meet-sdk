module.exports.startJitsiMeet = function (options, success, error) {
  cordova.exec(success, error, "JitsiMeet", "startJitsiMeet", [options]);
};