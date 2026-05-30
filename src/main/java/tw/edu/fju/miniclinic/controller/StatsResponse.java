package tw.edu.fju.miniclinic.controller;

import java.util.Map;

public class StatsResponse {
    private int totalDoctors;
    private int totalPatients;
    private int totalAppointments;
    private Map<String, Long> byStatus;

    public StatsResponse(int totalDoctors, int totalPatients, int totalAppointments, Map<String, Long> byStatus) {
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.byStatus = byStatus;
    }

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public Map<String, Long> getByStatus() {
        return byStatus;
    }
}
