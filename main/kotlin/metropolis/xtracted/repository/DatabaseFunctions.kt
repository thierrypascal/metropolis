package metropolis.xtracted.repository

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.time.LocalDate
import java.util.logging.Logger
import kotlin.time.Duration
import metropolis.xtracted.data.DbColumn
import metropolis.xtracted.data.Filter
import metropolis.xtracted.data.SortDirective
import metropolis.xtracted.data.asSql

fun <T> readFirst(url: String, table: String, columns: String = "*", where: String , map: ResultSet.() -> T?) =
    DriverManager.getConnection(url)
        .use {
            val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

            val sql = "SELECT $columns FROM $table WHERE $where"

            val start = System.currentTimeMillis()

            val resultSet = it.createStatement()
                .executeQuery(sql)

            logger.info("$sql \n took ${System.currentTimeMillis() - start} msec")
            if (resultSet.next()) {
                resultSet.map()
            } else {
                null
            }
        }

fun readIds(url: String, table: String, idColumn: DbColumn, filters: List<Filter<*>>, sortDirective: SortDirective): List<Int> =
    DriverManager.getConnection(url)
        .use {
            val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

            val orderBy = if(null == sortDirective.column) "" else "ORDER BY  ${sortDirective.column.name}  ${sortDirective.direction}"

            val sql = "SELECT ${idColumn.name} FROM $table ${filters.asSql()} $orderBy"

            logger.info(sql)

            val resultSet = it.createStatement()
                .executeQuery(sql)

            buildList {
                while (resultSet.next()) {
                    add(resultSet.getInt(1))
                }
            }
        }

fun <T> readAll(url: String, table: String, columns: String = "*", where: String = "", orderBy : String = "ID", map: ResultSet.() -> T) =
    DriverManager.getConnection(url)
        .use {
            val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

            val sql = "SELECT $columns FROM $table ${if (where.isNotEmpty()) "WHERE $where" else ""} ORDER BY $orderBy ASC"

            logger.info(sql)

            val resultSet = it.createStatement()
                .executeQuery(sql)

            buildList {
                while (resultSet.next()) {
                    add(resultSet.map())
                }
            }
        }

fun update(url: String, table: String, id: Int, setStatement: String, idName: String = "ID") =
    DriverManager.getConnection(url)
        .use {
            val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

            val sql = "UPDATE $table $setStatement WHERE $idName = $id"

            logger.info(sql)

            try {
                it.createStatement()
                    .executeUpdate(sql)
            } catch (e: SQLException) {
                logger.severe { e.localizedMessage }
            }
        }

fun count(url: String, table: String, idName: String = "ID"): Int =
    DriverManager.getConnection(url)
        .use {
            val sql = "SELECT COUNT($idName) FROM $table"

            val resultSet = it.createStatement()
                .executeQuery(sql)

            resultSet.next()

            resultSet.getInt(1)
        }

fun count(url : String, table: String, filters: List<Filter<*>>, idName: String = "ID") =
    DriverManager.getConnection(url)
        .use {
            val sql = "SELECT COUNT($idName) FROM $table ${filters.asSql()}"

            val resultSet = it.createStatement()
                .executeQuery(sql)

            resultSet.next()

            resultSet.getInt(1)
        }

fun insertAndCreateKey(url: String, insertStmt: String): Int =
    DriverManager.getConnection(url)
        .use {
            val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)

            logger.info(insertStmt)

            val stmt = it.prepareStatement(insertStmt, Statement.RETURN_GENERATED_KEYS)

            stmt.executeUpdate()

            val keys = stmt.generatedKeys
            keys.next()

            return keys.getInt(1)
        }

fun delete(url: String, table: String, id: Int): Unit =
    DriverManager.getConnection(url)
        .use {
            val sql: String = if (table == "CITY") {
                "DELETE FROM $table WHERE ID = $id"
            }else{
                "DELETE FROM $table WHERE ISO_NUMERIC = $id"
            }

            it.createStatement()
                .execute(sql)
        }

fun deleteAll(url: String, table: String): Unit =
    DriverManager.getConnection(url)
        .use {
            val sql = "DELETE FROM $table"

            it.createStatement()
                .execute(sql)
        }


fun String.urlFromResources(): String =
    "jdbc:sqlite:${
        object {}.javaClass.getResource(this)!!
            .toExternalForm()
    }"

fun String.urlFromWorkingDirectory(): String =
    "jdbc:sqlite:./src/main/resources$this"

fun String.asSql() = "'${this.replace("'", "''")}'"
fun LocalDate.asSql() = "'$this'"
fun Duration.asSql() = "'$this'"
