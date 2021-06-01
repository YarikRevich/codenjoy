package com.codenjoy.dojo.cucumber.page;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.client.Closeable;
import com.codenjoy.dojo.cucumber.page.admin.ActiveGames;
import com.codenjoy.dojo.services.AutoSaver;
import com.codenjoy.dojo.services.PlayerService;
import com.codenjoy.dojo.services.TimerService;
import com.codenjoy.dojo.services.dao.ActionLogger;
import com.codenjoy.dojo.services.log.DebugService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static org.junit.Assert.assertEquals;

@Component
@RequiredArgsConstructor
@Scope(SCOPE_CUCUMBER_GLUE)
public class AdminPage implements Closeable {

    public static final String LOAD_ALL_HREF_XPATH = "//a[@href='/codenjoy-contest/admin/player/loadAll?room=sample#savePlayersGame']";
    public static final String INACTIVITY_KICK_CHECKBOX_XPATH = "//input[@name='inactivity.kickEnabled']";
    public static final String INACTIVITY_TICKS_INPUT_XPATH = "//input[@name='inactivity.inactivityTimeout']";
    public static final String INACTIVITY_SAVE_BUTTON_XPATH = "//table[@id='inactivity']//input[@value='Save']";
    public static final String PLAYER_INACTIVE_TICKS_XPATH = "//span[@class='span-ticksInactive']";

    public static final String PLAYER_INACTIVE_TICKS_XPATH_FORMAT = "//span[@class='span-ticksInactive'][preceding::td//input[@value='%s']]";

    public static final String URL = "/admin?room=";
    public static final BiFunction<String, String, String> CREATE_ROOM_URL =
            (room, game) -> String.format("/admin?room=%s&game=%s", room, game);

    // application services
    private final AutoSaver autoSaver;
    private final DebugService debugService;
    private final TimerService timerService;
    private final ActionLogger actionLogger;
    private final PlayerService playerService;

    // page objects
    private final Page page;
    private final WebDriverWrapper web;
    private final ActiveGames activeGames;

    @Override
    public void close() {
        actionLogger.pause();
        autoSaver.resume();
        debugService.pause();
        timerService.resume();
        playerService.openRegistration();
        timerService.changePeriod(1000);
    }

    public void open() {
        web.open("/admin");
    }

    public void open(String room) {
        web.open(URL + room);
    }

    public void assertOnPage() {
        page.assertPage("admin");
    }

    public void assertRegistrationActive(boolean active) {
        String status = registrationActiveStatus().getText();
        String linkText = registrationChangeActiveLink().getText();
        if (active) {
            assertEquals("Server registration is active", status);
            assertEquals("Close registration", linkText);
        } else {
            assertEquals("Server registration was closed", status);
            assertEquals("Open registration", linkText);
        }
    }

    public WebElement registrationActiveStatus() {
        return web.element("#closeRegistration td b");
    }

    public WebElement registrationChangeActiveLink() {
        return web.element("#closeRegistration td a");
    }

    public WebElement pauseResumeGameStatus() {
        return web.element("#pauseGame td b");
    }

    public WebElement pauseResumeGameLink() {
        return web.element("#pauseGame td a");
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public WebElement loadAllHRef() {
        By xpath = By.xpath(LOAD_ALL_HREF_XPATH);
        return web.elementBy(xpath);
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public WebElement inactivityKickCheckbox() {
        By xpath = By.xpath(INACTIVITY_KICK_CHECKBOX_XPATH);
        return web.elementBy(xpath);
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public WebElement inactivityTicksInput() {
        By xpath = By.xpath(INACTIVITY_TICKS_INPUT_XPATH);
        return web.elementBy(xpath);
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public WebElement inactivitySaveButton() {
        By xpath = By.xpath(INACTIVITY_SAVE_BUTTON_XPATH);
        return web.elementBy(xpath);
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public List<WebElement> playerInactiveTicks() {
        By xpath = By.xpath(PLAYER_INACTIVE_TICKS_XPATH);
        return web.elementsBy(xpath);
    }

    // TODO [RK#1]: prefer using css selector to xpath
    public WebElement playerInactiveTicks(String value) {
        By xpath = By.xpath(String.format(PLAYER_INACTIVE_TICKS_XPATH_FORMAT, value));
        return web.elementBy(xpath);
    }

    public void assertGameIsActive(boolean active) {
        String status = pauseResumeGameStatus().getText();
        String linkText = pauseResumeGameLink().getText();
        if (active) {
            assertEquals("Game in this room is active", status);
            assertEquals("Pause game", linkText);
        } else {
            assertEquals("Game in this room was suspended", status);
            assertEquals("Resume game", linkText);
        }
    }

    public void selectRoom(String room) {
        activeGames.selectRoom(room);
    }

    public void assertGameAndRoom(String game, String room) {
        page.assertGame(game);
        page.assertRoom(room);
        page.assertUrl(URL + room);
    }

    public void assertRoomsAvailable(String expected) {
        assertEquals(expected, activeGames.getRooms().toString());
    }

    public void createNewRoomForGame(String room, String game) {
        page.open(CREATE_ROOM_URL.apply(room, game));
    }

    public void assertPlayersInRooms(String expected) {
        assertEquals(expected, activeGames.getPlayersInRooms().toString());
    }
}
