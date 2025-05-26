/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

/**
 * @author aziz
 **/
@FunctionalInterface
public interface Rateable<T> {
    Rating DEFAULT_RATING = Rating.NOT_RATED;

    T applyRating(Rating rating);

    static Rating convert(int stars) {
        return (stars >= 0 && stars <= 5) ? Rating.values()[stars] : DEFAULT_RATING;
    }

    default T applyRating(int stars) {
        return applyRating(convert(stars));
    }

    default Rating getRating() {
        return DEFAULT_RATING;
    }
}
