package com.mukkebi.foodfinder.core.domain;

import java.util.List;

public interface RestaurantFinder {
    List<Restaurant> findNearBy(Double latitude, Double longitude, Integer radius);
}