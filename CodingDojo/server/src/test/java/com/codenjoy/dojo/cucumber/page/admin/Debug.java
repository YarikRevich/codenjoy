package com.codenjoy.dojo.cucumber.page.admin;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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

import com.codenjoy.dojo.cucumber.page.PageObject;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.codenjoy.dojo.cucumber.utils.Assert.assertEquals;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
@RequiredArgsConstructor
public class Debug extends PageObject {

    public WebElement status() {
        return web.element("#debug td b");
    }

    private WebElement stopButton() {
        return web.button("#debug", actions.stopDebug);
    }

    private WebElement startButton() {
        return web.button("#debug", actions.startDebug);
    }

    public void assertSuspended() {
        assertEquals("The debug was suspended", status().getText());
        assertEquals(actions.startDebug, startButton().getAttribute("value"));
    }

    public void assertStarted() {
        assertEquals("The debug in progress", status().getText());
        assertEquals(actions.stopDebug, stopButton().getAttribute("value"));
    }

    public void stop() {
        stopButton().click();
    }

    public void start() {
        startButton().click();
    }
}