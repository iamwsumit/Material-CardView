package com.sumit.materialcardview.helpers;

import com.google.appinventor.components.common.Default;
import com.google.appinventor.components.common.OptionList;

import java.util.HashMap;
import java.util.Map;

public enum Orientation implements OptionList<Integer> {
    @Default
    Vertical(1),
    Horizontal(0);

    private static final Map<Integer, Orientation> lookup = new HashMap<>();

    static {
        for (Orientation anim : Orientation.values()) {
            lookup.put(anim.toUnderlyingValue(), anim);
        }
    }

    private final int tech;

    Orientation(int tech) {
        this.tech = tech;
    }

    public static Orientation fromUnderlyingValue(Integer anim) {
        return lookup.get(anim);
    }

    @Override
    public Integer toUnderlyingValue() {
        return tech;
    }

}
