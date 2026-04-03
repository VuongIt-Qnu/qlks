package com.example.hotel.controller;

import com.example.hotel.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping("/revenue")
    public BigDecimal revenue(
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return switch (period.toUpperCase()) {
            case "DAY" -> reportService.revenueDay(day != null ? day : LocalDate.now());
            case "MONTH" -> reportService.revenueMonth(
                    year != null ? year : LocalDate.now().getYear(),
                    month != null ? month : LocalDate.now().getMonthValue());
            case "YEAR" -> reportService.revenueYear(year != null ? year : LocalDate.now().getYear());
            default -> throw new IllegalArgumentException("period must be DAY, MONTH, or YEAR");
        };
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping("/revenue/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) throws Exception {
        byte[] data = reportService.exportPdfRevenue(from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping("/revenue/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) throws Exception {
        byte[] data = reportService.exportExcelRevenue(from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
