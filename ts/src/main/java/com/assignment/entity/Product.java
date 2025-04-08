package com.assignment.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<Review> reviews;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void setFormattedPrice(String formattedPrice) {
        if (formattedPrice != null) {
            this.price = Double.parseDouble(formattedPrice.replace(",", ""));
        }
    }

    private boolean active;
}