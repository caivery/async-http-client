/*
 * Copyright (c) 2014 AsyncHttpClient Project. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.asynchttpclient.cookie;

import org.asynchttpclient.util.AsyncHttpProviderUtils;

class CookieBuilder {

    private String name;
    private String value;
    private String rawValue;
    private String domain;
    private String path;
    private int maxAge = -1;
    private boolean secure;
    private boolean httpOnly;

    public void addKeyValuePair(String header, int nameStart, int nameEnd, String value, String rawValue) {

        if (name == null) {
            name = header.substring(nameStart, nameEnd);
            this.value = value;
            this.rawValue = rawValue;

        } else {
            setCookieAttribute(header, nameStart, nameEnd, value);
        }
    }

    public Cookie build() {
        return name != null ? new Cookie(domain, name, value, rawValue, path, maxAge, secure, httpOnly) : null;
    }

    private boolean isPath(char c0, char c1, char c2, char c3) {
        return (c0 == 'P' || c0 == 'p') && //
                (c1 == 'a' || c1 == 'A') && //
                (c2 == 't' || c2 == 'T') && //
                (c3 == 'h' || c3 == 'H');
    }

    private void parse4(String header, int nameStart, int length, String value) {

        char c0 = header.charAt(nameStart);
        char c1 = header.charAt(nameStart + 1);
        char c2 = header.charAt(nameStart + 2);
        char c3 = header.charAt(nameStart + 3);

        if (isPath(c0, c1, c2, c3)) {
            path = value;
        }
    }

    private boolean isDomain(char c0, char c1, char c2, char c3, char c4, char c5) {
        return (c0 == 'D' || c0 == 'd') && //
                (c1 == 'o' || c1 == 'O') && //
                (c2 == 'm' || c2 == 'M') && //
                (c3 == 'a' || c3 == 'A') && //
                (c4 == 'i' || c4 == 'I') && //
                (c5 == 'n' || c5 == 'N');
    }

    private boolean isSecure(char c0, char c1, char c2, char c3, char c4, char c5) {
        return (c0 == 'S' || c0 == 's') && //
                (c1 == 'e' || c1 == 'E') && //
                (c2 == 'c' || c2 == 'C') && //
                (c3 == 'u' || c3 == 'U') && //
                (c4 == 'r' || c4 == 'R') && //
                (c5 == 'e' || c5 == 'E');
    }

    private void parse6(String header, int nameStart, int length, String value) {

        char c0 = header.charAt(nameStart);
        char c1 = header.charAt(nameStart + 1);
        char c2 = header.charAt(nameStart + 2);
        char c3 = header.charAt(nameStart + 3);
        char c4 = header.charAt(nameStart + 4);
        char c5 = header.charAt(nameStart + 5);

        if (isDomain(c0, c1, c2, c3, c4, c5)) {
            domain = value;
        } else if (isSecure(c0, c1, c2, c3, c4, c5)) {
            secure = true;
        }
    }

    private boolean isExpires(char c0, char c1, char c2, char c3, char c4, char c5, char c6) {
        return (c0 == 'E' || c0 == 'e') && //
                (c1 == 'x' || c1 == 'X') && //
                (c2 == 'p' || c2 == 'P') && //
                (c3 == 'i' || c3 == 'I') && //
                (c4 == 'r' || c4 == 'R') && //
                (c5 == 'e' || c5 == 'E') && //
                (c6 == 's' || c6 == 'S');
    }

    private boolean isMaxAge(char c0, char c1, char c2, char c3, char c4, char c5, char c6) {
        return (c0 == 'M' || c0 == 'm') && //
                (c1 == 'a' || c1 == 'A') && //
                (c2 == 'x' || c2 == 'X') && //
                (c3 == '-') && //
                (c4 == 'A' || c4 == 'a') && //
                (c5 == 'g' || c5 == 'G') && //
                (c6 == 'e' || c6 == 'E');
    }

    private void setExpire(String value) {
        Integer expireAsMaxAge = AsyncHttpProviderUtils.convertExpireField(value);
        if (expireAsMaxAge != null) {
            // ignore failure to parse -> treat as session cookie
            maxAge = expireAsMaxAge.intValue();
        }
    }

    private void setMaxAge(String value) {
        try {
            maxAge = Math.max(Integer.valueOf(value), 0);
        } catch (NumberFormatException e1) {
            // ignore failure to parse -> treat as session cookie
        }
    }

    private void parse7(String header, int nameStart, int length, String value) {

        char c0 = header.charAt(nameStart);
        char c1 = header.charAt(nameStart + 1);
        char c2 = header.charAt(nameStart + 2);
        char c3 = header.charAt(nameStart + 3);
        char c4 = header.charAt(nameStart + 4);
        char c5 = header.charAt(nameStart + 5);
        char c6 = header.charAt(nameStart + 6);

        if (isExpires(c0, c1, c2, c3, c4, c5, c6)) {
            setExpire(value);

        } else if (isMaxAge(c0, c1, c2, c3, c4, c5, c6)) {
            setMaxAge(value);
        }
    }

    private boolean isHttpOnly(char c0, char c1, char c2, char c3, char c4, char c5, char c6, char c7) {
        return (c0 == 'H' || c0 == 'h') && //
                (c1 == 't' || c1 == 'T') && //
                (c2 == 't' || c2 == 'T') && //
                (c3 == 'p' || c3 == 'P') && //
                (c4 == 'O' || c4 == 'o') && //
                (c5 == 'n' || c5 == 'N') && //
                (c6 == 'l' || c6 == 'L') && //
                (c7 == 'y' || c7 == 'Y');
    }

    private void parse8(String header, int nameStart, int length, String value) {

        char c0 = header.charAt(nameStart);
        char c1 = header.charAt(nameStart + 1);
        char c2 = header.charAt(nameStart + 2);
        char c3 = header.charAt(nameStart + 3);
        char c4 = header.charAt(nameStart + 4);
        char c5 = header.charAt(nameStart + 5);
        char c6 = header.charAt(nameStart + 6);
        char c7 = header.charAt(nameStart + 7);

        if (isHttpOnly(c0, c1, c2, c3, c4, c5, c6, c7)) {
            httpOnly = true;
        }
    }

    private void setCookieAttribute(String header, int nameStart, int nameEnd, String value) {

        int length = nameEnd - nameStart;

        switch (length) {
        case 4:
            parse4(header, nameStart, length, value);
            break;

        case 6:
            parse6(header, nameStart, length, value);
            break;

        case 7:
            parse7(header, nameStart, length, value);
            break;

        case 8:
            parse8(header, nameStart, length, value);
            break;

        default:
        }
    }
}
