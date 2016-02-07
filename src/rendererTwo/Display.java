package rendererTwo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import utils.Triangle;
import utils.Vector;

/**
 *
 * @author Jono
 */
public class Display extends JPanel implements ActionListener {

   public Display() {
      this.t = new Timer(10, this);
      t.start();
      this.addMouseMotionListener(ma);
      this.addMouseListener(ma);
      this.addMouseWheelListener(ma);
      this.addKeyListener(ka);
      this.setFocusable(true);
      this.setBackground(Color.black);
      camera = new Camera();
   }

   public Camera camera;
   public List<Vector> points;
   public List<Triangle> faces;
   private double scrollSpeed = 0.008;
   private Vector cameraSpeed = new Vector(0, 0, 0);
   private Timer t;
   private int mousex, mousey;
   private boolean mouseDown = false;
   private MouseAdapter ma = new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent me) {
         if (mouseDown) {
            camera.look((mousex - me.getX()) / 1000.0, (mousey - me.getY()) / 1000.0, 0);
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
      public void mouseClicked(MouseEvent me) {

      }
   };
   public KeyAdapter ka = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
         switch (e.getKeyCode()) {
         case 37: // right
            cameraSpeed.x = -scrollSpeed;
            break;
         case 38: // down
            cameraSpeed.y = -scrollSpeed;
            break;
         case 39: // left
            cameraSpeed.x = scrollSpeed;
            break;
         case 40: // up
            cameraSpeed.y = scrollSpeed;
            break;
         case 16: // in (RShift)
            cameraSpeed.z = scrollSpeed;
            break;
         case 17: // out (LShift)
            cameraSpeed.z = -scrollSpeed;
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
      camera.render(points, faces, g, this.getWidth(), this.getHeight());
   }

   double lightRotation = 0;

   @Override
   public void actionPerformed(ActionEvent ae) {
      camera.move(cameraSpeed);
      repaint();
   }

   public void setPaused(boolean selected) {
      if (selected) {
         t.stop();
      } else {
         t.start();
      }
   }
}
