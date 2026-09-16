package com.example.gaming_ecomerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_payment", nullable = false)
    private Double totalPayment;

    @Column(name = "id_payment")
    private String idPayment;

    // En Strapi esto estaba como JSON. Podemos guardarlo como texto JSON
    // o más adelante crear entidades dedicadas, por ahora usamos String
    @Column(name = "address_shipping", columnDefinition = "TEXT")
    private String addressShipping;

    @Column(columnDefinition = "TEXT")
    private String products;

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
