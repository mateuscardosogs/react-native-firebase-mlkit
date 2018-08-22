
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
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class RNMlKitModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private final FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
  private FirebaseVisionImage image;

  public RNMlKitModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @ReactMethod
    public void detectFromUri(String uri, final Promise promise) {
        try {
            image = FirebaseVisionImage.fromFilePath(this.reactContext, android.net.Uri.parse(uri));
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    promise.resolve(getDataAsArray(firebaseVisionText));
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

    /**
     * Converts firebaseVisionText into a map
     *
     * @param firebaseVisionText
     * @return
     */
    private WritableArray getDataAsArray(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0) {
            return "[]";
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
            return (new ObjectMapper().writeValueAsString(blks));
        } catch (JsonProcessingException e) {
            return ("Error Reading Data", e);
        }
    }


  @Override
  public String getName() {
    return "RNMlKit";
  }
}