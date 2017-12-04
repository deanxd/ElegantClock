package com.deanxd.elegantclock.util;

import android.graphics.PointF;

/**
 * @author Dean
 */

public class CommonUtils {


    /**
     * 根据角度和长度计算  线段的起点和终点的坐标
     *
     * @param angle 偏移角度
     */
    public static float[] calculatePoint(float angle, float leftLength, float rigthLength) {
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * rigthLength;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * rigthLength;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * leftLength;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * leftLength;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * rigthLength;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * rigthLength;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * leftLength;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * leftLength;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * rigthLength;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * rigthLength;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * leftLength;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * leftLength;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * rigthLength;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * rigthLength;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * leftLength;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * leftLength;
        }
        return points;
    }

}
