package busyman624.projektmam;

import android.graphics.Point;
import android.hardware.Camera;
import android.location.Location;

public class ObjectData{
    final float EARTH_RADIUS = 6378137;

    float[] MFrame = new float[3];
    float[] BFrame = new float[3];
    float[] ECEF = new float[3];
    float[] pixelCoords = new float[3];
    boolean isVisible=false;
    public String name;
    public Location location;

    public ObjectData(String name, double latitude, double longitude, double altitude){
        this.name = name;
        location=new Location("Object Location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);

        locationToECEF();
    }

    public void convertToMFrame(Location cameraLocation, float[] cameraECEF){
        float[] offsetECEF = new float[3];
        offsetECEF[0]=ECEF[0]-cameraECEF[0];
        offsetECEF[1]=ECEF[1]-cameraECEF[1];
        offsetECEF[2]=ECEF[2]-cameraECEF[2];
        MFrame[0]= (float)(-Math.sin(Math.toRadians(cameraLocation.getLongitude()))*offsetECEF[0] +
                Math.cos(Math.toRadians(cameraLocation.getLongitude()))*offsetECEF[1]);
        MFrame[1]= (float)(-Math.cos(Math.toRadians(cameraLocation.getLongitude()))*Math.sin(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[0] -
                Math.sin(Math.toRadians(cameraLocation.getLongitude()))*Math.sin(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[1] +
                Math.cos(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[2]);
        MFrame[2]= (float)(Math.cos(Math.toRadians(cameraLocation.getLongitude()))*Math.cos(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[0] +
                Math.sin(Math.toRadians(cameraLocation.getLongitude()))*Math.cos(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[1] +
                Math.sin(Math.toRadians(cameraLocation.getLatitude()))*offsetECEF[2]);
    }

    public void convertToBFrame(float[] rotationMatrix) {
        BFrame[0] = MFrame[0] * rotationMatrix[0] +
                MFrame[1] * rotationMatrix[3] +
                MFrame[2] * rotationMatrix[6];
        BFrame[1] = MFrame[0] * rotationMatrix[1] +
                MFrame[1] * rotationMatrix[4] +
                MFrame[2] * rotationMatrix[7];
        BFrame[2] = MFrame[0] * rotationMatrix[2] +
                MFrame[1] * rotationMatrix[5] +
                MFrame[2] * rotationMatrix[8];
    }

    public void locationToECEF() {
        ECEF[0] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(location.getLongitude())));
        ECEF[1] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.cos(Math.toRadians(location.getLatitude())) * Math.sin(Math.toRadians(location.getLongitude())));
        ECEF[2] = (float) ((location.getAltitude() + EARTH_RADIUS) * Math.sin(Math.toRadians(location.getLatitude())));
    }

    public void calcPixelCoords(Point screenSize, Camera.Parameters cameraParameters) {
        if (BFrame[2] < 0) {
            pixelCoords[0] = (int) (screenSize.x / 2 + (BFrame[1] / BFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getHorizontalViewAngle()) / 2)) * screenSize.x / 2);
            pixelCoords[1] = (int) (screenSize.y / 2 + (BFrame[0] / BFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getVerticalViewAngle()) / 2)) * screenSize.y / 2);
        }

        if(pixelCoords[0]>0 && pixelCoords[0]<screenSize.x && pixelCoords[1]>0 && pixelCoords[1]<screenSize.y) isVisible=true;
        else isVisible=false;
    }
}
