package tw.edu.fju.miniclinic.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {
    @Override
    public String convertToDatabaseColumn(LocalDate date) { return date == null ? null : date.toString(); }

    @Override
    public LocalDate convertToEntityAttribute(String s) { return s == null ? null : LocalDate.parse(s); }
}