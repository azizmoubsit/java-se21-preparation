/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

import java.math.BigDecimal;
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
        pm.createProduct(150, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.parseReview("150,5,Nice hot cup of tea");
        pm.reviewProduct(150, Rating.FOUR, "Good product");
        pm.reviewProduct(150, Rating.ONE, "Bad product");
        pm.reviewProduct(150, Rating.TWO, "product not good");
        pm.reviewProduct(150, Rating.ONE, "Very Bad");
//        pm.dumpData();
//        pm.restoreData();
        pm.printProductReport(164);
        Comparator<Product> sortByRating = Comparator.comparingInt(pr -> pr.getRating().ordinal());
        Comparator<Product> sortByPrice = Comparator.comparing(Product::getPrice);
        Predicate<Product> filter = (pr) -> pr.getPrice().floatValue() < 10;
        pm.printProducts(sortByRating.thenComparing(sortByPrice).reversed(), filter);
    }
}
