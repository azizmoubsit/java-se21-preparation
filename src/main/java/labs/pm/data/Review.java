/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

import java.io.Serializable;

/**
 * @author aziz
 **/
public record Review(Rating rating, String comments) implements Serializable, Comparable<Review> {
    @Override
    public String toString() {
        return "Rating: " + rating.getStars() + "\tComments: " + comments;
    }

    @Override
    public int compareTo(Review o) {
        return o.rating().ordinal() - this.rating().ordinal();
    }
}
