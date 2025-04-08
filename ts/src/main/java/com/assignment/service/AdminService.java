package com.assignment.service;

import com.assignment.dto.RevenueReportDTO;
import com.assignment.dto.UserDTO;
import com.assignment.dto.VipCustomerDTO;

import java.util.List;

public interface AdminService {
    List<UserDTO> getAllUsersDTO();
    UserDTO toggleUserStatusDTO(Integer id);
    RevenueReportDTO getRevenueReportDTO();
    List<VipCustomerDTO> getVipCustomersDTO();
}