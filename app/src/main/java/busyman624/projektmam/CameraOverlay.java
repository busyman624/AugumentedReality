package busyman624.projektmam;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;


public class CameraOverlay extends View{

    private double angle=0;
    private ArrayList<int[]> pixelCoordsList=new ArrayList<>();
    //private int[] pixelCoords=new int[2];
    private int calcCounter=0;
    public Camera.Parameters cameraParameters;
    private Point screenSize;
    private CameraData cameraData;
    private ArrayList<ObjectData> objectsData;
    public UserLocation userLocation;

    public CameraOverlay(Context context, Camera.Parameters cameraParameters) {
        super(context);
        this.cameraParameters = cameraParameters;
        cameraData = new CameraData();
        objectsData=new ArrayList<>();
        objectsData.add(new ObjectData("CALYPSO Morena", 54.352884, 18.593711, 66.7));
        objectsData.add(new ObjectData("CALYPSO Madison", 54.356404, 18.648558, 7.11));
        objectsData.add(new ObjectData("CALYPSO Zaspa", 54.389912, 18.610180, 13.28));
        objectsData.add(new ObjectData("CALYPSO Przymorze", 54.409577, 18.590862, 12.34));
        userLocation = ((MainActivity)context).userLocation;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenSize=new Point();
        display.getSize(screenSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ArrayList<Integer> visibleObjects=new ArrayList<>();
        Paint bg = new Paint();
        bg.setARGB(128, 214, 214, 214);
        Paint ballText = new Paint();
        ballText.setARGB(255, 0, 0, 0);
        ballText.setTextSize(40);
        Paint description = new Paint();
        description.setARGB(255, 0, 0, 0);
        description.setTextSize(30);
        Paint stroke = new Paint();
        stroke.setStyle(Style.STROKE);
        stroke.setARGB(255, 0, 0, 0);
        stroke.setStrokeWidth(10);
        for(int i=0; i < objectsData.size(); i++) {
            if(objectsData.get(i).isVisible) {
                visibleObjects.add(i);
            }
        }

        if(visibleObjects.size()>0){
            canvas.drawRect(30, 30, 2*screenSize.x/5, screenSize.y-30, bg);
            canvas.drawRect(30, 30, 2*screenSize.x/5, screenSize.y-30, stroke);
            for(int i=0; i<visibleObjects.size(); i++) {
                canvas.drawText(Integer.toString(visibleObjects.get(i) + 1) + ". " + objectsData.get(visibleObjects.get(i)).name , 40, 70 + 120 * i, ballText);
                canvas.drawText("Dystans: "+ userLocation.location.distanceTo(objectsData.get(visibleObjects.get(i)).location)+"m", 40, 120 + 120 * i, description);

                canvas.drawCircle(objectsData.get(visibleObjects.get(i)).pixelCoords[0], objectsData.get(visibleObjects.get(i)).pixelCoords[1], 30, bg);
                canvas.drawCircle(objectsData.get(visibleObjects.get(i)).pixelCoords[0], objectsData.get(visibleObjects.get(i)).pixelCoords[1], 30, stroke);
                canvas.drawText(Integer.toString(visibleObjects.get(i) + 1), objectsData.get(visibleObjects.get(i)).pixelCoords[0] - 10, objectsData.get(visibleObjects.get(i)).pixelCoords[1] + 10, ballText);
            }
        }
    }

    public void updateObjects(float[] rotationMatrix){
        if(userLocation.location!=null) {
            cameraData.convertToMFrame(rotationMatrix);
            cameraData.locationToECEF(userLocation.location);
            for (ObjectData objectData : objectsData) {
                objectData.convertToMFrame(userLocation.location, cameraData.ECEF);
                objectData.convertToBFrame(rotationMatrix);
                objectData.calcPixelCoords(screenSize, cameraParameters);
            }
            invalidate();
        }
        /*if(pixelCoordsList.size()>9){
            int[] pixelsSum=new int[2];
            for(int[] tempPixel : pixelCoordsList){
                pixelsSum[0]=pixelsSum[0]+tempPixel[0];
                pixelsSum[1]=pixelsSum[0]+tempPixel[0];
            }
            pixelCoords[0]=pixelsSum[0]/pixelCoordsList.size();
            pixelCoords[1]=pixelsSum[1]/pixelCoordsList.size();
            invalidate();
            angle = getAngle(objectData.MFrame, cameraData.MFrame);
            angle = (angle * 180.0f) / 3.14f;
            pixelCoordsList.clear();
        } */
        //angle = getAngle(objectData.MFrame, cameraData.MFrame);
        //angle = (angle * 180.0f) / 3.14f;
    }


    private double getAngle(float[] a, float b[]) {
        double temp = (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]) /
                Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]) /
                Math.sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
        return Math.acos(temp);
    }

   /*  private void calcPixelCoords(float[] objectBFrame){
        if(objectBFrame[2]<0) {
            pixelCoords[0] = (int) (screenSize.x / 2 + (objectBFrame[1] / objectBFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getHorizontalViewAngle()) / 2)) * screenSize.x / 2);
            pixelCoords[1] = (int) (screenSize.y / 2 + (objectBFrame[0] / objectBFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getVerticalViewAngle()) / 2)) * screenSize.y / 2);
        }
       int[] tempPixelCoords=new int[2];
        if(objectBFrame[2]<0) {
            tempPixelCoords[0] = (int) (screenSize.x / 2 + (objectBFrame[1] / objectBFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getHorizontalViewAngle()) / 2)) * screenSize.x / 2);
            tempPixelCoords[1] = (int) (screenSize.y / 2 + (objectBFrame[0] / objectBFrame[2]) * (1 / Math.tan(Math.toRadians(cameraParameters.getVerticalViewAngle()) / 2)) * screenSize.y / 2);
            pixelCoordsList.add(tempPixelCoords);
        }
    }*/
}
