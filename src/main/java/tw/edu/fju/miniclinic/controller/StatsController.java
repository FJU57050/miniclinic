package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
