# cordova-plugin-pdascanner
Android PDA Device Scanner cordova plugin
PDA手持终端获取扫码数据的cordova插件

## 安装(Installation)

```
cordova plugin add cordova-plugin-pdascanner
```

如果是ionic项目则在cordova前加上ionic

```
ionic cordova plugin add cordova-plugin-pdascanner
```

## 用法(Usage)

### cordova/phonegap项目(in cordova/phonegap)

```
window.addEventListener("getqrdata", getQRData, false);

function getQRData(data){
  console.log(data.data);
}
```

### ionic3+
```
npm i ionic-pdascanner
```

添加至module.ts(Add to your app's module)
```
import { PDAScanner } from 'ionic-pdascanner';

...
providers: [
    ...
    PDAScanner,
    ...
  ]
...
```

```
import { PDAScanner } from 'ionic-pdascanner';
...

constructor(public platform: Platform,public pdascanner:PDAScanner,public toastCtrl: ToastController) {

      this.platform.ready().then(()=>{
        this.pdascanner.onReceiver().subscribe((data:QRDataResponse)=>{
          this.toastCtrl.create({
            message:data.data,
            duration:2000,
            position:"middle"
          }).present();
        })
     });
    }

  }
```
