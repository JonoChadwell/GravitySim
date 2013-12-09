package gravitysim;

import java.awt.Color;

/**
 *
 * @author Jono
 */
public class GOholder {

    public GOholder(GravObject go) {
        location = go.location;
        radius = go.radius;
        drawColor = go.color;
        if (go instanceof Sun) {
            isSun = true;
        }
    }
    public boolean isSun = false;
    public Vector location;
    public double radius;
    public Color drawColor;
    public double screenRadius;
    public Vector screenLocation;
}
