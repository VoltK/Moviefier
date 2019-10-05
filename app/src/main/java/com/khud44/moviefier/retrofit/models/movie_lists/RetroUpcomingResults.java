
package com.khud44.moviefier.retrofit.models.movie_lists;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetroUpcomingResults {

    @SerializedName("results")
    @Expose
    private List<RetroMovie> results = null;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("total_results")
    @Expose
    private int totalResults;
    @SerializedName("dates")
    @Expose
    private RetroUpcomingDates dates;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RetroUpcomingResults() {
    }

    /**
     * 
     * @param results
     * @param dates
     * @param totalResults
     * @param page
     * @param totalPages
     */
    public RetroUpcomingResults(List<RetroMovie> results, int page, int totalResults, RetroUpcomingDates dates, int totalPages) {
        super();
        this.results = results;
        this.page = page;
        this.totalResults = totalResults;
        this.dates = dates;
        this.totalPages = totalPages;
    }

    public List<RetroMovie> getResults() {
        return results;
    }

    public void setResults(List<RetroMovie> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public RetroUpcomingDates getDates() {
        return dates;
    }

    public void setDates(RetroUpcomingDates dates) {
        this.dates = dates;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
