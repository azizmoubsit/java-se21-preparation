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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author aziz
 **/
public class ProductManager {
    private static Map<String, ResourceFormatter> formatters =
            Map.of(
                    "en-US", new ResourceFormatter(Locale.US),
                    "fr-FR", new ResourceFormatter(Locale.FRENCH),
                    "ar-MA", new ResourceFormatter(Locale.of("ar", "MA"))
            );
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceFormatter formatter;

    public ProductManager(String languageTag) {
        System.out.println(languageTag);
        changeLocale(languageTag);
    }

    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public void changeLocale(String languageTag) {
        formatter = formatters.getOrDefault(languageTag, formatters.get("en-US"));
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
        return products.keySet()
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product);
        reviews.add(new Review(rating, comments));

        double average = reviews.stream().mapToInt(r -> r.rating().ordinal()).average().orElse(0);
        Product newProduct = product.applyRating((int) Math.round(average));
        products.put(newProduct, reviews);
        return newProduct;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        return reviewProduct(findProduct(id), rating, comments);
    }

    public void printProductReport(Product product) {
        StringBuilder txt = new StringBuilder();
        txt.append(formatter.formatProduct(product));
        txt.append("\n");
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
        if (reviews.isEmpty())
            txt.append(formatter.getText("no.reviews"));
        else
            txt.append(
                    reviews.stream()
                            .map(review -> formatter.formatReview(review))
                            .collect(Collectors.joining("\n"))
            );
        System.out.println(txt);
    }

    public void printProductReport(int id) {
        printProductReport(findProduct(id));
    }

    public void printProducts(Comparator<Product> sorter, Predicate<Product> filter) {
        products.keySet()
                .stream()
                .filter(filter)
                .sorted(sorter)
                .forEach(this::printProductReport);
    }

    public Map<String, String> getDiscount() {
        return products.keySet()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getRating().getStars(),
                                Collectors.collectingAndThen(Collectors.summingDouble(
                                                product -> product.getDiscount().doubleValue()),
                                        discount -> formatter.moneyFormat.format(discount)
                                )
                        )
                );
    }

    private static class ResourceFormatter {
        private ResourceBundle resources;
        private DateTimeFormatter dateFormatter;
        private NumberFormat moneyFormat;

        public ResourceFormatter(Locale locale) {
            resources = ResourceBundle.getBundle("resources", locale);
            dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            String type = switch (product) {
                case Food ignored -> getText("food");
                case Drink ignored -> getText("drink");
            };

            return MessageFormat.format(getText("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormatter.format(product.getBestBefore()),
                    type);
        }

        private String formatReview(Review review) {
            return MessageFormat.format(getText("review"),
                    review.rating().getStars(),
                    review.comments());
        }

        private String getText(String key) {
            return resources.getString(key);
        }
    }
}