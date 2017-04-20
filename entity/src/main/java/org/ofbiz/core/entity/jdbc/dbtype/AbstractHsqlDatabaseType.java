package org.ofbiz.core.entity.jdbc.dbtype;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;

abstract class AbstractHsqlDatabaseType extends AbstractDatabaseType {

    private static final int MAJOR = 0;
    private static final int MINOR = 1;
    private static final int MICRO = 2;

    AbstractHsqlDatabaseType(String name) {
        super(name, "hsql", new String[]{"HSQL Database Engine"});
    }

    private int[] parseVersionStr(String version) {
        int[] versionNumber = new int[3];

        StringTokenizer versionTokens = new StringTokenizer(version, ".");

        if (versionTokens.hasMoreElements()) {
            versionNumber[MAJOR] = Integer.parseInt(versionTokens.nextToken());


            if (versionTokens.hasMoreElements()) {
                versionNumber[MINOR] = Integer.parseInt(versionTokens.nextToken());

                if (versionTokens.hasMoreElements()) {
                    versionNumber[MICRO] = Integer.parseInt(versionTokens.nextToken());
                } else {
                    versionNumber[MICRO] = 0;
                }

                return versionNumber;
            }
        }

        return null;
    }

    private int[] getHsqlVersion(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        return parseVersionStr(metaData.getDatabaseProductVersion());
    }

    protected boolean hsqlVersionGreaterThanOrEqual(Connection con, int major, int minor, int micro) throws SQLException {
        int[] vers = getHsqlVersion(con);

        return versionGreaterThanOrEqual(vers[MAJOR], vers[MINOR], major, minor) && vers[MICRO] >= micro;
    }

    protected boolean hsqlVersionLessThanOrEqual(Connection con, int major, int minor, int micro) throws SQLException {
        int[] vers = getHsqlVersion(con);

        return versionGreaterThanOrEqual(major, minor, vers[MAJOR], vers[MINOR]) && vers[MICRO] <= micro;
    }

    @Override
    protected String getChangeColumnTypeStructure() {
        return CHANGE_COLUMN_TYPE_CLAUSE_STRUCTURE_STANDARD_MODIFY;
    }

    @Override
    public String getDropIndexStructure() {
        return DROP_INDEX_SCHEMA_DOT_INDEX;
    }
}
