var exec = require('cordova/exec');
var cordova = require('cordova');



var PDAScanner = function(){
    this.data = null;
    cordova.addWindowEventHandler('getqrdata').onHasSubscribersChange = PDAScanner.onHasSubscribersChange;
}

PDAScanner.onHasSubscribersChange = function(){
    exec(pdascanner._listen,pdascanner._error,"PDAScanner","start",[]);
}

PDAScanner.prototype._listen = function(info){
    if(info){
        cordova.fireWindowEvent('getqrdata',info);
        pdascanner.data = info.data;
    }
}

PDAScanner.prototype._error = function(e){
    console.log('插件初始化错误，详情: ' + e);
}

var pdascanner = new PDAScanner();

module.exports = pdascanner;