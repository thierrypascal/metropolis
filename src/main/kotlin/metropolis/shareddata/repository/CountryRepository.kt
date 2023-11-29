package metropolis.shareddata.repository

import metropolis.shareddata.data.Country
import metropolis.shareddata.repository.CountryRepository.*
import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.repository.CrudRepository
import metropolis.xtracted.repository.asSql

enum class CountryRepository : DbColumn {
    ISO_ALPHA2,
    ISO_ALPHA3,
    ISO_NUMERIC,
    FIPS_CODE,
    NAME,
    CAPITAL,
    AREA_IN_SQKM,
    POPULATION,
    CONTINENT,
    TLD,
    CURRENCY_CODE,
    CURRENCY_NAME,
    PHONE,
    POSTAL_CODE_FORMAT,
    POSTAL_CODE_REGEX,
    LANGUAGES,
    GEONAME_ID,
    NEIGHBOURS,
    EQUIVALENT_FIPS_CODE,
}

fun countryRepository(url: String) =
    CrudRepository(
        url = url,
        table = "COUNTRY",
        idColumn = ISO_NUMERIC,
        dataColumns = mapOf(
            NAME            to { it.name?.asSql() },
            CAPITAL         to { if(it.capital.isNullOrBlank()) "" else it.capital.asSql() },
            ISO_ALPHA2      to { it.isoAlpha2?.asSql() },
            AREA_IN_SQKM    to { it.area_sqm?.toString() },
            POPULATION      to { it.population?.toString() },
            CONTINENT       to { it.continent?.asSql() },
            TLD             to { it.tld?.asSql() },
            CURRENCY_CODE   to { it.currencyCode?.asSql() },
            CURRENCY_NAME   to { it.currencyName?.asSql() },
            PHONE           to { it.phone?.asSql() },
            POSTAL_CODE_FORMAT to { it.postal_code_format?.asSql() },
            LANGUAGES       to { it.languages?.asSql() },
            NEIGHBOURS      to { it.neighbours?.asSql() },
        ),
        mapper = {
            Country(
                id              = getInt(ISO_NUMERIC.name),
                name            = getString(NAME.name),
                isoAlpha2       = getString(ISO_ALPHA2.name),
                capital         = getString(CAPITAL.name),
                area_sqm        = getDouble(AREA_IN_SQKM.name),
                population      = getDouble(POPULATION.name),
                continent       = getString(CONTINENT.name),
                tld             = getString(TLD.name),
                currencyCode    = getString(CURRENCY_CODE.name),
                currencyName    = getString(CURRENCY_NAME.name),
                phone           = getString(PHONE.name),
                postal_code_format = getString(POSTAL_CODE_FORMAT.name),
                languages       = getString(LANGUAGES.name),
                neighbours      = getString(NEIGHBOURS.name),
            )
        }
    )

/*
CREATE TABLE COUNTRY (
                         ISO_ALPHA2           CHAR(2)          NOT NULL,
                         ISO_ALPHA3           CHAR(3)          NOT NULL,
                         ISO_NUMERIC          INTEGER          PRIMARY KEY AUTOINCREMENT,
                         FIPS_CODE            VARCHAR(3),
                         NAME                 VARCHAR(200)     NOT NULL,
                         CAPITAL              VARCHAR(200),
                         AREA_IN_SQKM         DOUBLE PRECISION NOT NULL,
                         POPULATION           INTEGER          NOT NULL,
                         CONTINENT            CHAR(2)          NOT NULL,
                         TLD                  CHAR(3),
                         CURRENCY_CODE        CHAR(3),
                         CURRENCY_NAME        VARCHAR(20),
                         PHONE                VARCHAR(10),
                         POSTAL_CODE_FORMAT   VARCHAR(10),
                         POSTAL_CODE_REGEX    VARCHAR(30),
                         LANGUAGES            VARCHAR(30),
                         GEONAME_ID           BIGINT           NOT NULL,
                         NEIGHBOURS           VARCHAR(30),
                         EQUIVALENT_FIPS_CODE VARCHAR(3)
);

 */