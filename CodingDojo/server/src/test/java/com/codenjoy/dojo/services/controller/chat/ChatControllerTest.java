package com.codenjoy.dojo.services.controller.chat;

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


import com.codenjoy.dojo.services.FieldService;
import com.codenjoy.dojo.services.TimeService;
import com.codenjoy.dojo.services.chat.ChatControl;
import com.codenjoy.dojo.services.chat.ChatService;
import com.codenjoy.dojo.services.chat.ChatType;
import com.codenjoy.dojo.services.chat.Filter;
import com.codenjoy.dojo.services.controller.AbstractControllerTest;
import com.codenjoy.dojo.services.controller.Controller;
import com.codenjoy.dojo.services.dao.Chat;
import com.codenjoy.dojo.services.dao.ChatTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.chat.ChatType.*;
import static com.codenjoy.dojo.stuff.SmartAssert.assertEquals;
import static java.util.stream.Collectors.joining;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChatControllerTest extends AbstractControllerTest<String, ChatControl> {

    @Autowired
    private ChatController controller;

    @Autowired
    private FieldService fields;

    @SpyBean
    private ChatService chatService;

    @SpyBean
    private TimeService time;

    @Autowired
    private Chat chat;

    private List<Chat.Message> messages = new LinkedList<>();

    @Before
    public void setup() {
        super.setup();

        chat.removeAll();
        fields.removeAll();

        createPlayer("player", "room", "first");
        login.asUser("player", "player");
    }

    @Override
    protected String endpoint() {
        return "chat-ws";
    }

    @Override
    protected ChatControl control(String id) {
        // TODO так себе решение, если найдешь в мокито как подписаться сразу на все методы - супер!
        ChatControl control = spy(chatService.control(id));
        Answer<?> answer = invocation -> {
            serverReceived(String.format("%s(%s)",
                    invocation.getMethod().getName(),
                    Arrays.stream(invocation.getArguments())
                            .map(Object::toString)
                            .collect(joining(", "))));

            return invocation.callRealMethod();
        };
        doAnswer(answer).when(control).get(anyInt(), anyString());
        doAnswer(answer).when(control).delete(anyInt(), anyString());
        doAnswer(answer).when(control).getAllRoom(any(Filter.class));
        doAnswer(answer).when(control).getAllTopic(anyInt(), any(Filter.class));
        doAnswer(answer).when(control).getAllField(any(Filter.class));
        doAnswer(answer).when(control).postRoom(anyString(), anyString());
        doAnswer(answer).when(control).postTopic(anyInt(), anyString(), anyString());
        doAnswer(answer).when(control).postField(anyString(), anyString());
        return control;
    }

    public void nowIs(long time) {
        when(this.time.now()).thenReturn(time);
    }

    @Override
    protected Controller<String, ChatControl> controller() {
        return controller;
    }

    @Test
    public void shouldGet_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'get', " +
                "'data':{'id':1, 'room':'room'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[get(1, room)]", receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                "'message':'There is no message with id '1' in room 'room''}]",
                client(0).messages());
    }

    public Chat.Message addMessage(String room, String player, Integer topicId, ChatType type) {
        return ChatTest.addMessage(chat, messages, room, player, topicId, type);
    }

    @Test
    public void shouldGet_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1

        // when
        client(0).sendToServer("{'command':'get', " +
                "'data':{'id':1, 'room':'room'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[get(1, room)]", receivedOnServer());
        assertEquals("[{'id':1,'text':'message1','room':'room','topicId':null," +
                        "'playerId':'player','playerName':'player-name','time':1615231523345}]",
                client(0).messages());
    }

    @Test
    public void shouldDelete_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1

        // when
        client(0).sendToServer("{'command':'delete', " +
                "'data':{'id':1, 'room':'room'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[delete(1, room)]", receivedOnServer());
        assertEquals("[true]", client(0).messages());

        assertEquals(null, chat.getMessageById(1));
    }

    @Test
    public void shouldDelete_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'delete', " +
                "'data':{'id':1, 'room':'room'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[delete(1, room)]", receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'Player 'player' cant delete " +
                        "message with id '1' in room 'room''}]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllRoom_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'getAllRoom', " +
                "'data':{'room':'otherRoom', 'count':1}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllRoom(Filter(room=otherRoom, count=1, " +
                        "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                "'message':'Player 'player' is not in room 'otherRoom''}]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllRoom_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1
        addMessage("room", "player", null, ROOM); // 2
        addMessage("room", "player", null, ROOM); // 3

        // when
        client(0).sendToServer("{'command':'getAllRoom', " +
                "'data':{'room':'room', 'count':2}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllRoom(Filter(room=room, count=2, " +
                "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[[{'id':2,'text':'message2','room':'room','topicId':null," +
                        "'playerId':'player','playerName':'player-name','time':1615231623345}," +
                        "{'id':3,'text':'message3','room':'room','topicId':null," +
                        "'playerId':'player','playerName':'player-name','time':1615231723345}]]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllTopic_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'getAllTopic', " +
                "'data':{'id':1, 'room':'room', 'count':1}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllTopic(1, Filter(room=room, count=1, " +
                        "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'There is no message with id '1' in room 'room''}]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllTopic_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1
        addMessage("room", "player", 1, TOPIC); // 2
        addMessage("room", "player", 1, TOPIC); // 3

        // when
        client(0).sendToServer("{'command':'getAllTopic', " +
                "'data':{'id':1, 'room':'room', 'count':2}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllTopic(1, Filter(room=room, count=2, " +
                        "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[[{'id':2,'text':'message2','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':1615231623345}," +
                        "{'id':3,'text':'message3','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':1615231723345}]]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllField_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'getAllField', " +
                "'data':{'room':'otherRoom', 'count':1}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllField(Filter(room=otherRoom, count=1, " +
                        "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'There is no player 'player' in room 'otherRoom''}]",
                client(0).messages());
    }

    @Test
    public void shouldGetAllField_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1
        addMessage("room", "player", 1, FIELD); // 2
        addMessage("room", "player", 1, FIELD); // 3

        // when
        client(0).sendToServer("{'command':'getAllField', " +
                "'data':{'room':'room', 'count':2}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[getAllField(Filter(room=room, count=2, " +
                        "afterId=null, beforeId=null, inclusive=null))]",
                receivedOnServer());
        assertEquals("[[{'id':2,'text':'message2','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':1615231623345}," +
                        "{'id':3,'text':'message3','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':1615231723345}]]",
                client(0).messages());
    }

    @Test
    public void shouldPostRoom_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'postRoom', " +
                "'data':{'room':'otherRoom', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postRoom(message, otherRoom)]", receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'Player 'player' is not in room 'otherRoom''}]",
                client(0).messages());
    }

    @Test
    public void shouldPostRoom_success() {
        // given
        client(0).start();

        // when
        nowIs(12345L);
        client(0).sendToServer("{'command':'postRoom', " +
                "'data':{'room':'room', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postRoom(message, room)]", receivedOnServer());
        assertEquals("[{'id':1,'text':'message','room':'room','topicId':null," +
                        "'playerId':'player','playerName':'player-name','time':12345}]",
                client(0).messages());
    }

    @Test
    public void shouldPostField_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'postField', " +
                "'data':{'room':'otherRoom', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postField(message, otherRoom)]", receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'There is no player 'player' in room 'otherRoom''}]",
                client(0).messages());
    }

    @Test
    public void shouldPostField_success() {
        // given
        client(0).start();

        // when
        nowIs(12345L);
        client(0).sendToServer("{'command':'postField', " +
                "'data':{'room':'room', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postField(message, room)]", receivedOnServer());
        assertEquals("[{'id':1,'text':'message','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':12345}]",
                client(0).messages());

        assertEquals("Chat.Message(id=1, topicId=1, type=FIELD(3), room=room, " +
                        "playerId=player, time=12345, text=message)",
                chat.getMessageById(1).toString());
    }

    @Test
    public void shouldPostTopic_fail() {
        // given
        client(0).start();

        // when
        client(0).sendToServer("{'command':'postTopic', " +
                "'data':{'id':1, 'room':'room', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postTopic(1, message, room)]", receivedOnServer());
        assertEquals("[{'error':'IllegalArgumentException'," +
                        "'message':'There is no message with id '1' in room 'room''}]",
                client(0).messages());
    }

    @Test
    public void shouldPostTopic_success() {
        // given
        client(0).start();
        addMessage("room", "player", null, ROOM); // 1
        addMessage("room", "player", null, ROOM); // 2

        // when
        nowIs(12345L);
        client(0).sendToServer("{'command':'postTopic', " +
                "'data':{'id':1, 'room':'room', 'text':'message'}}");
        waitForServerReceived();
        waitForClientReceived(0);

        // then
        assertEquals("[postTopic(1, message, room)]", receivedOnServer());
        assertEquals("[{'id':3,'text':'message','room':'room','topicId':1," +
                        "'playerId':'player','playerName':'player-name','time':12345}]",
                client(0).messages());

        assertEquals("Chat.Message(id=3, topicId=1, type=TOPIC(2), " +
                        "room=room, playerId=player, time=12345, text=message)",
                chat.getMessageById(3).toString());
    }
}