/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author aziz
 **/
public final class Food extends Product {
    private LocalDate bestBefore;

    Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

//    @Override
//    public String toString() {
//        return super.toString() + " bestBefore=" + getBestBefore();
//    }

    @Override
    public BigDecimal getDiscount() {
        return (bestBefore.isEqual(LocalDate.now()) ? super.getDiscount() : BigDecimal.ZERO);
    }

    @Override
    public Product applyRating(Rating rating) {
        return new Food(getId(), getName(), getPrice(), rating, getBestBefore());
    }
}
