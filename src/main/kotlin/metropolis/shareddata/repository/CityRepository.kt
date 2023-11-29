package metropolis.shareddata.repository

import metropolis.shareddata.data.City
import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.repository.LazyRepository
import metropolis.shareddata.repository.CityRepository.*
import metropolis.xtracted.repository.CrudRepository
import metropolis.xtracted.repository.asSql

enum class CityRepository : DbColumn {
    ID,
    NAME,
    ASCII_NAME,
    ALTERNATE_NAMES,
    LATITUDE,
    LONGITUDE,
    FEATURE_CLASS,
    FEATURE_CODE,
    COUNTRY_CODE,
    CC2,
    ADMIN1_CODE,
    ADMIN2_CODE,
    ADMIN3_CODE,
    ADMIN4_CODE,
    POPULATION,
    ELEVATION,
    DEM,
    TIMEZONE,
    MODIFICATION_DATE,
}

fun cityRepository(url: String) =
    CrudRepository(
        url = url,
        table = "CITY",
        idColumn = ID,
        dataColumns = mapOf(
            NAME                to { it.name?.asSql() },
            COUNTRY_CODE        to { if(it.country_code.isNullOrBlank()) "" else it.country_code.asSql() },
            LATITUDE            to { it.latitude?.toString() },
            LONGITUDE           to { it.longitude?.toString() },
            FEATURE_CLASS       to { it.feature_class?.asSql() },
            FEATURE_CODE        to { if(it.feature_code.isNullOrBlank()) "" else it.feature_code.asSql() },
            POPULATION          to { it.population?.toString() },
            ELEVATION           to { it.elevation?.toString() },
            DEM                 to { it.dem?.toString() },
            TIMEZONE            to { if(it.timezone.isNullOrBlank())     "" else it.timezone.asSql() },
        ),
        mapper = {
            City(
                id              = getInt(ID.name),
                name            = getString(NAME.name),
                country_code    = getString(COUNTRY_CODE.name),
                latitude        = getDouble(LATITUDE.name),
                longitude       = getDouble(LONGITUDE.name),
                feature_class   = getString(FEATURE_CLASS.name),
                feature_code    = getString(FEATURE_CODE.name),
                population      = getDouble(POPULATION.name),
                elevation       = getDouble(ELEVATION.name),
                dem             = getDouble(DEM.name),
                timezone        = getString(TIMEZONE.name),
            )
        }
    )

/*
create table CITY
(
    ID                INTEGER          primary key  AUTOINCREMENT,
    NAME              VARCHAR(200)     NOT NULL,
    ASCII_NAME        VARCHAR(200),
    ALTERNATE_NAMES   VARCHAR(10000),
    LATITUDE          DOUBLE           NOT NULL,
    LONGITUDE         DOUBLE           NOT NULL,
    FEATURE_CLASS     CHAR             NOT NULL,
    FEATURE_CODE      VARCHAR(10)      NOT NULL,
    COUNTRY_CODE      VARCHAR(2)       NOT NULL,
    CC2               CHAR(200),
    ADMIN1_CODE       VARCHAR(20),
    ADMIN2_CODE       VARCHAR(80),
    ADMIN3_CODE       VARCHAR(20),
    ADMIN4_CODE       VARCHAR(20),
    POPULATION        INTEGER          NOT NULL,
    ELEVATION         INTEGER,
    DEM               INTEGER          NOT NULL,
    TIMEZONE          VARCHAR(40)      NOT NULL,
    MODIFICATION_DATE DATE             NOT NULL
);

create unique index CITY_ID
    on CITY (ID);

create index CITY_COUNTRY_CODE
    on CITY (COUNTRY_CODE);

create index CITY_NAME
    on CITY (NAME);

create index CITY_TIMEZONE
    on CITY (TIMEZONE);

 */