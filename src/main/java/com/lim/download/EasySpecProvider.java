package com.lim.download;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

/**
 * @program: carrier
 * @description: Cookie Provider
 * @author: zhuke
 * @created: 2019-01-29 17:00
 **/
class EasySpecProvider implements CookieSpecProvider {
    @Override
    public CookieSpec create(HttpContext context) {
        return new EasyCookieSpec();
    }
}