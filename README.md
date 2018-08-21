
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
      compile project(':react-native-firebase-mlkit')
  	```


## Usage
```javascript
import RNMlKit from 'react-native-firebase-mlkit';

const pathOfImage = `the_path_of_your_image_to_extract`

RNMlKit.detect(pathOfImage).then(result => {
	// do something with the data
	console.log(JSON.parse(result))
})
```
  
