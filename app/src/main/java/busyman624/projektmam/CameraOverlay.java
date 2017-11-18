package busyman624.projektmam;

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

    final int ballTextSize=40;
    final int ballRadius=30;
    final int strokeWidth=10;
    final int descriptionTextSize=30;
    final int interspace=2*ballRadius+2*strokeWidth+ballTextSize;

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
        objectsData.add(new ObjectData("CALYPSO Morena", 54.352884, 18.593711, 66.7, new int[]{8, 12}));
        objectsData.add(new ObjectData("CALYPSO Madison", 54.356404, 18.648558, 7.11, new int[]{10, 14}));
        objectsData.add(new ObjectData("CALYPSO Zaspa", 54.389912, 18.610180, 13.28, new int[]{12, 20}));
        objectsData.add(new ObjectData("CALYPSO Przymorze", 54.409577, 18.590862, 12.34, new int[]{8, 20}));
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
        int longestObjectName=0;
        Paint bg = new Paint();
        Paint ballBg = new Paint();
        Paint ballText = new Paint();
        Paint description = new Paint();
        Paint stroke = new Paint();

        bg.setARGB(128, 214, 214, 214);

        ballText.setARGB(255, 0, 0, 0);
        ballText.setTextSize(ballTextSize);

        description.setARGB(255, 0, 0, 0);
        description.setTextSize(descriptionTextSize);

        stroke.setStyle(Style.STROKE);
        stroke.setARGB(255, 0, 0, 0);
        stroke.setStrokeWidth(strokeWidth);

        for(int i=0; i < objectsData.size(); i++) {
            if(objectsData.get(i).isVisible) {
                visibleObjects.add(i);
                if(objectsData.get(i).name.length()>longestObjectName) longestObjectName=objectsData.get(i).name.length();
            }
        }

        if(visibleObjects.size()>0){
            canvas.drawRect(30, 30, longestObjectName*ballTextSize+30, interspace + interspace * visibleObjects.size(), bg);
            canvas.drawRect(30, 30, longestObjectName*ballTextSize+30, interspace + interspace * visibleObjects.size(), stroke);
            for(int i=0; i<visibleObjects.size(); i++) {
                if(objectsData.get(visibleObjects.get(i)).isOpen()) ballBg.setARGB(128, 0, 255, 0);
                else ballBg.setARGB(128, 255, 0, 0);

                canvas.drawCircle(objectsData.get(visibleObjects.get(i)).pixelCoords[0], objectsData.get(visibleObjects.get(i)).pixelCoords[1], ballRadius, ballBg);
                canvas.drawCircle(objectsData.get(visibleObjects.get(i)).pixelCoords[0], objectsData.get(visibleObjects.get(i)).pixelCoords[1], ballRadius, stroke);
                canvas.drawText(Integer.toString(visibleObjects.get(i) + 1), objectsData.get(visibleObjects.get(i)).pixelCoords[0] - strokeWidth, objectsData.get(visibleObjects.get(i)).pixelCoords[1] + strokeWidth, ballText);

                canvas.drawText(Integer.toString(visibleObjects.get(i) + 1) + ". " + objectsData.get(visibleObjects.get(i)).name , ballRadius+strokeWidth, 2*ballRadius+strokeWidth + interspace * i, ballText);
                canvas.drawCircle(2*ballRadius+strokeWidth, interspace + interspace * i, ballRadius, ballBg);
                canvas.drawCircle(2*ballRadius+strokeWidth, interspace + interspace * i, ballRadius, stroke);
                canvas.drawText("Dystans: "+ userLocation.location.distanceTo(objectsData.get(visibleObjects.get(i)).location)+"m", interspace+10, interspace + interspace * i, description);


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
    }

    private double getAngle(float[] a, float b[]) {
        double temp = (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]) /
                Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]) /
                Math.sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
        return Math.acos(temp);
    }
}
