/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

/**
 * @author aziz
 **/
public record Review(Rating rating, String comments) implements Comparable<Review> {
    @Override
    public String toString() {
        return "Rating: " + rating.getStars()+ "\tComments: " + comments;
    }

    @Override
    public int compareTo(Review o) {
        return o.rating().ordinal() - this.rating().ordinal();
    }
}
