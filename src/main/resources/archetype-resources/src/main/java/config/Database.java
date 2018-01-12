#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * SQL2o Database singleton
 */
@Singleton
public class Database {

    private final Sql2o sql2o;
    private final TableVersionRepository tableVersionRepository;

    private static final Logger log = LoggerFactory.getLogger(Database.class);


    @Inject
    public Database(Properties properties) {
        //ADD custom converters
        String url = properties.getString("database.url");
        String user = properties.getString("database.user");
        String pass = properties.getString("database.password");
        sql2o = new Sql2o(url, user, pass);
        tableVersionRepository = new TableVersionRepository(this);
    }

    /**
     * Get Read/write system table version
     *
     * @return TableVersionRepository
     */
    public TableVersionRepository tablesVersion() {
        return tableVersionRepository;
    }

    /**
     * Return Database connection
     *
     * @return Sql2o Connection
     */
    public Connection getConnection() {
        return sql2o.open();
    }

    /**
     * Return Database transaction
     *
     * @return Sql2o Connection
     */

    public Connection getTransaction() {
        return sql2o.beginTransaction();
    }


    /**
     * Inner repository System Table Version
     */
    public class TableVersionRepository {

        private final static String TABLE_NAME = "sys_sql2o_tables";
        private final static String CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "tablename TEXT, " +
                "version INTEGER, " +
                "PRIMARY KEY(tablename))";
        private final static String GET_VERSION = "SELECT version FROM " + TABLE_NAME + " where tablename LIKE :tablename";
        private final static String SET_VERSION = "INSERT INTO " + TABLE_NAME + "(tablename, version) VALUES (:tablename,:version) ON CONFLICT(tablename) DO UPDATE SET version= :version ";

        private final Logger log = LoggerFactory.getLogger(TableVersionRepository.class);

        private final Database database;

        TableVersionRepository(Database database) {
            this.database = database;
            verifyTable();
        }

        private void verifyTable() {
            try (Connection con = database.getConnection()) {
                con.createQuery(CREATE).executeUpdate();
            } catch (Exception e) {
                log.error("Error CREATE TABLE " + TABLE_NAME + " " + e.getMessage());
            }
        }


        /**
         * Get Current version of table in database
         *
         * @param tableName table name
         * @return version -1 if doesn't exist
         */
        public int getCurrentTableVersion(String tableName) {
            int result = -1;
            try (Connection con = database.getConnection()) {
                result = con.createQuery(GET_VERSION)
                        .addParameter("tablename", tableName)
                        .executeScalar(Integer.class);
            } catch (Exception e) {
                log.error("Error getCurrentTableVersion :" + e.getMessage());
            }
            return result;
        }

        /**
         * Set Current version of table in database
         *
         * @param con       current transaction
         * @param tableName table name
         * @param version   version
         */
        public void setTableVersion(Connection con, String tableName, int version) {
            try {
                con.createQuery(SET_VERSION)
                        .addParameter("tablename", tableName)
                        .addParameter("version", version)
                        .executeUpdate();
            } catch (Exception e) {
                log.error("Error setCurrentTableVersion :" + e.getMessage());
            }
        }
    }
}
