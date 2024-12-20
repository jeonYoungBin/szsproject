package com.szs.szsproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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

    @Column(name = "total_income")
    private String totalIncome;   //종합소득금액
    @Column(name = "total_tax_deductions")
    private String TotalTaxDeduction; //세액공제
    /**
     * 소득 공제
     */
    @Column(name = "total_pension_deductions")
    private String totalPensionDeductions; //국민연금(총합)
    @Column(name = "total_creditCard_deduction")
    private String totalCreditCardDeduction; //신용카드소득세(총합)

    /*@LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;*/

    public void updateTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void updateTotalPensionDeductions(String totalPensionDeductions) {
        this.totalPensionDeductions = totalPensionDeductions;
    }

    public void updateTotalCreditCardDeduction(String totalCreditCardDeduction) {
        this.totalCreditCardDeduction = totalCreditCardDeduction;
    }

    public void updateTotalTaxDeduction(String totalTaxDeduction) {
        this.TotalTaxDeduction = totalTaxDeduction;
    }
}
