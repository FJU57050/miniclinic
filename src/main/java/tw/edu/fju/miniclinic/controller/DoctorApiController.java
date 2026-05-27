package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class DoctorApiController {
    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/api/doctors")
    public List<Doctor> getDoctors(@RequestParam(required = false) String department) {
        if (department == null || department.isBlank()) return doctorRepo.findAll();
        return doctorRepo.findByDepartment(department);
    }

    @GetMapping("/api/doctors/{doctorId}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable String doctorId) {
        Optional<Doctor> doctor = doctorRepo.findById(doctorId);
        return doctor.map(d -> ResponseEntity.ok(d)).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/doctors/{doctorId}")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable String doctorId,
            @RequestBody Doctor updated) {

        return doctorRepo.findById(doctorId).map(existing -> {
                existing.setName(updated.getName());
                existing.setDepartment(updated.getDepartment());
                existing.setSpecialty(updated.getSpecialty());
                return ResponseEntity.ok(doctorRepo.save(existing));
            }).orElse(ResponseEntity.notFound().build());
    }

    // @GetMapping("/api/departments")
    // public List<String> getDepartments() {
    //     return doctorRepo.findAllDepartments();
    // }
}