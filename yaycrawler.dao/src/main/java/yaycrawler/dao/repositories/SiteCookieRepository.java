package yaycrawler.dao.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.SiteCookie;

import javax.transaction.Transactional;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@Repository
public interface SiteCookieRepository extends CrudRepository<SiteCookie, String> {

    @Query(value = "select *  from res_site_cookie where domain=?  limit 1", nativeQuery = true)
    SiteCookie findOneByDomain(String domain);

    @Modifying
    @Transactional
    @Query(value = "delete from SiteCookie sc where sc.siteId = ?1")
    Integer deleteBySiteId(String siteId);
}
