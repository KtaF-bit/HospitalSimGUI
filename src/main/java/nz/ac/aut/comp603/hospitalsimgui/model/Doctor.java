/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.model;

import java.util.Set;

/**
 *
 * @author GGPC
 */

public class Doctor {

    private final Set<Integer> treatableLevels;
    private boolean isBusy;

    // ✅ Constructor
    public Doctor(Set<Integer> treatableLevels) {
        this.treatableLevels = treatableLevels;
        this.isBusy = false;
    }

    // ✅ Check if doctor can treat sickness level
    public boolean canTreat(int sicknessLevel) {
        return treatableLevels.contains(sicknessLevel);
    }

    // ✅ Status methods
    public boolean isBusy() {
        return isBusy;
    }

    public void startTreatment() {
        isBusy = true;
    }

    public void stopTreatment() {
        isBusy = false;
    }

    public Set<Integer> getTreatableLevels() {
        return treatableLevels;
    }

    @Override
    public String toString() {
        return "Doctor {Treatable: " + treatableLevels +
               ", Status: " + (isBusy ? "Busy" : "Free") + "}";
    }
}
