package com.assignment.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.ReportService;
import com.assignment.entity.OrderDetail;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportRestController {
    @Autowired
    ReportService reportService;

    @GetMapping("/total-revenue")
    public ResponseEntity<?> getTotalRevenue() {
        Double totalRevenue = reportService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/top-selling")
    public ResponseEntity<?> getTopSellingProducts() {
        List<OrderDetail> topSelling = reportService.getTopSellingProducts();
        return ResponseEntity.ok(topSelling);
    }
}
