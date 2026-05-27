package tw.edu.fju.miniclinic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeForm {
    @NotBlank(message = "請輸入舊密碼")
    private String oldPassword;

    @NotBlank(message = "請輸入新密碼")
    @Size(min = 8, message = "新密碼至少 8 碼")
    private String newPassword;

    @NotBlank(message = "請再次輸入新密碼")
    private String confirmPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
