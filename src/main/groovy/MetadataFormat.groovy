/**
 * Holds names of columns used in output metadata file.
 */
class MetadataFormat
{
    // Mandatory items
    static final String COURT_NAME = 'court_name';
    static final String REGISTRY_MARK = 'registry_mark';
    static final String DECISION_DATE = 'decision_date';
    static final String WEB_PATH = 'web_path';
    static final String LOCAL_PATH = 'local_path';

    // Extra items relevant for supreme court documents
    static final String ECLI = 'ecli';
    static final String DECISION_TYPE = 'decision_type';
}
