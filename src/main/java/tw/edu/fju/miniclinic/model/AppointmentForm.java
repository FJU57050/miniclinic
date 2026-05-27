package tw.edu.fju.miniclinic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Locale;

public class AppointmentForm {
    @NotBlank(message = "請輸入病歷號")
    @Pattern(regexp = "TEST\\d{5}", message = "病歷號格式為 TESTxxxxx")
    private String chartNo;

    @NotBlank(message = "請選擇醫師")
    private String doctorId;

    @NotBlank(message = "請選擇日期")
    private String apptDate;

    @NotBlank(message = "請選擇時段")
    private String timeSlot;

    public String getChartNo() { return chartNo; }
    public void setChartNo(String chartNo) { this.chartNo = normalizeUppercase(chartNo); }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = normalizeUppercase(doctorId); }
    public String getApptDate() { return apptDate; }
    public void setApptDate(String apptDate) { this.apptDate = normalize(apptDate); }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = normalizeUppercase(timeSlot); }

    private String normalize(String value) { return value == null ? null : value.trim(); }

    private String normalizeUppercase(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
