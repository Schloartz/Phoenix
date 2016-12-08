package utils;

import javafx.scene.shape.Polygon;
import java.util.ArrayList;

//thanks to StanislavL from which I adapted this solution http://java-sl.com/shapes.html

class Star extends Polygon {
    Star(double r, double innerR) { //innerR ~ 0.4*r
        this(0, 0, r, innerR, 5, 3*Math.PI/10);
    }

    private Star(int x, int y, double r, double innerR, int vertexCount, double startAngle) {
        int[] xi = getXCoordinates(x, y, r, innerR,  vertexCount, startAngle);
        int[] yi = getYCoordinates(x, y, r, innerR,  vertexCount, startAngle);
        ArrayList<Double> points = new ArrayList<>();
        for(int i=0;i<10;i++){
            points.add((double)xi[i]);
            points.add((double)yi[i]);
        }
        getPoints().addAll(points);
    }

    private static int[] getXCoordinates(int x, int y, double r, double innerR, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount*2];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        double innerAngle=startAngle+Math.PI/vertexCount;
        for (int i=0; i<vertexCount; i++) {
            res[i*2]=(int)Math.round(r*Math.cos(angle))+x;
            angle+=addAngle;
            res[i*2+1]=(int)Math.round(innerR*Math.cos(innerAngle))+x;
            innerAngle+=addAngle;
        }
        return res;
    }

    private static int[] getYCoordinates(int x, int y, double r, double innerR, int vertexCount, double startAngle) {
        int res[]=new int[vertexCount*2];
        double addAngle=2*Math.PI/vertexCount;
        double angle=startAngle;
        double innerAngle=startAngle+Math.PI/vertexCount;
        for (int i=0; i<vertexCount; i++) {
            res[i*2]=(int)Math.round(r*Math.sin(angle))+y;
            angle+=addAngle;
            res[i*2+1]=(int)Math.round(innerR*Math.sin(innerAngle))+y;
            innerAngle+=addAngle;
        }
        return res;
    }
}
