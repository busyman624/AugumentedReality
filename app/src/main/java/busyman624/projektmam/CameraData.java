package busyman624.projektmam;

import android.location.Location;

public class CameraData {
    final float EARTH_RADIUS = 6378137;

    float[] MFrame = new float[3];
    float[] BFrame = new float[3];
    float[] ECEF = new float[3];

    public CameraData(){
        BFrame[0] = 0;
        BFrame[1] = 0;
        BFrame[2] = -1;
    }

    public void convertToMFrame(float[] rotationMatrix){
            MFrame[0] = BFrame[0] * rotationMatrix[0] +
                    BFrame[1] * rotationMatrix[1] +
                    BFrame[2] * rotationMatrix[2];
            MFrame[1] = BFrame[0] * rotationMatrix[3] +
                    BFrame[1] * rotationMatrix[4] +
                    BFrame[2] * rotationMatrix[5];
            MFrame[2] = BFrame[0] * rotationMatrix[6] +
                    BFrame[1] * rotationMatrix[7] +
                    BFrame[2] * rotationMatrix[8];
    }

    public void locationToECEF(Location location) {
        ECEF[0] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(location.getLongitude())));
        ECEF[1] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.cos(Math.toRadians(location.getLatitude())) * Math.sin(Math.toRadians(location.getLongitude())));
        ECEF[2] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.sin(Math.toRadians(location.getLatitude())));
    }
}
