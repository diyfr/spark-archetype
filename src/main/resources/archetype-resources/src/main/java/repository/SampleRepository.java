#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.repository;

import ${groupId}.${artifactId}.config.Database;
import ${groupId}.${artifactId}.domain.Sample;
import ${groupId}.${artifactId}.helper.SQL2oColumnMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SampleRepository {

    private final static String TABLE_NAME = "sample";
    private final static int TABLE_VERSION = 1;
    private final Database database;


    private static final String CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            "tech_id  BIGSERIAL NOT NULL, " +
            "title TEXT, " +
            "sub_title TEXT," +
            "version INT8," +
            "PRIMARY KEY(tech_id))";

    private static final String INSERT = "INSERT INTO " + TABLE_NAME + "(title,sub_title,version) VALUES " +
            "(:title,:sub_title, :version)";
    private static final String GET_BY_TECH_ID = "SELECT * FROM " + TABLE_NAME + " where tech_id=:tech_id";

    private static final Logger log = LoggerFactory.getLogger(SampleRepository.class);

    @SuppressWarnings("unused")
    @Inject
    public SampleRepository(Database database) {
        this.database = database;
        checkAndUpgrade();
    }

    private void checkAndUpgrade() {
        String sql;
        switch (database.tablesVersion().getCurrentTableVersion(TABLE_NAME)) {
	    //Add your SQL Update here
            case 1:
            default:
                sql = CREATE;
        }
        try (Connection con = database.getTransaction()) {
            con.createQuery(sql).executeUpdate();
            database.tablesVersion().setTableVersion(con, TABLE_NAME, TABLE_VERSION);
            con.commit();
        } catch (Exception e) {
            log.error("Error CREATE/ALTER TABLE " + TABLE_NAME + " :" + e.getMessage());
        }
    }


    
    public Sample create(Sample sample) {
        try (Connection con = database.getConnection()) {
            long techId = (Long) con.createQuery(INSERT, true)
                    .addParameter("title", sample.getTitle())
                    .addParameter("sub_title", sample.getSubTitle())
                    .addParameter("version", sample.getVersion())
                    .executeUpdate().getKey();
            sample.setTechId(techId);
        } catch (Exception e) {
            log.error("Error insert Sample" + e.getMessage());
            sample = null;
        }
        return sample;
    }

    public Sample getByTechId(long techId) {
        Sample result = null;
        try (Connection con = database.getConnection()) {
            result = con.createQuery(GET_BY_TECH_ID)
                    .addParameter("tech_id", techId)
                    .setColumnMappings(SQL2oColumnMapping.getMapping(Sample.class))
                    .executeAndFetchFirst(Sample.class);
        } catch (Exception e) {
            log.error("Error getByTechId Sample" + e.getMessage());
        }
        return result;
    }
}
