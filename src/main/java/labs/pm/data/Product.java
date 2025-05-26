/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * {@code Product} class represents properties and behaviour of
 * product objects in the Product Management System
 * <br>
 * Each product has an id, name and price
 * <br>
 * Each product can have a discount, calculated based on a
 * {@link DISCOUNT_RATE discount rate}
 * @version 1.0
 * @author aziz
 **/
public sealed abstract class Product implements Rateable<Product> permits Food, Drink  {
    private static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);
    private int id;
    private String name;
    private BigDecimal price;
    private final Rating rating;

    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

//    public Product(int id, String name, BigDecimal price) {
//        this(id, name, price, Rating.NOT_RATED);
//    }

//    public Product() {
//        this(-1, "N/A", BigDecimal.ZERO, Rating.NOT_RATED);
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public Rating getRating() {
        return rating;
    }

    /*
     * Assumes that the best before date is today
     * @returns the current date
     */
    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return "Type=" + this.getClass()
                + "\tID=" + this.getId()
                + "\tName=" + this.getName()
                + "\tPrice=" + this.getPrice()
                + "\tDiscount=" + this.getDiscount()
                + "\tRating=" + this.getRating().getStars()
                + "\tBest Before=" + this.getBestBefore();
    }

//    public abstract Product applyRating(Rating rating);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof Product p
//                && o.getClass() == this.getClass()
        ) {
            return  this.id == p.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
