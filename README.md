
# react-native-firebase-mlkit
React Native wrapper for functionality of https://developers.google.com/ml-kit/

## Getting started

`$ npm install git+https://github.com/mateusc42/react-native-firebase-mlkit.git --save`


## Mostly automatic installation

`$ react-native link react-native-firebase-mlkit`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-firebase-mlkit` and add `RNMlKit.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMlKit.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNMlKitPackage;` to the imports at the top of the file
  - Add `new RNMlKitPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-firebase-mlkit'
  	project(':react-native-firebase-mlkit').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-firebase-mlkit/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

    ```
    ...
    dependencies {
        implementation 'com.google.firebase:firebase-core:16.0.1'
        implementation 'com.google.firebase:firebase-ml-vision:17.0.0'

        implementation (project(':react-native-firebase-mlkit')) {
            exclude group: 'com.google.firebase'
        }
    }

    // Place this line at the end of file

    apply plugin: 'com.google.gms.google-services'

    // Work around for onesignal-gradle-plugin compatibility
    com.google.gms.googleservices.GoogleServicesPlugin.config.disableVersionCheck = true
    ```

4. Insert the following lines inside the dependencies block in `android/build.gradle`:

    ```
    buildscript {
        repositories {
            google()
            ...
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:3.0.1'
            classpath 'com.google.gms:google-services:4.0.2' // google-services plugin
        }
    }
    ```


## Usage (Example using [react-native-camera](https://github.com/react-native-community/react-native-camera))

```javascript

import RNMlKit from 'react-native-firebase-mlkit';

export class textRecognition extends Component {
  ...

  async takePicture() {
    if (this.camera) {
      const options = { quality: 0.5, base64: true, skipProcessing: true, forceUpOrientation: true };
      const data = await this.camera.takePictureAsync(options);
      const textRecognition = await RNTextDetector.detectFromUri(data.uri);
      console.log('Text Recognition', textRecognition);
    }
  };

  ...
}
```
  
