package com.szs.szsproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id")
    private String userId;
    private String password;
    private String name;
    private String regNo;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /*//급여
    private Long salary;

    //산출세액
    private Long calculated_tax_amount;

    //보험료
    private Long premium_expenses;

    //교육비
    private Long educational_expenses;

    //기부금
    private Long donation_expenses;

    //의료비
    private Long medical_expenses;

    //퇴직연금
    private Long retirement_pension;



    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    public void updateSalary(Long salary) {
        this.salary = salary;
    }

    public void updateCalculatedTaxAmount(Long calculated_tax_amount) {
        this.calculated_tax_amount = calculated_tax_amount;
    }

    public void updatePremiumExpenses(Long premium_expenses) {
        this.premium_expenses = premium_expenses;
    }

    public void updateEducationalExpenses(Long educational_expenses) {
        this.educational_expenses = educational_expenses;
    }

    public void updateDonationExpenses(Long donation_expenses) {
        this.donation_expenses = donation_expenses;
    }

    public void updateMedicalExpenses(Long medical_expenses) {
        this.medical_expenses = medical_expenses;
    }

    public void updateRetirementPension(Long retirement_pension) {
        this.retirement_pension = retirement_pension;
    }*/
}
