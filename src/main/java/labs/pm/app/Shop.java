/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * {@code Shop} class represents an application that manages Products
 *
 * @author aziz
 * @version 1.0
 **/
public class Shop {
    public static void main(String[] args) {
        ProductManager pm = new ProductManager("fr-FR");
//        Product p1 = pm.createProduct(1, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        Product p1 = pm.parseProduct("D,1,Tea,1.99,4,2025-06-20");
        pm.parseReview("1,5,Nice hot cup of tea");
//        pm.reviewProduct(1, Rating.FOUR, "Good product");
//        pm.printProductReport(1);
        pm.reviewProduct(1, Rating.ONE, "Bad product");
        pm.reviewProduct(1, Rating.TWO, "product not good");
        pm.reviewProduct(1, Rating.ONE, "Very Bad");
//        pm.printProductReport(1);
        Product p2 = pm.createProduct(2, "Burger", BigDecimal.valueOf(9.99), Rating.NOT_RATED, LocalDate.now().minusDays(4));
//        pm.printProductReport(2);
//        pm.reviewProduct(1, Rating.FOUR, "Good product");
        pm.reviewProduct(2, Rating.FIVE, "Perfect product");
        pm.reviewProduct(2, Rating.TWO, "Bad product");
        pm.reviewProduct(2, Rating.THREE, "Good");
        pm.reviewProduct(2, Rating.ONE, "Worst product");
        pm.reviewProduct(2, Rating.TWO, "Bad");
        pm.reviewProduct(2, Rating.FOUR, "I think is good product");
        pm.reviewProduct(2, Rating.TWO, "It is a good product");
        pm.reviewProduct(2, Rating.THREE, "Not bad");
        pm.reviewProduct(2, Rating.TWO, "Bad product");
        pm.reviewProduct(2, Rating.FIVE, "WOW!");
//        pm.printProductReport(2);
//        pm.printProductReport(1);
        Comparator<Product> sortByRating = Comparator.comparingInt(pr -> pr.getRating().ordinal());
        Comparator<Product> sortByPrice = Comparator.comparing(Product::getPrice);
        Predicate<Product> filter = (pr) -> pr.getPrice().floatValue() < 2;
        pm.printProducts(sortByRating.thenComparing(sortByPrice).reversed(), filter);
//        Product d1 = pm.createProduct(10, "Coffee", BigDecimal.valueOf(2.99), Rating.THREE);
//        Product f1 = pm.createProduct(108, "Burger", BigDecimal.valueOf(9.99), Rating.THREE, LocalDate.now().plusDays(0));
//        Product f2 = pm.createProduct(10, "Burger", BigDecimal.valueOf(9.99), Rating.THREE, LocalDate.now().plusDays(2));
//        Product d2 = pm.createProduct(10, "Burger", BigDecimal.valueOf(9.99), Rating.THREE);
//        Product d3 = d2.applyRating(Rating.ONE);
//        System.out.println(p1);
//        System.out.println(d1);
//        System.out.println(d2);
//        System.out.println(d3.getBestBefore());
//        System.out.println(f1);
//        System.out.println(f2);
    }
}
