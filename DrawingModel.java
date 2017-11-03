/**
 * Created by suyueyun on 2017-10-25.
 */

import java.util.*;

public class DrawingModel {
    private IView view;
    private ArrayList<Shape> ShapeList;

    public DrawingModel(){
        ShapeList = new ArrayList();
    }
    public void addView(IView view){
        this.view = view;
    }

    public void notifyObserver(){
        this.view.update();
    }


    public void addShape(Shape shape){
        ShapeList.add(shape);
    }

    public void removeHighlight(){
        for(int i = 0; i < ShapeList.size(); i++){
            Shape s = ShapeList.get(i);
            if(s.getisHightlighted()){
                ShapeList.remove(i);
                notifyObserver();
                break;
            }
        }
    }

    public void setTranslate(int x, int y){
        for(int i = 0; i < ShapeList.size(); i++){
            Shape s = ShapeList.get(i);
            if(s.getisHightlighted()){
                s.setTrans(x,y);
                notifyObserver();
                break;
            }
        }
    }

    public void setOffset(){
        for(int i = 0; i < ShapeList.size(); i++){
            Shape s = ShapeList.get(i);
            if(s.getisHightlighted()){
                s.setOffset();
                notifyObserver();
                break;
            }
        }
    }

    public void setShapeScale(float f){
        for(int i = 0; i < ShapeList.size(); i++){
            Shape s = ShapeList.get(i);
            if(s.getisHightlighted()){
                s.setScale(f);
                notifyObserver();
                break;
            }
        }
    }

    public void setShapeRotate(int r){
        for(int i = 0; i < ShapeList.size(); i++){
            Shape s = ShapeList.get(i);
            if(s.getisHightlighted()){
                s.setRotate(r);
                notifyObserver();
                break;
            }
        }
    }

    public  ArrayList<Shape> getShapeList(){
        return ShapeList;
    }


}
