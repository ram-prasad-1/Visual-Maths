/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.visualfiber.apps.visualmaths.utils.pathanimation;


import android.animation.TypeEvaluator;
import android.graphics.Path;


// This class is same as pathEvaluator with Path object included additionaly
public class PathEvaluator2 implements TypeEvaluator<PathPoint> {


    public boolean reverseTravel = false;

    private Path path = new Path();

    private boolean firstCall = true; // flag to record first call to method "evaluate"

    @Override
    public PathPoint evaluate(float t, PathPoint startValue, PathPoint endValue) {
        float x, y;

        if (reverseTravel) {
            t = 1 - t;
        }

        if (firstCall) {
            path.moveTo(startValue.mX, startValue.mY);
            firstCall = false;
        }


        if (endValue.mOperation == PathPoint.CURVE) {
            float oneMinusT = 1 - t;
            x = oneMinusT * oneMinusT * oneMinusT * startValue.mX +
                    3 * oneMinusT * oneMinusT * t * endValue.mControl0X +
                    3 * oneMinusT * t * t * endValue.mControl1X +
                    t * t * t * endValue.mX;
            y = oneMinusT * oneMinusT * oneMinusT * startValue.mY +
                    3 * oneMinusT * oneMinusT * t * endValue.mControl0Y +
                    3 * oneMinusT * t * t * endValue.mControl1Y +
                    t * t * t * endValue.mY;

            // @ rp hack for path drawing
            path.lineTo(x, y);

        } else if (endValue.mOperation == PathPoint.LINE) {
            x = startValue.mX + t * (endValue.mX - startValue.mX);
            y = startValue.mY + t * (endValue.mY - startValue.mY);

            //rp
            path.lineTo(x, y);
        } else {
            x = endValue.mX;
            y = endValue.mY;
            path.moveTo(x, y);

        }


        return PathPoint.moveTo(x, y);
    }


    // use this method if you want to draw the path itself while the animation is running
    public Path getPath() {
        return path;

    }


}
