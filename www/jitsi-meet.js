module.exports.startConference = function (options, success, error) {
  cordova.exec(success, error, "JitsiMeet", "startConference", [options]);
};
module.exports.disposeConference = function (success, error) {
  cordova.exec(success, error, "JitsiMeet", "disposeConference");
};