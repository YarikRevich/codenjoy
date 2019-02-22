package com.codenjoy.dojo.services.hash;

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


import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class Hash {

    public static String md5(String string) {
        return DigestUtils.md5Hex(string.getBytes());
    }

    public static String getId(String email, String soul) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }

        String encoded = xor(email, soul);
        return ZBase32Encoder.encode(encoded.getBytes());
    }

    public static String getEmail(String id, String soul) {
        if (StringUtils.isEmpty(id)) {
            return id;
        }

        String data = new String(ZBase32Encoder.decode(id));
        return xor(data, soul);
    }

    private static String xor(String email, String soul) {
        String hash = md5(soul);

        AtomicInteger index = new AtomicInteger();
        return email.chars()
                .map(ch -> {
                    int i = index.getAndIncrement();
                    if (i >= hash.length()) {
                        i = i % hash.length();
                    }
                    return ch ^ hash.codePointAt(i);
                })
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static String getCode(String emailOrId, String password) {
        return String.valueOf(Math.abs(
                md5(md5(emailOrId) + md5(password)).hashCode()
                + 10000000000000L * md5(md5(password) + md5(emailOrId) + emailOrId).hashCode()
                ^ md5(emailOrId + password).hashCode() << 13 ^ 345378L * md5(password + md5(emailOrId)).hashCode()
        ));
    }

    public static void main(String[] args) {
        String soul = "soul";

        String email = "apofig@gmail.com";
        String password = "apofig@gmail.com";
        String passwordHash = md5(password);
        String id = getId(email, soul);
        String code = getCode(email, passwordHash);

        System.out.println("email: " + email);
        System.out.println("id: " + id);
        System.out.println("password: " + password);
        System.out.println("password md5: " + passwordHash);
        System.out.println("code: " + code);

        System.out.println("---");
        System.out.printf("UPDATE players " +
                "SET password = '%s', code = '%s' " +
                "WHERE email = '%s';%n", passwordHash, code, email);
    }

}
