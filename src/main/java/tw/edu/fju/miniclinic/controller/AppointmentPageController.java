package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.AppointmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppointmentPageController {
    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepo.findAll());
        return "appointments";
    }
}