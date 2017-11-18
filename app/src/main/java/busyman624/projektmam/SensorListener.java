package busyman624.projektmam;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorListener implements SensorEventListener {

    public Context context;
    public CameraView cameraView;
    public CameraOverlay cameraOverlay;
    private int counter=0;
    float[] accelerometerData = new float[3];
    float[] magnetometerData = new float[3];
    float[] rotationMatrix = new float[9];

    public SensorListener(Context context) {
        this.context = context;
        this.cameraView = ((MainActivity)context).cameraView;
        this.cameraOverlay = ((MainActivity)context).cameraOverlay;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = event.values.clone();
                break;
            case Sensor.TYPE_ALL:
                cameraView.invalidate();
                break;
            default:
                return;
        }

        if (accelerometerData != null && magnetometerData != null) {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData)) {
                if (counter % 5 == 0) cameraOverlay.updateObjects(rotationMatrix);
            }
        }
    }
}
