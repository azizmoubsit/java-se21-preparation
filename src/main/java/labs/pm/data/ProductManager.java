/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static Logger logger = Logger.getLogger(ProductManager.class.getName());
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceFormatter formatter;
    private ResourceBundle config = ResourceBundle.getBundle("config");
    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private Path reportsFolder = Path.of(config.getString("reports.folder"));
    private Path dataFolder = Path.of(config.getString("data.folder"));
    private Path tempFolder = Path.of(config.getString("temp.folder"));

    public ProductManager(String languageTag) {
        changeLocale(languageTag);
        loadAllData();
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

    public Product findProduct(int id) throws ProductManagerException {
        return products.keySet()
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductManagerException("No product with id: " + id + " was found"));
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
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
            return null;
        }
    }

    public void printProductReport(Product product) {
        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId()));
        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(
                        Files.newOutputStream(
                                productFile,
                                StandardOpenOption.CREATE),
                        StandardCharsets.UTF_8)
        )
        ) {
            out.append(formatter.formatProduct(product));
            out.append(System.lineSeparator());
            List<Review> reviews = products.get(product);
            Collections.sort(reviews);
            if (reviews.isEmpty())
                out.append(formatter.getText("no.reviews"));
            else
                out.append(
                        reviews.stream()
                                .map(review -> formatter.formatReview(review))
                                .collect(Collectors.joining(System.lineSeparator()))
                );

        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public void printProductReport(int id) {
        try {
            printProductReport(findProduct(id));
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public void printProducts(Comparator<Product> sorter, Predicate<Product> filter) {
        products.keySet()
                .stream()
                .filter(filter)
                .sorted(sorter)
                .forEach(this::printProductReport);
    }

    public Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])), (String) values[1]);
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error while parsing review: " + text, e.getMessage());
        }
        return review;
    }

    private List<Review> loadReviews(Product product) {
        List<Review> reviews = null;

        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                reviews = lines.map(this::parseReview)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error loading reviews " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return reviews;
    }

    public Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            char type = ((String) values[0]).charAt(0);
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            int rating = Integer.parseInt((String) values[4]);
            product = switch (type) {
                case 'D' -> createProduct(id, name, price, Rateable.convert(rating));
                case 'F' ->
                        createProduct(id, name, price, Rateable.convert(rating), LocalDate.parse((String) values[5]));
                default -> throw new ProductManagerException("Type must be either 'F' or 'D'");
            };

            products.put(product, new ArrayList<>());
        } catch (ParseException | NumberFormatException | DateTimeParseException | ProductManagerException e) {
            logger.log(Level.WARNING, "Error while parsing product: " + text + " " + e.getMessage());
        }
        return product;
    }

    private Product loadProduct(Path file) {
        Product product = null;
        try (Stream<String> lines = Files.lines(dataFolder.resolve(file), StandardCharsets.UTF_8)) {
            product = parseProduct(lines.findFirst().orElseThrow());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while parsing product: " + e.getMessage());
        }
        return product;
    }

    private void loadAllData() {
        try (Stream<Path> filesList = Files.list(dataFolder)) {
            products = filesList
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(this::loadProduct)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(product -> product, this::loadReviews));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading products with reviews " + e.getMessage());
        }
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