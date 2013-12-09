package gravitysim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Jono
 */
public class display extends JPanel implements ActionListener {

    public display() {
        this.t = new Timer(10, this);
        t.start();
        this.addMouseMotionListener(ma);
        this.addMouseListener(ma);
        this.addMouseWheelListener(ma);
        this.addKeyListener(ka);
        this.setFocusable(true);
        this.setBackground(Color.black);
        //simController.addRandomRange(1000);
        simController.sim.addObject(SolarObjectHolder.Sol);
        simController.sim.addObject(SolarObjectHolder.Earth);
        simController.sim.addObject(SolarObjectHolder.Jupiter);
        simController.sim.addObject(SolarObjectHolder.Luna);
        simController.sim.addObject(SolarObjectHolder.Mercury);
    }
    public SimulationController simController = new SimulationController();
    private ArrayList<GOholder> suns;
    private GOholder[] drawList;
    private double scale = 30;
    private double vRot = Math.PI;
    private double hRot = 0;
    private boolean DistanceScaled = true;
    private boolean autoTurn = false;
    private double screenDistance = 40.0;
    private Timer t;
    private Vector cameraPosition = new Vector(0, 0, 0);
    private Vector cameraSpeed = new Vector(0, 0, 0);
    private int mousex, mousey;
    private boolean mouseDown = false;
    private MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent me) {
            if (mouseDown) {
                hRot += (me.getX() - mousex) / 300.0;
                vRot += (me.getY() - mousey) / 300.0;
                mousex = me.getX();
                mousey = me.getY();
                repaint();
            } else {
                mousex = me.getX();
                mousey = me.getY();
                mouseDown = true;
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            mouseDown = false;
        }

        @Override
        public void mousePressed(MouseEvent me) {

            requestFocus(true);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            
        }
    };
    public KeyAdapter ka = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37: //right
                    cameraSpeed.x = 1;
                    break;
                case 38: //down
                    cameraSpeed.y = 1;
                    break;
                case 39: //left
                    cameraSpeed.x = -1;
                    break;
                case 40: //up
                    cameraSpeed.y = -1;
                    break;
                case 16: //in (RShift)
                    cameraSpeed.z = 1;
                    break;
                case 17: //out (LShift)
                    cameraSpeed.z = -1;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37:
                    cameraSpeed.x = 0;
                    break;
                case 38:
                    cameraSpeed.y = 0;
                    break;
                case 39:
                    cameraSpeed.x = 0;
                    break;
                case 40:
                    cameraSpeed.y = 0;
                    break;
                case 16:
                    cameraSpeed.z = 0;
                    break;
                case 17:
                    cameraSpeed.z = 0;
                    break;
            }
        }
    };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        precalcDrawing();
        for (GOholder gh : drawList) {
            drawGOholder(gh, g);
        }
        double d = 5.0;
        /*g.setColor(Color.red);
        drawLine(new Vector(d, 0, 0), new Vector(0, 0, 0), g);
        g.setColor(Color.blue);
        drawLine(new Vector(0, d, 0), new Vector(0, 0, 0), g);
        g.setColor(Color.green);
        drawLine(new Vector(0, 0, d), new Vector(0, 0, 0), g);
        */
    }

    private void drawLine(Vector i, Vector j, Graphics g) {
        try {
            i = Vector.add(cameraPosition,getScreenLocation(i));
            j = Vector.add(cameraPosition,getScreenLocation(j));
            g.drawLine(
                    (int) (i.x * scale + getWidth() / 2),
                    (int) (i.y * scale + getHeight() / 2),
                    (int) (j.x * scale + getWidth() / 2),
                    (int) (j.y * scale + getHeight() / 2));
        } catch (GraphicsException ge) {
            
        }
    }

    private void drawGOholder(GOholder gh, Graphics g) {
        try {
            calcScreenInformation(gh);
            double r = scale * gh.screenRadius / 2 * 50;
            double xPos = gh.screenLocation.x * scale + getWidth() / 2;
            double yPos = gh.screenLocation.y * scale + getHeight() / 2;
            g.setColor(Color.black);
            g.fillOval(
                    (int) (xPos - r),
                    (int) (yPos - r),
                    (int) (r * 2),
                    (int) (r * 2));
            g.setColor(gh.drawColor);
            g.drawOval(
                    (int) (xPos - r),
                    (int) (yPos - r),
                    (int) (r * 2),
                    (int) (r * 2));
            int points = 20;
            for (GOholder s : suns) {
                if (!gh.isSun) {
                    double cosScreenAngle = Math.sin(Math.atan2(gh.screenLocation.x - s.screenLocation.x, gh.screenLocation.y - s.screenLocation.y));
                    double sinScreenAngle = Math.cos(Math.atan2(gh.screenLocation.x - s.screenLocation.x, gh.screenLocation.y - s.screenLocation.y));
                    double cosRealAngle = Vector.dot(Vector.difference(gh.location, s.location), distScalars) / Vector.abs(Vector.difference(gh.location, s.location)) / Vector.abs(distScalars);

                    double[] xPoints1 = new double[points * 2];
                    double[] yPoints1 = new double[points * 2];
                    for (int i = 0; i < points; i++) {
                        int j = i;
                        double ang = Math.PI / points * j;
                        xPoints1[i] = -Math.sin(ang) * r;
                        yPoints1[i] = Math.cos(ang) * r;
                    }
                    for (int i = 2 * points - 1; i > points - 1; i--) {
                        int j = i - points;
                        double ang = Math.PI / points * j;
                        xPoints1[i] = -Math.sin(ang) * r * cosRealAngle;
                        yPoints1[i] = -Math.cos(ang) * r;
                    }
                    int[] xPoints2 = new int[points * 2];
                    int[] yPoints2 = new int[points * 2];
                    for (int i = 0; i < points * 2; i++) {
                        xPoints2[i] = (int) (xPoints1[i] * cosScreenAngle + yPoints1[i] * -sinScreenAngle + xPos);
                        yPoints2[i] = (int) (xPoints1[i] * sinScreenAngle + yPoints1[i] * cosScreenAngle + yPos);
                    }
                    g.setColor(new Color(s.drawColor.getRed() * 2 / 3,s.drawColor.getBlue() * 2 / 3,s.drawColor.getGreen() * 2 / 3));
                    g.fillPolygon(xPoints2, yPoints2, points * 2);

                } else {
                    g.setColor(s.drawColor);
                    g.fillOval(
                            (int) (xPos - r),
                            (int) (yPos - r),
                            (int) (r * 2),
                            (int) (r * 2));
                }
            }
        } catch (GraphicsException ge) {
        }
    }
    private Vector xScalar, yScalar, zScalar, distScalars;

    private void precalcDrawing() {
        xScalar = new Vector(Math.sin(hRot), -Math.cos(hRot) * Math.sin(vRot), 0);
        yScalar = new Vector(Math.cos(hRot), Math.sin(hRot) * Math.sin(vRot), 0);
        zScalar = new Vector(0, Math.cos(vRot), 0);
        distScalars = new Vector(-Math.cos(hRot) * Math.cos(vRot), Math.sin(hRot) * Math.cos(vRot), -Math.sin(vRot));
        suns = new ArrayList<>();
        GravObject[] objectList = simController.sim.getObjects();
        drawList = new GOholder[objectList.length];
        for (int i = 0; i < objectList.length; i++) {
            GOholder h = new GOholder(objectList[i]);
            h.location = Vector.add(cameraPosition,h.location);
            try {
                h.screenLocation = getScreenLocation(h.location);
            } catch (GraphicsException ge) {
                h.screenLocation = new Vector(10000, 10000, 0);
            }
            drawList[i] = h;
            if (h.isSun) {
                suns.add(h);
            }
        }
        quickSort(drawList, 0, drawList.length - 1);
    }

    private Vector getScreenLocation(Vector v) {
        if (DistanceScaled) {
            double dist = Vector.dot(distScalars, v);
            double endDistScalar = (screenDistance) / (.01 - dist);
            if (endDistScalar < 0) {
                throw new GraphicsException();
            }
            Vector rtn = Vector.scale(Vector.add(Vector.scale(xScalar, v.x), Vector.scale(yScalar, v.y), Vector.scale(zScalar, v.z)), endDistScalar);
            rtn.z = dist;
            return rtn;
        } else {
            double zoom = 1;
            Vector rtn = Vector.add(Vector.scale(xScalar, v.x * zoom), Vector.scale(yScalar, v.y * zoom), Vector.scale(zScalar, v.z * zoom));
            rtn.z = Vector.dot(distScalars, v);
            return rtn;
        }
    }

    private void calcScreenInformation(GOholder gh) {
        double dist = Vector.dot(distScalars, gh.location);
        if (DistanceScaled) {
            double endDistScalar = (screenDistance) / (.01 - dist);
            if (endDistScalar < 0) {
                throw new GraphicsException();
            }
            gh.screenLocation =
                    Vector.scale(
                    Vector.add(
                    Vector.scale(xScalar, gh.location.x),
                    Vector.scale(yScalar, gh.location.y),
                    Vector.scale(zScalar, gh.location.z)), endDistScalar);
            gh.screenRadius = gh.radius * endDistScalar;
        } else {
            gh.screenLocation = Vector.add(
                    Vector.scale(xScalar, gh.location.x),
                    Vector.scale(yScalar, gh.location.y),
                    Vector.scale(zScalar, gh.location.z));
            gh.screenRadius = gh.radius;
        }
        gh.screenLocation.z = dist;
    }

    @Override
    public void actionPerformed(ActionEvent ae) { //main logic
        if (autoTurn) {
            hRot += .003;
        }
        cameraPosition = Vector.add(Vector.scale(cameraSpeed, .01), cameraPosition);
        repaint();
    }

    public void setOrtho(Boolean val) {
        DistanceScaled = val;
        repaint();
    }

    public void setVRot(double val) {
        vRot = val;
    }

    public void setHRot(double val) {
        hRot = val;
    }

    void setAutoTurn(boolean b) {
        autoTurn = !b;
    }

    void quickSort(GOholder arr[], int left, int right) {
        int i = left, j = right;
        GOholder tmp;
        double pivot = arr[(left + right) / 2].screenLocation.z;

        /* partition */
        while (i <= j) {
            while (arr[i].screenLocation.z < pivot) {
                i++;
            }
            while (arr[j].screenLocation.z > pivot) {
                j--;
            }
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        /* recursion */
        if (left < j) {
            quickSort(arr, left, j);
        }
        if (i < right) {
            quickSort(arr, i, right);
        }
    }

    public void setPaused(boolean selected) {
        if (selected) {
            t.stop();
        } else {
            t.start();
        }
    }

    public void setSimPaused(boolean selected) {
        simController.paused = selected;
    }
}
