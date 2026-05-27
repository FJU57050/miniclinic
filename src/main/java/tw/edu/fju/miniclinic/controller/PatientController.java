package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PatientController {
    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String listPatients(Model model) {
        List<Patient> patients = patientRepo.findAll();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/api/patients")
    @ResponseBody
    public List<Patient> getPatientsApi() { return patientRepo.findAll(); }
}