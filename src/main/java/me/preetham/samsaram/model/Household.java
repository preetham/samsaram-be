package me.preetham.samsaram.model;

import lombok.Getter;

@Getter
public class Household {
    private long id;
    private String name;
    private String imageUrl;
    private HouseholdState state;

    public Household(long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.state = HouseholdState.ACTIVE;
    }
}
