package tw.edu.fju.miniclinic.controller;

import tw.edu.fju.miniclinic.model.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInDoctorId") != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("loginForm")) model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm form,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) return "login";

        String doctorId = form.getDoctorId();
        if (doctorId == null) {
            model.addAttribute("errorMessage", "醫師編號或密碼錯誤");
            return "login";
        }

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null || doctor.getPasswordHash() == null || !BCrypt.checkpw(form.getPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "醫師編號或密碼錯誤");
            return "login";
        }

        session.setAttribute("loggedInDoctorId", doctor.getDoctorId());
        session.setAttribute("loggedInDoctorName", doctor.getName());

        return "redirect:/dashboard";
    }

    @GetMapping("/password")
    public String passwordForm(HttpSession session, Model model) {
        if (!model.containsAttribute("passwordChangeForm")) {
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        }

        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        if (doctorId == null || doctorRepo.findById(doctorId).isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        return "password";
    }

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeForm") PasswordChangeForm form,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        if (loggedInDoctorId == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Doctor doctor = doctorRepo.findById(loggedInDoctorId).orElse(null);

        if (doctor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        if (!result.hasFieldErrors("oldPassword")
                && (doctor.getPasswordHash() == null || !BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash()))) {
            result.rejectValue("oldPassword", "password.mismatch", "舊密碼不正確");
        }

        if (!result.hasFieldErrors("newPassword")
                && !result.hasFieldErrors("confirmPassword")
                && !form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.confirm", "新密碼與確認密碼不一致");
        }

        if (result.hasErrors()) return "password";

        doctor.setPasswordHash(BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt()));
        doctorRepo.save(doctor);

        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "密碼已更新，請重新登入。");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
