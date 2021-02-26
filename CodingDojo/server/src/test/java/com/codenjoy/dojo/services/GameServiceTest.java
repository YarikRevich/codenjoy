package com.codenjoy.dojo.services;

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


import com.codenjoy.dojo.services.mocks.FirstGameType;
import com.codenjoy.dojo.services.mocks.SecondGameType;
import com.codenjoy.dojo.services.nullobj.NullGameType;
import com.codenjoy.dojo.services.room.RoomService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static com.codenjoy.dojo.services.mocks.FirstGameSettings.Keys.PARAMETER1;
import static com.codenjoy.dojo.services.mocks.SecondGameSettings.Keys.PARAMETER3;
import static com.codenjoy.dojo.services.mocks.SecondGameSettings.Keys.PARAMETER4;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class GameServiceTest {
    
    private GameServiceImpl service;

    @Before
    public void setup() {
        forGames(FirstGameType.class, SecondGameType.class);
        service.init();
    }

    private void forGames(Class... classes) {
        service = new GameServiceImpl() {

            {
                excludeGames = new String[0];
                roomService = new RoomService();
            }

            @Override
            public Collection<? extends Class> findInPackage(String packageName) {
                return Arrays.asList(classes);
            }
        };
    }

    @Test
    public void shouldGetGameNames() {
        assertEquals("[first, second]", 
                service.getGameNames().toString());
    }

    @Test
    public void shouldGetSpritesNames() {
        assertEquals("{first=[none, wall, hero], second=[none, red, green, blue]}", 
                service.getSpritesNames().toString());
    }
    
    @Test
    public void shouldGetOnlyGameNames() {
        assertEquals("[first, second]", 
                service.getOnlyGameNames().toString());
    }
    
    @Test
    public void shouldGetSpritesValues() {
        assertEquals("{first=[ , ☼, ☺], second=[ , R, G, B]}", 
                service.getSpritesValues().toString());
    }
    
    @Test
    public void shouldGetSprites() {
        assertEquals("{first=[none= , wall=☼, hero=☺], second=[none= , red=R, green=G, blue=B]}", 
                service.getSprites().toString());
    }
    
    @Test
    public void shouldGetDefaultGame() {
        assertEquals("first", 
                service.getDefaultGame());
    }

    @Test
    public void shouldGetGame() {
        assertEquals(FirstGameType.class,
                service.getGame("first").getClass());

        assertEquals(SecondGameType.class,
                service.getGame("second").getClass());

        assertEquals(NullGameType.class,
                service.getGame("not-exists").getClass());
    }

    // TODO этот тест надо запускать с парамером mvn test -DallGames иначе не тянутся дипенденси игр а хотелось бы их чекнуть так же 
    @Test
    public void shouldGetPngForSprites() {
        // given
        forGames(new GameServiceImpl().findInPackage("com.codenjoy.dojo").toArray(new Class[0]));
        
        // when
        Map<String, List<String>> sprites = service.getSpritesNames();
        System.out.println(sprites.toString());
        
        // then
        List<String> errors = new LinkedList<>();
        for (Map.Entry<String, List<String>> entry : sprites.entrySet()) {
            for (String sprite : entry.getValue()) {
                String spriteUri = String.format("/%s/%s.png", entry.getKey(), sprite);
                File file = new File("target/test-classes/sprite" + spriteUri);
                if (!file.exists() && !new File("/sprite" + spriteUri).exists()) {
                    errors.add("Файл не найден: " + file.getAbsolutePath());
                }
            }
        }

        assertEquals(errors.toString().replace(',', '\n'), 
                true, errors.isEmpty());
    }

    @Test
    public void shouldSameSettings_whenGetGameByRoomName() {
        // given
        List<GameType> list = new LinkedList<>(){{
            add(service.getGame("first", "room1"));
            add(service.getGame("first", "room1"));

            add(service.getGame("first", "room2"));

            add(service.getGame("second", "room3"));
            add(service.getGame("second", "room3"));

            add(service.getGame("second", "room4"));
            add(service.getGame("second", "room4"));

            add(service.getGame("first"));

            add(service.getGame("second"));
        }};

        // then
        assertEquals("First[Parameter 1=15, Parameter 2=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(0).getSettings().getParameter(PARAMETER1.key()).update(123);

        // then
        assertEquals("First[Parameter 1=123, Parameter 2=true]\n" +
                        "First[Parameter 1=123, Parameter 2=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(1).getSettings().getParameter(PARAMETER1.key()).update(234);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(2).getSettings().getParameter(PARAMETER1.key()).update(345);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(3).getSettings().getParameter(PARAMETER4.key()).update(false);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=false]\n" +
                        "Second[Parameter 3=43, Parameter 4=false]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(4).getSettings().getParameter(PARAMETER3.key()).update(456);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(5).getSettings().getParameter(PARAMETER3.key()).update(567);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=true]\n" +
                        "Second[Parameter 3=567, Parameter 4=true]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(6).getSettings().getParameter(PARAMETER4.key()).update(false);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(7).getSettings().getParameter(PARAMETER1.key()).update(678);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        // TODO сделать так, чтобы изменение базовых настроек
                        //  влияло на будущие созданные настройки комнат
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

        // when
        list.get(8).getSettings().getParameter(PARAMETER3.key()).update(789);

        // then
        assertEquals("First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=234, Parameter 2=true]\n" +
                        "First[Parameter 1=345, Parameter 2=true]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=456, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        "Second[Parameter 3=567, Parameter 4=false]\n" +
                        "First[Parameter 1=15, Parameter 2=true]\n" +
                        // TODO сделать так, чтобы изменение базовых настроек
                        //  влияло на будущие созданные настройки комнат
                        "Second[Parameter 3=43, Parameter 4=true]\n",
                toString(list));

    }

    private String toString(List<GameType> list) {
        return list.stream()
                .map(GameType::getSettings)
                .map(settings -> settings.toString())
                .reduce("", (out, string) -> out.concat(string + "\n"));
    }

}
