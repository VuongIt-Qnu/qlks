package com.example.hotel.service;

import com.example.hotel.entity.PaymentStatus;
import com.example.hotel.repository.PaymentRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;

    public ReportService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public BigDecimal revenueBetween(LocalDateTime from, LocalDateTime to) {
        return paymentRepository.sumAmountByStatusAndCreatedBetween(PaymentStatus.SUCCESS, from, to);
    }

    public BigDecimal revenueDay(LocalDate day) {
        LocalDateTime from = day.atStartOfDay();
        LocalDateTime to = day.plusDays(1).atStartOfDay();
        return revenueBetween(from, to);
    }

    public BigDecimal revenueMonth(int year, int month) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDateTime from = first.atStartOfDay();
        LocalDateTime to = first.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay();
        return revenueBetween(from, to);
    }

    public BigDecimal revenueYear(int year) {
        LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(year + 1, 1, 1).atStartOfDay();
        return revenueBetween(from, to);
    }

    public byte[] exportPdfRevenue(LocalDateTime from, LocalDateTime to) throws Exception {
        BigDecimal total = revenueBetween(from, to);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, baos);
        doc.open();
        doc.add(new Paragraph("Revenue report"));
        doc.add(new Paragraph("From: " + from + " To: " + to));
        doc.add(new Paragraph("Total: " + total));
        doc.close();
        return baos.toByteArray();
    }

    public byte[] exportExcelRevenue(LocalDateTime from, LocalDateTime to) throws Exception {
        BigDecimal total = revenueBetween(from, to);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Revenue");
            Row h = sh.createRow(0);
            h.createCell(0).setCellValue("From");
            h.createCell(1).setCellValue(from.toString());
            Row h2 = sh.createRow(1);
            h2.createCell(0).setCellValue("To");
            h2.createCell(1).setCellValue(to.toString());
            Row h3 = sh.createRow(2);
            h3.createCell(0).setCellValue("Total");
            h3.createCell(1).setCellValue(total.doubleValue());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        }
    }
}
