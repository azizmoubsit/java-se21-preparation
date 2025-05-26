/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

/**
 * @author aziz
 **/
public enum Rating {
    NOT_RATED("☆☆☆☆☆"),
    ONE("★☆☆☆☆"),
    TWO("★★☆☆☆"),
    THREE("★★★☆☆"),
    FOUR("★★★★☆"),
    FIVE("★★★★★");

    private String stars;

    Rating(String stars) {
        this.stars = stars;
    }

    public String getStars() {
        return stars;
    }
}
