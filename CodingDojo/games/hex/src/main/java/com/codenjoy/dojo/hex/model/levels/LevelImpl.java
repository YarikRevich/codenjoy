package com.codenjoy.dojo.hex.model.levels;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.hex.model.Hero;
import com.codenjoy.dojo.hex.model.items.Wall;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.utils.LevelUtils;

import java.util.List;

import static com.codenjoy.dojo.games.hex.Element.*;

public class LevelImpl implements Level {

    private LengthToXY xy;
    private String map;

    public LevelImpl(String map) {
        this.map = LevelUtils.clear(map);
        xy = new LengthToXY(getSize());
    }

    @Override
    public int getSize() {
        return (int) Math.sqrt(map.length());
    }

    @Override
    public List<Hero> getHeroes() {
        return LevelUtils.getObjects(xy, map,
                Hero::new,
                HERO1, HERO2);
    }

    @Override
    public List<Wall> getWalls() {
        return LevelUtils.getObjects(xy, map,
                Wall::new,
                WALL);
    }
}