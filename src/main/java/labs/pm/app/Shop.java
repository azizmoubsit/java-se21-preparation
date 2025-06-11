/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;

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
        Comparator<Product> sortByRating = Comparator.comparingInt(pr -> pr.getRating().ordinal());
        Comparator<Product> sortByPrice = Comparator.comparing(Product::getPrice);
        Predicate<Product> filter = (pr) -> pr.getPrice().floatValue() < 10;
        pm.printProducts(sortByRating.thenComparing(sortByPrice).reversed(), filter);
    }
}
