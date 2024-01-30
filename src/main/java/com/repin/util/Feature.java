package com.repin.util;

import lombok.Getter;

@Getter
public enum Feature {
    TRAILERS("Trailers"),
    COMMENTARIES("Commentaries"),
    DELETED_SCENES("Deleted Scenes"),
    BEHIND_THE_SCENES("Behind the Scenes");

    private final String value;

    Feature(String value) {
        this.value = value;
    }

    public static Feature getFeatureByValue(String value) {
        if (value == null) return null;

        for (Feature feature : Feature.values()) {
            if (feature.value.equals(value)) return feature;
        }

        return null;
    }
}
