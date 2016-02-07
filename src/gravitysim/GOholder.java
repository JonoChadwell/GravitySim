package gravitysim;

import java.awt.Color;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class GOholder {

    public GOholder(GravObject _go) {
        go = _go;
        if (go.name != null)
        {
            name = go.name;
        }
        location = go.location;
        radius = go.radius;
        drawColor = go.color;
        if (go instanceof Sun) {
            isSun = true;
        }
    }
    public GravObject go;
    public String name;
    public boolean isSun = false;
    public Vector location;
    public double radius;
    public Color drawColor;
    public double screenRadius;
    public Vector screenLocation;
}
