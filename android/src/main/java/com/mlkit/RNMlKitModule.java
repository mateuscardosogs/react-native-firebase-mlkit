
package com.mlkit;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class RNMlKitModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private FirebaseVisionTextRecognizer textDetector;
  private FirebaseVisionTextRecognizer cloudTextDetector;

  public RNMlKitModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @ReactMethod
  public void deviceBarcodeRecognition(String uri, final Promise promise) {
    try {
      FirebaseVisionBarcodeDetectorOptions options =
        new FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(FirebaseVisionBarcode. FORMAT_ALL_FORMATS)
        .build();
      FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this.reactContext, android.net.Uri.parse(uri));
      FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(options);
      
      Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                WritableArray data = Arguments.createArray();
                WritableMap info = Arguments.createMap();

                for (FirebaseVisionBarcode barcode: barcodes) {
                    info = Arguments.createMap();
                    info.putString("format", barcodeFormat(barcode.getFormat()));
                    info.putString("value", barcode.getRawValue());
                    data.pushMap(info);
                }
                promise.resolve(data);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                promise.reject(e);
            }
      });
    } catch (IOException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void deviceTextRecognition(String uri, final Promise promise) {
      try {
          FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this.reactContext, android.net.Uri.parse(uri));
          FirebaseVisionTextRecognizer detector = this.getTextRecognizerInstance();
          Task<FirebaseVisionText> result =
                  detector.processImage(image)
                          .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                              @Override
                              public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                  promise.resolve(processDeviceResult(firebaseVisionText));
                              }
                          })
                          .addOnFailureListener(
                                  new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          e.printStackTrace();
                                          promise.reject(e);
                                      }
                                  });;
      } catch (IOException e) {
          promise.reject(e);
          e.printStackTrace();
      }
  }

  private FirebaseVisionTextRecognizer getTextRecognizerInstance() {
    if (this.textDetector == null) {
      this.textDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    }

    return this.textDetector;
  }

  @ReactMethod
  public void close(final Promise promise) {
    if(this.textDetector != null) {
      try {
        this.textDetector.close();
        this.textDetector = null;
        promise.resolve(true);
      } catch (IOException e) {
        e.printStackTrace();
        promise.reject(e);
      }
    }

    if(this.cloudTextDetector != null) {
      try {
        this.cloudTextDetector.close();
        this.cloudTextDetector = null;
        promise.resolve(true);
      } catch (IOException e) {
        e.printStackTrace();
        promise.reject(e);
      }
    }
  }

  private FirebaseVisionTextRecognizer getCloudTextRecognizerInstance() {
    if (this.cloudTextDetector == null) {
      this.cloudTextDetector = FirebaseVision.getInstance().getCloudTextRecognizer();
    }

    return this.cloudTextDetector;
  }

  @ReactMethod
  public void cloudTextRecognition(String uri, final Promise promise) {
      try {
          FirebaseVisionTextRecognizer detector = this.getCloudTextRecognizerInstance();
          FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this.reactContext, android.net.Uri.parse(uri));
          Task<FirebaseVisionText> result =
                  detector.processImage(image)
                          .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                              @Override
                              public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                  promise.resolve(processCloudResult(firebaseVisionText));
                              }
                          })
                          .addOnFailureListener(
                                  new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          e.printStackTrace();
                                          promise.reject(e);
                                      }
                                  });
      } catch (IOException e) {
          promise.reject(e);
          e.printStackTrace();
      }
  }

  private String barcodeFormat(int format) {
      switch (format) {
          case FirebaseVisionBarcode.FORMAT_CODE_128:
              return "CODE_128";
      
          case FirebaseVisionBarcode.FORMAT_CODE_39:
              return "CODE_39";
      
          case FirebaseVisionBarcode.FORMAT_CODE_93:
              return "CODE_93";
            
          case FirebaseVisionBarcode.FORMAT_CODABAR:
              return "CODABAR";

          case FirebaseVisionBarcode.FORMAT_DATA_MATRIX:
              return "DATA_MATRIX";
      
          case FirebaseVisionBarcode.FORMAT_EAN_13:
              return "EAN_13";
      
          case FirebaseVisionBarcode.FORMAT_EAN_8:
              return "EAN_8";
      
          case FirebaseVisionBarcode.FORMAT_ITF:
              return "ITF";
      
          case FirebaseVisionBarcode.FORMAT_QR_CODE:
              return "QR_CODE";
      
          case FirebaseVisionBarcode.FORMAT_UPC_A:
              return "UPC_A";
      
          case FirebaseVisionBarcode.FORMAT_UPC_E:
              return "UPC_E";

          case FirebaseVisionBarcode.FORMAT_PDF417:
              return "PDF417";
      
          case FirebaseVisionBarcode.FORMAT_AZTEC:
              return "AZTEC";
      
          default:
            return "UNKNOWN";
      }
  }

 
  /**
   * Converts firebaseVisionText into a map
   *
   * @param firebaseVisionText
   * @return
   */
  private WritableArray processDeviceResult(FirebaseVisionText firebaseVisionText) {
      WritableArray data = Arguments.createArray();
      WritableMap info = Arguments.createMap();
      WritableMap coordinates = Arguments.createMap();
      List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();

      if (blocks.size() == 0) {
          return data;
      }

      for (int i = 0; i < blocks.size(); i++) {
          List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
          info = Arguments.createMap();
          coordinates = Arguments.createMap();

          Rect boundingBox = blocks.get(i).getBoundingBox();

          coordinates.putInt("top", boundingBox.top);
          coordinates.putInt("left", boundingBox.left);
          coordinates.putInt("width", boundingBox.width());
          coordinates.putInt("height", boundingBox.height());

          info.putMap("blockCoordinates", coordinates);
          info.putString("blockText", blocks.get(i).getText());
          info.putString("resultText", firebaseVisionText.getText());

          for (int j = 0; j < lines.size(); j++) {
              List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
              info.putString("lineText", lines.get(j).getText());

              for (int k = 0; k < elements.size(); k++) {
                  info.putString("elementText", elements.get(k).getText());
              }
          }

          data.pushMap(info);
      }

      return data;
  }

  private WritableArray processCloudResult(FirebaseVisionText firebaseVisionText) {
      WritableArray data = Arguments.createArray();
      WritableMap info = Arguments.createMap();
      WritableMap coordinates = Arguments.createMap();
      List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();

      if (blocks.size() == 0) {
          return data;
      }

      for (int i = 0; i < blocks.size(); i++) {
          List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
          info = Arguments.createMap();
          coordinates = Arguments.createMap();

          Rect boundingBox = blocks.get(i).getBoundingBox();

          coordinates.putInt("top", boundingBox.top);
          coordinates.putInt("left", boundingBox.left);
          coordinates.putInt("width", boundingBox.width());
          coordinates.putInt("height", boundingBox.height());

          info.putMap("blockCoordinates", coordinates);
          info.putString("blockText", blocks.get(i).getText());

          for (int j = 0; j < lines.size(); j++) {
              List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
              info.putString("lineText", lines.get(j).getText());

              for (int k = 0; k < elements.size(); k++) {
                  info.putString("elementText", elements.get(k).getText());
              }
          }

          data.pushMap(info);
      }

      return data;
  }


  @Override
  public String getName() {
    return "RNMlKit";
  }
}
