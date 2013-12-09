package gravitysim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author Jono
 */
public class SimulationController implements ActionListener {

    public Simulation sim = new Simulation();
    private static Random rn = new Random();
    private Timer t;
    public boolean paused = false;
    public double tickAmount = 1.0;

    public SimulationController() {
        this.t = new Timer(100, this);
        t.start();
    }

    public void addRandomRange(int count) {
        double startVelocity = .08;
        double avgMass = .1;
        double range = 10;
        for (int i = 0; i < count; i++) {
            sim.addObject(new GravObject(new Vector(getRandom(2 * range) - range, getRandom(2 * range) - range, getRandom(2 * range) - range), new Vector(getRandom(startVelocity) - startVelocity / 2, getRandom(startVelocity) - startVelocity / 2, getRandom(startVelocity) - startVelocity / 2), getRandom(avgMass * 2)));
        }
        sim.centerMass();
    }

    private double getRandom(double max) {
        return rn.nextDouble() * max;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (!paused) {
            sim.tickSimulation(tickAmount);
        }
    }
}
