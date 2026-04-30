package com.example.barbershop.application.dto;

public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private String price;
    private Integer durationMinutes;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}