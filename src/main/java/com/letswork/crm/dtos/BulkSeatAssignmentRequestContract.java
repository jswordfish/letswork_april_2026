package com.letswork.crm.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BulkSeatAssignmentRequestContract {
	
	private Long contractId;
    private String letsWorkCentre;
    private String city;
    private String state;
    private String companyId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    private List<SeatAssignmentDto> seats;

    

    public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getLetsWorkCentre() {
        return letsWorkCentre;
    }

    public void setLetsWorkCentre(String letsWorkCentre) {
        this.letsWorkCentre = letsWorkCentre;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<SeatAssignmentDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatAssignmentDto> seats) {
        this.seats = seats;
    }

}
