package com.mlkit;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;

import java.util.List;

public class Utils {
    public static WritableArray getContourPointsWritableArray(FirebaseVisionFace face, int contourType){
        WritableArray data = Arguments.createArray();
        List<FirebaseVisionPoint> points = face.getContour(contourType).getPoints();
        for (FirebaseVisionPoint point : points){
            WritableMap info = Arguments.createMap();
            info.putDouble("x", point.getX());
            info.putDouble("y", point.getY());

            data.pushMap(info);
        }
        return data;
    }
}
