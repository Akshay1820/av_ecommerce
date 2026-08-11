package com.ecommerce.user.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collation = "address")
@NoArgsConstructor
public class Address {

    @Id
    private Long id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
