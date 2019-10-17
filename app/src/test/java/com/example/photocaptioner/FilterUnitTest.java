package com.example.photocaptioner;

import org.junit.Test;

import FilterImages.Filter;

import static org.junit.Assert.assertEquals;

public class FilterUnitTest {
    @Test
    public void gpsIsCorrect(){
        assertEquals(true, Filter.filterByGPS ("100/1", "130/1", "80/1", "100/1","90/1", "120/1"));
    }

    @Test
    public void gpsIsInCorrect(){
        assertEquals(false, Filter.filterByGPS ("100/1", "130/1", "80/1", "100/1","200/1", "120/1"));
    }

    @Test
    public void dateIsCorrect(){
        assertEquals(true, Filter.filterByDate ("2019:05:06", "2019-03-01", "2019-09-01"));
    }

    @Test
    public void dateIsInCorrect(){
        assertEquals(false, Filter.filterByDate ("2019:12:06", "2019-03-01", "2019-09-01"));
    }

    @Test
    public void captionIsCorrect(){
        String[] dictionary = {"hello", "goodbye"};
        assertEquals (true, Filter.filterByCaption (dictionary, "hello"));
    }

    @Test
    public void captionIsInCorrect(){
        String[] dictionary = {"hello", "goodbye"};
        assertEquals (false, Filter.filterByCaption (dictionary, "seeya"));
    }
}
