environments {
    production {
        grails.paypal.server = "https://www.paypal.com/cgi-bin/webscr"
        grails.paypal.email = "example@business.com"
        grails.serverURL = "http://www.grails.org"
    }
    development {
        grails.paypal.server = "https://www.sandbox.paypal.com/cgi-bin/webscr"
        grails.paypal.email = "seller_1237686842_biz@mattstine.com"
        grails.serverURL = "http://localhost:8080"
    }
}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"

dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'com.opensymphony.oscache.hibernate.OSCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
}