package com.codenjoy.dojo.battlecity.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.battlecity.model.items.Prize;
import com.codenjoy.dojo.battlecity.services.GameSettings;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.settings.Chance;

import static com.codenjoy.dojo.battlecity.services.GameSettings.Keys.PRIZE_ON_FIELD;
import static com.codenjoy.dojo.battlecity.services.GameSettings.Keys.PRIZE_WORKING;


public class PrizeGenerator {

    private Chance<Elements> chance;
    private Field field;

    private GameSettings settings;

    public PrizeGenerator(Field field, GameSettings settings) {
        this.field = field;
        this.chance = settings.chance();
        this.settings = settings;
    }

    public void drop(Point pt) {
        Elements type = chance.getAny();

        field.add(new Prize(pt,
                settings.integer(PRIZE_ON_FIELD),
                settings.integer(PRIZE_WORKING),
                type));
    }
}
