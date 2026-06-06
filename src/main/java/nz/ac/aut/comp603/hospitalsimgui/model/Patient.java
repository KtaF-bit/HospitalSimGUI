package nz.ac.aut.comp603.hospitalsimgui.model;

/**
 *
 * @author Kobe Fabrello (22157634)
 */
public class Patient {

    // Sickness levels
    public static final int SICKNESS_1 = 1;
    public static final int SICKNESS_2 = 2;
    public static final int SICKNESS_3 = 3;

    // Treatment times
    public static final int TREATMENT_TIME_S1 = 2;
    public static final int TREATMENT_TIME_S2 = 3;
    public static final int TREATMENT_TIME_S3 = 5;

    private int sicknessLevel;
    private int treatmentTime;
    private boolean beingTreated;
    private int timeInHospital;

    // Constructor
    public Patient(int sicknessLevel) {
        this.sicknessLevel = sicknessLevel;
        this.beingTreated = false;
        this.timeInHospital = 0;

        // Set treatment time based on sickness
        switch (sicknessLevel) {
            case SICKNESS_1:
                this.treatmentTime = TREATMENT_TIME_S1;
                break;
            case SICKNESS_2:
                this.treatmentTime = TREATMENT_TIME_S2;
                break;
            case SICKNESS_3:
                this.treatmentTime = TREATMENT_TIME_S3;
                break;
        }
    }

    // Getters
    public int getSicknessLevel() {
        return sicknessLevel;
    }

    public int getTreatmentTime() {
        return treatmentTime;
    }

    public boolean isBeingTreated() {
        return beingTreated;
    }

    public int getTimeInHospital() {
        return timeInHospital;
    }

    // Behaviour
    public void startTreatment() {
        beingTreated = true;
    }

    public void stopTreatment() {
        beingTreated = false;
    }

    public void reduceTreatmentTime() {
        if (beingTreated && treatmentTime > 0) {
            treatmentTime--;
        }
    }

    public boolean isTreated() {
        return treatmentTime <= 0;
    }

    public void incrementTimeInHospital() {
        timeInHospital++;
    }

    @Override
    public String toString() {
        return "Patient [Level=" + sicknessLevel +
               ", Treatment Left=" + treatmentTime +
               ", Time In Hospital=" + timeInHospital + "]";
    }
}
