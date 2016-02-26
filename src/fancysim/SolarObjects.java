package fancysim;

import utils.Vector;

/**
 *
 * @author Jono
 */
public abstract class SolarObjects {
    public static Sun Sol = new Sun(new Vector(),new Vector(),332948.6,.004649,"Sol");
    public static GravObject Mercury = new GravObject(new Vector(0,.39528,0),new Vector(.0274,0,0),1,1.6308 * Math.pow(10, -5),"Mercury");
    public static GravObject Earth = new GravObject(new Vector(0,1,0),new Vector(.0172,0,0),1,4.2564 * Math.pow(10, -5),"Earth");
    public static GravObject Luna = new GravObject(new Vector(0,1.002481,0),new Vector(.0172 + 0.000588,0,0),.0123,1.1614 * Math.pow(10, -5));
    public static GravObject Jupiter = new GravObject(new Vector(0,5.209,0),new Vector(.00754,0,0),317.94,4.6239 * Math.pow(10, -4),"Jupiter");
    public static GravObject IO = new GravObject(new Vector(0,5.209 + 0.00282,0),new Vector(.00754 + .01,0,0),0.014952,1.2177 * Math.pow(10, -5),"IO"); //glitches
    public static GravObject Callisto = new GravObject(new Vector(0,5.209 + 0.012585,0),new Vector(.00754 + .00474,0,0),0.018011,1.6112 * Math.pow(10, -5));
    public static GravObject Leda = new GravObject(new Vector(0,5.209 + 0.075632,0),new Vector(.00754 + .00193,0,0),1.8 * Math.pow(10,-9),6.7 * Math.pow(10, -8));
    
}