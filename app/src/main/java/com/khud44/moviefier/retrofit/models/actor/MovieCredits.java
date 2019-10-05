
package com.khud44.moviefier.retrofit.models.actor;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieCredits {

    @SerializedName("cast")
    @Expose
    private List<Cast> cast = null;
    @SerializedName("crew")
    @Expose
    private List<Crew> crew = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MovieCredits() {
    }

    /**
     * 
     * @param cast
     * @param crew
     */
    public MovieCredits(List<Cast> cast, List<Crew> crew) {
        super();
        this.cast = cast;
        this.crew = crew;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

}
