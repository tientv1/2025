package com.assignment.service;

import com.assignment.entity.OrderDetail;
import com.assignment.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Double getTotalRevenue() {
        return orderDetailRepository.findAll()
                .stream()
                .mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity())
                .sum();
    }

    public List<OrderDetail> getTopSellingProducts() {
        return orderDetailRepository.findAll();
    }
}
