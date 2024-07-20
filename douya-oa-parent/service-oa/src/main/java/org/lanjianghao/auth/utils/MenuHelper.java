package org.lanjianghao.auth.utils;

import org.lanjianghao.model.base.BaseEntity;
import org.lanjianghao.model.system.SysMenu;

import java.util.*;
import java.util.stream.Collectors;

public class MenuHelper {
    private final static long ROOT_MENU_ID = 0;

    public static List<SysMenu> buildTree(List<SysMenu> list) {
        list.forEach(menu -> menu.setChildren(new ArrayList<>()));
        Map<Long, SysMenu> menuMap = buildMap(list);
        return buildTree(list, menuMap);
    }

    private static Map<Long, SysMenu> buildMap(List<SysMenu> list) {
        return list.stream().collect(Collectors.toMap(
                BaseEntity::getId,
                menu -> menu
        ));
    }

    private static List<SysMenu> buildTree(List<SysMenu> list, Map<Long, SysMenu> map) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : list) {
            if (menu.getParentId() != 0) {
                map.get(menu.getParentId()).getChildren().add(menu);
            } else {
                tree.add(menu);
            }
        }
        return tree;
    }
}
