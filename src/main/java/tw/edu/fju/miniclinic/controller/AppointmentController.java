package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map;

@Controller
public class AppointmentController {
    private static final Set<String> ALLOWED_STATUSES = Set.of("BOOKED", "COMPLETED", "CANCELLED");

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/appointment/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

    @PostMapping("/appointment/new")
    public String submitAppointment(
            @Valid @ModelAttribute("form") AppointmentForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        Patient patient = patientRepo.findById(form.getChartNo()).orElse(null);
        Doctor  doctor  = doctorRepo.findById(form.getDoctorId()).orElse(null);

        if (patient == null || doctor == null) {
            model.addAttribute("error", "查無此病歷號或醫師，請確認後重試");
            model.addAttribute("form", form);
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(LocalDate.parse(form.getApptDate()));
        appt.setTimeSlot(form.getTimeSlot());
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);

        model.addAttribute("appointment", saved);
        return "appointment-result";
    }

    @GetMapping("/api/appointments")
    @ResponseBody
    public List<Appointment> getAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String doctorId) {

        String normalizedDoctorId = normalizeUppercase(doctorId);

        if (date != null && normalizedDoctorId != null) {
            Doctor doctor = doctorRepo.findById(normalizedDoctorId).orElse(null);
            if (doctor == null) return List.of();
            return appointmentRepo.findByDoctorAndApptDate(doctor, date);
        }

        if (date != null) return appointmentRepo.findByApptDate(date);

        if (normalizedDoctorId != null) {
            Doctor doctor = doctorRepo.findById(normalizedDoctorId).orElse(null);
            if (doctor == null) return List.of();
            return appointmentRepo.findByDoctor(doctor);
        }

        return appointmentRepo.findAll();
    }

    @GetMapping("/api/appointments/count")
    @ResponseBody
    public Map<String, Long> getAppointmentCount() {
        return Map.of("count", appointmentRepo.count());
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Map<String, String> request) {

        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = normalizeUppercase(request.get("timeSlot"));

        Patient patient = patientRepo.findById(normalizeUppercase(chartNo)).orElse(null);
        Doctor doctor = doctorRepo.findById(normalizeUppercase(doctorId)).orElse(null);

        if (patient == null || doctor == null) return ResponseEntity.badRequest().build();

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> request,
            jakarta.servlet.http.HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        String newStatus = normalizeUppercase(request.get("status"));

        if (newStatus == null || !ALLOWED_STATUSES.contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) return ResponseEntity.notFound().build();

        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) return ResponseEntity.status(403).build();

        appt.setStatus(newStatus);
        Appointment updated = appointmentRepo.save(appt);
        return ResponseEntity.ok(updated);
    }

    private String normalizeUppercase(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        if (normalized.isEmpty()) return null;
        return normalized.toUpperCase(Locale.ROOT);
    }
}
