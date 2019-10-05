
package com.khud44.moviefier.retrofit.models.movie_lists;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetroUpcomingDates {

    @SerializedName("maximum")
    @Expose
    private String maximum;
    @SerializedName("minimum")
    @Expose
    private String minimum;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RetroUpcomingDates() {
    }

    /**
     * 
     * @param minimum
     * @param maximum
     */
    public RetroUpcomingDates(String maximum, String minimum) {
        super();
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

}
