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
        displayCamera = new Camera(new Vector(-1, 0, 0));
        simController.addRandomRange(1500);
        /*
        simController.sim.addObject(SolarObjects.Sol);
        SolarObjects.Sol.color = Color.white;
        simController.sim.addObject(SolarObjects.Earth);
        simController.sim.addObject(SolarObjects.Jupiter);
        simController.sim.addObject(SolarObjects.Luna);
        simController.sim.addObject(SolarObjects.Mercury);
        simController.sim.addObject(SolarObjects.Callisto);
        simController.sim.addObject(SolarObjects.Leda);*/
    }
    public Camera displayCamera;
    public SimulationController simController = new SimulationController();
    public double scrollSpeed = 1;
    private GravObject following;
    private Vector cameraSpeed = new Vector(0, 0, 0);
    private boolean autoTurn = false;
    private Timer t;
    private int mousex, mousey;
    private boolean mouseDown = false;
    private MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent me) {
            if (mouseDown) {
                displayCamera.hRot += (me.getX() - mousex) / (displayCamera.zoom / 3.0);
                displayCamera.vRot += (me.getY() - mousey) / (displayCamera.zoom / 3.0);
                if (displayCamera.vRot > Math.PI / 2) {
                    displayCamera.vRot = Math.PI / 2;
                }
                if (displayCamera.vRot < -Math.PI / 2) {
                    displayCamera.vRot = -Math.PI / 2;
                }
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
            mousex = me.getX();
            mousey = me.getY();
            mouseDown = false;
        }

        @Override
        public void mousePressed(MouseEvent me) {

            requestFocus(true);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            if (e.getWheelRotation() < 0)
            {
                displayCamera.zoom *= 1.05;
            } else {
                displayCamera.zoom *= .95;
            }
            if (displayCamera.zoom < 1000)
                    displayCamera.zoom = 1000;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            if (me.getButton() == 2) {
                if (displayCamera.getFollowing() == null) {
                    GOholder gh = displayCamera.getNearestToMouse();
                    if (gh != null) {
                        displayCamera.setFollowing(gh.go);
                    }
                } else {
                    displayCamera.setFollowing(null);
                }
            }
        }
    };
    public KeyAdapter ka = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37: //right
                    cameraSpeed.x = -scrollSpeed;
                    break;
                case 38: //down
                    cameraSpeed.y = -scrollSpeed;
                    break;
                case 39: //left
                    cameraSpeed.x = scrollSpeed;
                    break;
                case 40: //up
                    cameraSpeed.y = scrollSpeed;
                    break;
                case 16: //in (RShift)
                    cameraSpeed.z = -scrollSpeed;
                    break;
                case 17: //out (LShift)
                    cameraSpeed.z = scrollSpeed;
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
        displayCamera.draw(simController.sim.getObjects(), g, getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void actionPerformed(ActionEvent ae) { //main logic
        displayCamera.setMouseLocation(new Vector(mousex, mousey, 0));
        if (autoTurn) {
            displayCamera.hRot += .003;
        }
        displayCamera.moveCamera(Vector.scale(cameraSpeed, .01));
        repaint();
    }

    void setAutoTurn(boolean b) {
        autoTurn = !b;
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
