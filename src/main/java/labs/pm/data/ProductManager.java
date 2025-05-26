/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * @author aziz
 **/
public class ProductManager {
//    private Product product;
//    private Review review;
//    private Review[] reviews = new  Review[1];
    Map<Product, List<Review>> products = new HashMap<>();
    private Locale locale;
    private ResourceBundle resources;
    private DateTimeFormatter dateFormatter;
    private NumberFormat moneyFormat;

    public ProductManager(Locale locale) {
        this.locale = locale;
        resources = ResourceBundle.getBundle("resources", locale);
        dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        moneyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product findProduct(int id) {
        for (Product product: products.keySet())
            if(product.getId() == id)
                return product;
        return null;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product);
        reviews.add(new Review(rating, comments));

        int sum = 0;
        for(Review review: reviews) {
            sum += review.rating().ordinal();
        }
        Product newProduct = product.applyRating(Math.round((float)sum/reviews.size()));
        products.put(newProduct, reviews);
        return newProduct;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        return reviewProduct(findProduct(id), rating, comments);
    }

    public void printProductReport(Product product) {
        StringBuilder txt = new StringBuilder();
        String type = switch (product) {
            case Food ignored -> resources.getString("food");
            case Drink ignored -> resources.getString("drink");
        };
        txt.append(MessageFormat.format(resources.getString("product"),
                product.getName(),
                moneyFormat.format(product.getPrice()),
                product.getRating().getStars(),
                dateFormatter.format(product.getBestBefore()),
                type));
        txt.append("\n");
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
        if(reviews.isEmpty())
            txt.append(resources.getString("no.reviews"));
        else
            for (Review review : reviews) {
                txt.append(MessageFormat.format(resources.getString("review"),
                        review.rating().getStars(),
                        review.comments()));
                txt.append("\n");
            }
        System.out.println(txt);
    }

    public void printProductReport(int id) {
        printProductReport(findProduct(id));
    }
}