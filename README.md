
## Why do you need Tattle-UI

As a developer, we struggle to understand and reproduce few UI 
bugs reported by the tester. In tester's point of view, tester 
writes paragraphs to explain a simple UI misalignment when they test.

Tattle-UI solves this problem by providing a simple mechanism to get UI feedback 
from testers.

## What do you see on your app

After integration, Tattle-UI library adds a floating button on every screen. 
Tester can click on this button whenever he sees some issue with the UI. 
Tattle-UI library takes the snapshot of the current screen and allow the tester 
to mark problematic section using scribbles. Tester may wish to add a audio note along with this. 
Tattle-UI provides tester to send them in Email. We use [MultipleImagePick] (https://github.com/luminousman/MultipleImagePick) to demo this control.

   

#Integration steps

## [Java](https://github.com/npctech/Tattle-UI-Android/tree/master/)

###From github 

* Download the code from github and follow the below steps to integrate Tattle-UI into your project.
* Add TattleUI library to your project.
* Include the below lines in Android Manifest file.
```
<application> 
   android:name="com.npcompete.tattle.utils.BaseApp" 
</application>
```

Note:

If we need spot it window on top of dialog/popup we have to call the below code befor calling dialog/popup.show() function.
```
TattleManager tattleManager = TattleManager.getInstance();	
tattleManager.assignVariables(arg0, windowObj);//windowObj means popup/dialog
tattleManager.inflatingTattleViewOnCurrentView();
```

## Requirement

* Minimum android SDK Version : 14
* Maximum android SDK Version : 18
* All Android devices.

## Limitation

- Only supported for **portrait** orientation.
- Audio recording supports only **2 minutes**.

# Optional Configuration

## Java

* Get Instance of Tattle_Configuration (Singleton) initially.
``` 
Tattle_Configuration tattleConfig = Tattle_Configuration.getConfigurationInstance();
```
* **Change scribble color (default black)**
```
tattleConfig.setScribbleColor(int yourColor);
```
* **Change scribble stroke width (default 12)**
```
tattleConfig.setScribbleStrokeWidth(int yourPaintStrokeWidth)
```
* **Set recipients email**
```
tattleConfig.setMailRecipient(String mailRecipient);
```
* **Set mail subject**
```
tattleConfig.setMailSubject(String mailSubject)
```
## License  
  
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
