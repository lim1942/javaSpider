package com.lim.download;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.DefaultCookieSpec;

/**
 * @program: carrier
 * @description: To allow all cookies
 * @author: zhuke
 * @created: 2019-01-29 16:59
 **/
public class EasyCookieSpec extends DefaultCookieSpec {
    @Override
    public void validate(Cookie arg0, CookieOrigin arg1) throws MalformedCookieException {
        //allow all cookies
    }
}

