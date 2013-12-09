package gravitysim;

/**
 *
 * @author Jono
 */
public class SolarObjectHolder {
    public static Sun Sol = new Sun(new Vector(),new Vector(),332948.6,.004649);
    public static GravObject Earth = new GravObject(new Vector(0,1,0),new Vector(.0172,0,0),1,4.2564 * Math.pow(10, -5));
    public static GravObject Jupiter = new GravObject(new Vector(0,5.209,0),new Vector(.00754,0,0),317.94,4.6239 * Math.pow(10, -4));
    public static GravObject Luna = new GravObject(new Vector(0,1.002481,0),new Vector(.0172 + 0.000588,0,0),.0123,1.1614 * Math.pow(10, -5));
    public static GravObject Mercury = new GravObject(new Vector(0,.39528,0),new Vector(.0274,0,0),1,1.6308 * Math.pow(10, -5));
}
