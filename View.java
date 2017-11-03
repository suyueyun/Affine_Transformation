import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by suyueyun on 2017-10-24.
 */
public class View extends JFrame implements IView {
    CanvasView DrawBoard;
    ToolBarView ToolBar;
    SatusView SatusBar;
    private DrawingModel model;
    Shape newShape;
    int initx;
    int inity;
    boolean selected = false;
    public View(DrawingModel model) {
        setTitle("A2Basic");
        setSize(800, 600);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        DrawBoard = new CanvasView();
        ToolBar = new ToolBarView();
        SatusBar = new SatusView();
        this.add(ToolBar);
        this.add(DrawBoard);
        this.add(SatusBar);
        setLayout(new FlowLayout());
        setVisible(true);
        this.model = model;
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int current_width = e.getComponent().getBounds().getSize().width;
                int current_height = e.getComponent().getBounds().getSize().height;
                setSize(current_width,current_height);
                ToolBar.setPreferredSize(new Dimension(current_width,current_height * 30 / 600));
                SatusBar.setPreferredSize(new Dimension(current_width,current_height * 40 / 600));
                DrawBoard.setPreferredSize(new Dimension(current_width, current_height * 500 / 600));
                model.notifyObserver();
            }
        });
    }

    public class CanvasView extends JPanel{
        CanvasView() {
            setPreferredSize(new Dimension(800, 500));
            setBorder(BorderFactory.createLineBorder(Color.lightGray));


            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // run hit-test
                    ArrayList<Shape> listShape = model.getShapeList();
                    boolean first = true;
                    boolean hit_hightlight = false;
                    for (Shape s : listShape) {
                        s.setIsHightlight(false);
                    }
                    for (int i = listShape.size() - 1; i >= 0; i--) { //Shape s  : listShape
                        Shape s = listShape.get(i);
                        if (s != null) {
                            boolean hittestresult = s.hittest(e.getX(), e.getY());
                            //System.out.println(hittestresult);//////debug
                            if (hittestresult && first) {
                                s.setIsHightlight(true);
                                first = false;
                                hit_hightlight = true;
                                selected = true;
                                float Scalevalue = s.scale;
                                ToolBar.scale_value.setText(String.format("%.1f", Scalevalue));
                                ToolBar.rotate_value.setText("" + s.rotate);
                                ToolBar.slider_scale.setValue((int) (Scalevalue * 100));
                                ToolBar.slider_rotate.setValue(s.rotate);
                            } else {
                                s.setIsHightlight(false);
                            }
                        }
                    }
                    if (hit_hightlight == false) {
                        for (Shape s : listShape) {
                            s.setIsHightlight(false);
                            selected = false;
                        }
                        ToolBar.scale_value.setText("1.0");
                        ToolBar.rotate_value.setText("0");
                        ToolBar.slider_scale.setValue(100);
                        ToolBar.slider_rotate.setValue(0);
                    }
                    if (selected == false) {
                        setToolbarEnable(false);
                    } else {
                        setToolbarEnable(true);
                    }
                    model.notifyObserver();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    newShape = new Shape();
                    initx = e.getX();
                    inity = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (newShape.points != null) {
                        model.addShape(newShape);
                        model.notifyObserver();
                    }
                    if (selected) {
                        model.setOffset();
                    }
                }

            });

            this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!selected) {
                        addPoint(e.getX(), e.getY());
                    } else {
                        model.setTranslate(e.getX() - initx, e.getY() - inity);
                    }
                }
            });

        }
        public void addPoint(double x,double y){
            newShape.addPoint(x,y);
            model.notifyObserver();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                    RenderingHints.VALUE_ANTIALIAS_ON);
            ArrayList<Shape> listShape = model.getShapeList();
            if(newShape != null) {
                newShape.draw(g2);
            }
            for(Shape s : listShape) {
                if (s != null)
                    s.draw(g2);
            }
        }
    }

    public void update(){
        SatusBar.setNumShapeName(model.getShapeList().size());
        String status = "";
        ArrayList<Shape> listShape = model.getShapeList();
        for(Shape s : listShape){
            if(s.isHightlighted) {
                int npoints = s.npoints;
                String scal = String.format("%.1f",s.scale);
                int rotate = s.rotate;
                status = ",Selection(" + npoints + " points, scale: " + scal + ",rotation " + rotate + ")";
                SatusBar.setInfo(status);
                break;
            }
        }
        SatusBar.setInfo(status);
        repaint();
    }

    public class SatusView extends JPanel {
        JLabel NumShape;
        JLabel name;
        JLabel Info;
        SatusView(){
            NumShape = new JLabel("0");
            name = new JLabel("Strokes");
            Info = new JLabel("");
            setLayout(new FlowLayout(FlowLayout.LEADING));
            setPreferredSize(new Dimension(800, 40));
            this.add(NumShape);
            this.add(name);
            this.add(Info);
        }
        public void setNumShapeName(int n){
            if(n == 1){
                name.setText("Stroke");
            }else{
                name.setText("Strokes");
            }
            NumShape.setText(n + "");
        }
        public void setInfo(String str){
            Info.setText(str);
        }
    }

    private void setToolbarEnable(boolean b){
        ToolBar.slider_rotate.setEnabled(b);
        ToolBar.slider_scale.setEnabled(b);
        ToolBar.button.setEnabled(b);
        ToolBar.scale_value.setEnabled(b);
        ToolBar.rotate_value.setEnabled(b);
        ToolBar.rotate_label.setEnabled(b);
        ToolBar.scale_label.setEnabled(b);
        ToolBar.setEnabled(b);
    }

    public class ToolBarView extends JPanel {
        JLabel scale_value;
        JLabel rotate_value;
        JSlider slider_scale;
        JSlider slider_rotate;
        JButton button;
        JLabel scale_label;
        JLabel rotate_label;
        ToolBarView(){
            setLayout(new FlowLayout(FlowLayout.LEADING));
            //setLayout(new BorderLayout());
            setPreferredSize(new Dimension(800, 30));
            // create a button and add a listener for events
            button = new JButton("Delete");
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //label.setText("Delete");
                    model.removeHighlight();
                    selected = false;
                    setToolbarEnable(false);
                }
            });
            scale_label = new JLabel("Scale");
            // create a slider
            slider_scale = new JSlider(50, 200, 100);
            slider_scale.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JSlider s = (JSlider) e.getSource();
                    float Scalevalue = (s.getValue() + 0.0f) / 100;
                    model.setShapeScale(Scalevalue);
                    scale_value.setText(String.format("%.1f", Scalevalue));
                }
            });
            rotate_label = new JLabel("Rotate");
            // create a slider
            slider_rotate = new JSlider(-180, 180, 0);
            slider_rotate.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JSlider s = (JSlider) e.getSource();
                    int rotateVal = s.getValue();
                    model.setShapeRotate(rotateVal);
                    rotate_value.setText("" + rotateVal);
                }
            });
            scale_value = new JLabel("1.0");
            rotate_value = new JLabel("0");
            // add widgets
            this.add(button);
            this.add(Box.createHorizontalStrut(20));
            this.add(scale_label);
            this.add(slider_scale);
            this.add(scale_value);
            this.add(Box.createHorizontalStrut(20));
            this.add(rotate_label);
            this.add(slider_rotate);
            this.add(rotate_value);
            this.slider_rotate.setEnabled(false);
            this.slider_scale.setEnabled(false);
            this.button.setEnabled(false);
            this.scale_value.setEnabled(false);
            this.rotate_value.setEnabled(false);
            this.rotate_label.setEnabled(false);
            this.scale_label.setEnabled(false);
            this.setEnabled(false);
        }
    }
}
