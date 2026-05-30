package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatsController {
    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String stats(Model model) {
        List<String> departments = doctorRepo.findAllDepartments();
        Map<String, Long> appointmentsByDepartment = new LinkedHashMap<>();

        for (String department : departments) {
            appointmentsByDepartment.put(department, 0L);
        }

        for (Appointment appointment : appointmentRepo.findAll()) {
            String department = appointment.getDoctor().getDepartment();
            appointmentsByDepartment.merge(department, 1L, Long::sum);
        }

        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());
        model.addAttribute("appointmentsByDepartment", appointmentsByDepartment);

        return "stats";
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public StatsResponse apiStats() {
        int totalDoctors = Math.toIntExact(doctorRepo.count());
        int totalPatients = Math.toIntExact(patientRepo.count());
        int totalAppointments = Math.toIntExact(appointmentRepo.count());
        Map<String, Long> byStatus = Map.of(
                "BOOKED", appointmentRepo.countByStatus("BOOKED"),
                "COMPLETED", appointmentRepo.countByStatus("COMPLETED"),
                "CANCELLED", appointmentRepo.countByStatus("CANCELLED")
        );

        return new StatsResponse(totalDoctors, totalPatients, totalAppointments, byStatus);
    }
}
