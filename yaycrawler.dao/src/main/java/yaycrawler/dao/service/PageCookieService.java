package yaycrawler.dao.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.PhantomCookie;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.repositories.SiteCookieRepository;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/6/28.
 */
@Service
public class PageCookieService {

    @Autowired
    private SiteCookieRepository cookieRepository;
    public static final String DEMO_CACHE_NAME = "demo";
    @CacheEvict(value = DEMO_CACHE_NAME)
    public void deleteCookieById(String cookieId) {
        try {
            if (StringUtils.isBlank(cookieId)) return;
            cookieRepository.delete(cookieId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public void deleteCookieBySiteId(String siteId) {
        try {
            if (StringUtils.isBlank(siteId)) return;
            cookieRepository.deleteBySiteId(siteId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean saveCookies(String domain,String siteId, List<PhantomCookie> cookies) {
        if (cookies == null || cookies.size() == 0) return false;
        try {
            StringBuilder cookieBuild = new StringBuilder();
            cookies.forEach(
                    phantomCookie -> {
                        if(phantomCookie.getName().equalsIgnoreCase("Cookie"))
                            cookieBuild.append(String.format("%s;",phantomCookie.getValue()));
                        else
                            cookieBuild.append(String.format("%s=%s;",phantomCookie.getName(),phantomCookie.getValue()));
                    });
            return cookieRepository.save(new SiteCookie(siteId,domain,cookieBuild.toString())) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    @CacheEvict(value = DEMO_CACHE_NAME)
    public boolean saveCookies(String domain,List<PhantomCookie> cookies) {
        if (cookies == null || cookies.size() == 0) return false;
        try {
            StringBuilder cookieBuild = new StringBuilder();
            cookies.forEach(phantomCookie -> cookieBuild.append(String.format("%s=%s;", phantomCookie.getName(), phantomCookie.getValue())));
            return cookieRepository.save(new SiteCookie(domain, cookieBuild.toString())) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    @Cacheable(value = DEMO_CACHE_NAME, keyGenerator = "wiselyKeyGenerator")
    public SiteCookie getCookieByUrl(String url) {
        if (StringUtils.isBlank(url)) return null;
        return cookieRepository.findOneByDomain(UrlUtils.getDomain(url));
    }

    @Cacheable(value = DEMO_CACHE_NAME, keyGenerator = "wiselyKeyGenerator")
    public String getCookieValueById(String cookieId) {
        SiteCookie siteCookie = cookieRepository.findOne(cookieId);
        return siteCookie == null ? "" : siteCookie.getCookie();
    }
}
