package com.yl.pdascanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class PDAScanner extends CordovaPlugin {

    //只有一行注释，就是这么霸气

    //算了，怂了，不写自己也看不懂

    //我也不知道自己写了什么，但就是好使了

    private String LOG_TAG = getClass().getName();

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;

    private SoundPool soundpool = null;

    private int soundid;

    private String barcodeStr;

    private ScanManager mScanManager;

    private BroadcastReceiver receiver;

    private CallbackContext pdaCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("start")) {
            if(this.pdaCallbackContext!=null){
                removeListener();
            }

            this.initScan();
            this.pdaCallbackContext = callbackContext;

            IntentFilter filter = new IntentFilter();
            int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
            String[] value_buf = mScanManager.getParameterString(idbuf);
            if(value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
                filter.addAction(value_buf[0]);
            } else {
                filter.addAction(SCAN_ACTION);
            }

            if (this.receiver == null) {
                this.receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //嘀
                        soundpool.play(soundid, 1, 1, 0, 0, 1);
                        //把intent扔给scanDataHandle吧，让它解析
                        scanDataHandle(intent);
                    }
                };
                webView.getContext().registerReceiver(receiver, filter);
            }


            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }else if(action.equals("stop")){
            removeListener();
            this.sendUpdate(new JSONObject(), false); // release status callback in JS side
            this.pdaCallbackContext = null;
            callbackContext.success();
            return true;
        }
        return false;
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode( 0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private  void scanDataHandle(Intent intent){
        //getQRData负责解析intent中的二维码数据，返回json
        //json给sendUpdate，让它发送出去给js
        this.sendUpdate(this.getQRData(intent),true);
    }

    private JSONObject getQRData(Intent intent) {
        JSONObject obj = new JSONObject();

        byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
        int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);

        barcodeStr = new String(barcode, 0, barcodelen);

        try {
            obj.put("data", barcodeStr);
        } catch (JSONException e) {
            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return obj;
    }


    private void removeListener(){
        if(this.receiver!=null){
            this.receiver = null;
            webView.getContext().unregisterReceiver(receiver);
        }
    }

    private void sendUpdate(JSONObject info, boolean keepCallback) {
        if (this.pdaCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(keepCallback);
            this.pdaCallbackContext.sendPluginResult(result);
        }
    }


}
