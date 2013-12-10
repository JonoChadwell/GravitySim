package gravitysim;

/**
 *
 * @author Jono
 */
class Sun extends GravObject{
    public Sun(Vector _location, Vector _velocity,double _mass) {
        super(_location,_velocity,_mass);
    }
    public Sun(Vector _location, Vector _velocity,double _mass,double _radius,String _name) {
        super(_location,_velocity,_mass,_radius,_name);
    }
}
