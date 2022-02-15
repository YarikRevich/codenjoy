package com.codenjoy.dojo.spacerace.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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

import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameSettingsTest {

    @Test
    public void shouldGetAllKeys() {
        assertEquals("TICKS_TO_RECHARGE   =[Game] Ticks to recharge\n" +
                    "BULLETS_COUNT       =[Game] Bullets count\n" +
                    "DESTROY_BOMB_SCORE  =[Score] Destroy bomb score\n" +
                    "DESTROY_STONE_SCORE =[Score] Destroy stone score\n" +
                    "DESTROY_ENEMY_SCORE =[Score] Destroy enemy score\n" +
                    "LOSE_PENALTY        =[ScoreL ose penalty\n" +
                    "LEVEL_MAP           =[Level] Level map",
                TestUtils.toString(new GameSettings().allKeys()));
    }
}