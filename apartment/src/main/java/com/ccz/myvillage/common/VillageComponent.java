package com.ccz.myvillage.common;

import com.ccz.myvillage.dto.TabMenuItem;

import java.util.Arrays;
import java.util.List;

public class VillageComponent {
    public static VillageComponent get() {
        return new VillageComponent();
    }

    private VillageComponent() {
    }

    public List<TabMenuItem> getComponents() {
        return Arrays.asList(
                new TabMenuItem("0", "우리동네"),
                new TabMenuItem("0", "강동롯데캐슬퍼스트"),
                new TabMenuItem("1", "암사동"),
                new TabMenuItem("2", "강동구"),
                new TabMenuItem("3", "서울시"));
    }
}
