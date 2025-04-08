package com.assignment.entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String sharedTo;

    @Column(nullable = false)
    private Date shareDate;
}
