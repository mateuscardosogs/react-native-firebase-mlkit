
package com.reactlibrary;

import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RNMlKitModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNMlKitModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

  }

  @Override
  public String getName() {
    return "RNMlKit";
  }

  @ReactMethod
  public void detect(String pathStr, final Promise promise) {
      try {
          FirebaseApp.initializeApp(this.reactContext);
          boolean a = new File(pathStr).exists();
          Uri path = Uri.fromFile(new File(pathStr));
          FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this.reactContext, path);
          FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
          detector.detectInImage(image).addOnSuccessListener(
                  new OnSuccessListener<FirebaseVisionText>() {
                      @Override
                      public void onSuccess(FirebaseVisionText texts) {
                          try {
                              processTextRecognitionResult(texts, promise);
                          } catch (Exception e) {
                              promise.reject("Error data parsing", e);
                          }
                      }
                  })
                  .addOnFailureListener(
                          new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  promise.reject("Error image detecting", e);
                              }
                          });
      } catch (Exception e) {
          promise.reject("Error ", e);
      }
  }

    private void processTextRecognitionResult(FirebaseVisionText texts, Promise promise) {
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            promise.resolve("[]");
        }
        List<Map<String, Object>> blks = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            FirebaseVisionText.Block block = blocks.get(i);
            StringBuilder sb = new StringBuilder();
            for (Point p : block.getCornerPoints()) {
                sb.append(p);
            }

            Map<String, Object> blk = new HashMap<>();
            blk.put("text", block.getText());
            blk.put("lines", new LinkedList<>());
            blk.put("contactPoints", Arrays.asList(block.getCornerPoints()[0], block.getCornerPoints()[1]));
            blks.add(blk);

            List<FirebaseVisionText.Line> lines = block.getLines();
            for (int j = 0; j < lines.size(); j++) {
                FirebaseVisionText.Line line = lines.get(j);

                Map<String, Object> ln = new HashMap<>();
                ln.put("text", line.getText());
                ln.put("elements", new LinkedList<>());
                ln.put("contactPoints", Arrays.asList(line.getCornerPoints()[0], line.getCornerPoints()[1]));
                ((List) blk.get("lines")).add(ln);

                List<FirebaseVisionText.Element> elements = line.getElements();
                for (int k = 0; k < elements.size(); k++) {

                    Map<String, Object> el = new HashMap<>();
                    FirebaseVisionText.Element element = elements.get(k);
                    el.put("text", element.getText());
                    el.put("contactPoints", Arrays.asList(element.getCornerPoints()[0], element.getCornerPoints()[1]));
                    ((List) ln.get("elements")).add(el);
                }
            }
        }

        try {
            promise.resolve(new ObjectMapper().writeValueAsString(blks));
        } catch (JsonProcessingException e) {
            promise.reject("Error Reading Data", e);
        }
    }
}