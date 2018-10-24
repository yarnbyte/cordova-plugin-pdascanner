var exec = require('cordova/exec');
var cordova = require('cordova');


//先定义QRScanreceiver，在QRScanreceiver里面addWindowEventHandler，监听getqrdata
var PDAScanner = function(){
    this.data = null;
    cordova.addWindowEventHandler('getqrdata').onHasSubscribersChange = PDAScanner.onHasSubscribersChange;
}

//指定回调，当插件初始化时响应
PDAScanner.onHasSubscribersChange = function(){
    exec(pdascanner._listen,pdascanner._error,"pdascanner","start",[]);
}

//有二维码数据来时，激发getqrdata事件
PDAScanner.prototype._listen = function(info){
    if(info){
        cordova.fireWindowEvent('getqrdata',info);
        pdascanner.data = info.data;
    }
}
//插件初始化失败的回调
PDAScanner.prototype._error = function(e){
    console.log('插件初始化错误，详情: ' + e);
}

var pdascanner = new PDAScanner();

module.exports = pdascanner;