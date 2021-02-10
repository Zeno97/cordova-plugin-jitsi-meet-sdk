module.exports = function(context) {

    var srcDir = context.opts.plugin.dir+'/sdks/android/';
    var destDir = context.opts.projectRoot+"/platforms/android/app/src/main/java/org/jitsi/meet";
    var hook = "[JitsiMeet] ";

    const fse = require('fs-extra');

    try{
        fse.copySync(srcDir, destDir);
        console.log(hook+"JitsiMeetSDK installed successfully!");
    }
    catch(error){
        console.error(hook+"Something went wrong..");
        console.error(error);
    }
}