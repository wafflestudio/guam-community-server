package waffle.guam.community.data.jdbc

import org.hibernate.dialect.MySQL8Dialect
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.type.StandardBasicTypes

class CustomMySqlDialect() : MySQL8Dialect() {
    init {
        registerFunction(
            "match",
            SQLFunctionTemplate(
                StandardBasicTypes.DOUBLE,
                "match(?1, ?2) against (?3 in boolean mode)"
            )
        )
    }
}
