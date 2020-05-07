package com.codenjoy.dojo.services.dao;

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


import com.codenjoy.dojo.services.entity.Player;
import com.codenjoy.dojo.services.entity.ServerLocation;
import com.codenjoy.dojo.services.jdbc.ConnectionThreadPoolFactory;
import com.codenjoy.dojo.services.jdbc.CrudConnectionThreadPool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Players {

    private CrudConnectionThreadPool pool;

    public Players(ConnectionThreadPoolFactory factory) {
        pool = factory.create(
                "CREATE TABLE IF NOT EXISTS players (" +
                        "email varchar(255), " +
                        "phone varchar(255), " +
                        "first_name varchar(255), " +
                        "last_name varchar(255), " +
                        "password varchar(255)," +
                        "city varchar(255)," +
                        "skills varchar(255)," +
                        "comment varchar(255)," +
                        "code varchar(255)," +
                        "server varchar(255)," +
                        "approved int," +
                        "verification_code varchar(255)," +
                        "verification_type varchar(255));");
    }

    void removeDatabase() {
        pool.removeDatabase();
    }

    public List<Player> getPlayersDetails() {
        return selectPlayers("SELECT * FROM players;");
    }

    private List<Player> selectPlayers(String query) {
        return pool.select(query,
                rs -> {
                    List<Player> result = new LinkedList<>();
                    while (rs.next()) {
                        result.add(getPlayer(rs));
                    }
                    return result;
                }
        );
    }

    private Player getPlayer(ResultSet rs) throws SQLException {
        return new Player(
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("city"),
                rs.getString("skills"),
                rs.getString("comment"),
                rs.getString("code"),
                rs.getString("server"),
                rs.getInt("approved"),
                rs.getString("verification_code"),
                rs.getString("verification_type")
        );
    }

    public List<Player> getPlayers(List<String> emails) {
        return selectPlayers(String.format(
                "SELECT * FROM players WHERE email IN ('%s');",
                String.join("','", emails)
        ));
    }

    public List<ServerLocation> getPlayersLocations() {
        return pool.select("SELECT * FROM players;",
                rs -> {
                    List<ServerLocation> result = new LinkedList<>();
                    while (rs.next()) {
                        result.add(
                            new ServerLocation(
                                rs.getString("email"),
                                rs.getString("phone"),
                                null, // TODO установить это поле в сервисе
                                rs.getString("code"),
                                rs.getString("server")));
                    }
                    return result;
                }
        );
    }

    public Player get(String email) {
        return pool.select("SELECT * FROM players WHERE email = ?;",
                new Object[]{email},
                rs -> rs.next() ? getPlayer(rs) : null
        );
    }

    public Optional<Player> getByPhone(String phone) {
        return pool.select("SELECT * FROM players WHERE phone = ?;",
                new Object[]{phone},
                rs -> rs.next() ? Optional.of(getPlayer(rs)) : Optional.empty()
        );
    }

    public String getCode(String email) {
        return pool.select("SELECT code FROM players WHERE email = ?;",
                new Object[]{email},
                rs -> rs.next() ? rs.getString("code") : null
        );
    }


    public String getServerByEmail(String email) {
        return pool.select("SELECT server FROM players WHERE email = ?;",
                new Object[]{email},
                rs -> rs.next() ? rs.getString("server") : null
        );
    }

    public String getServerByPhone(String phone) {
        return pool.select("SELECT server FROM players WHERE phone = ?;",
                new Object[]{phone},
                rs -> rs.next() ? rs.getString("server") : null
        );
    }


    public void create(Player player) {
        pool.update("INSERT INTO players (first_name, last_name, password, " +
                        "city, skills, comment, code, server, phone, email, approved, verification_code, verification_type) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);",
                getObjects(player));
    }

    public void update(Player player) {
        pool.update("UPDATE players SET first_name = ?, last_name = ?, " +
                        "password = ?, city = ?, skills = ?, comment = ?, " +
                        "code = ?, server = ?, phone = ? WHERE email = ?;",
                getObjectsForUpdate(player));
    }

    public void updateServer(String email, String server, String code) {
        pool.update("UPDATE players SET server = ?, code = ? WHERE email = ?;",
                server, code, email);
    }

    public void approveByPhone(String phone) {
        pool.update("UPDATE players SET approved = ? WHERE phone = ?;",
                Player.APPROVED, phone);
    }

    public void updateVerificationCode(String phone, String verificationCode, String verificationType) {
        pool.update("UPDATE players SET verification_code = ?, verification_type = ? WHERE phone = ?;",
                verificationCode, verificationType, phone);
    }

    private Object[] getObjects(Player player) {
        return new Object[]{
                player.getFirstName(),
                player.getLastName(),
                player.getPassword(),
                player.getCity(),
                player.getSkills(),
                player.getComment(),
                player.getCode(),
                player.getServer(),
                player.getPhone(),
                player.getEmail(),
                player.getApproved(),
                player.getVerificationCode(),
                player.getVerificationType(),
        };
    }

    private Object[] getObjectsForUpdate(Player player) {
        return new Object[]{
                player.getFirstName(),
                player.getLastName(),
                player.getPassword(),
                player.getCity(),
                player.getSkills(),
                player.getComment(),
                player.getCode(),
                player.getServer(),
                player.getPhone(),
                player.getEmail(),
        };
    }

    public void remove(String email) {
        pool.update("DELETE FROM players WHERE email = ?;",
                new Object[]{email});
    }

    public void removeAll() {
        pool.update("DELETE FROM players;");
    }
}
