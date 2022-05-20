package es.jaimetruman.menus.menubuilder;

import es.jaimetruman.menus.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public final class MenuBuildResult {
    @Getter private final List<Page> pages;
}
