/**
 * Holds names of columns used in output metadata file.
 */
class MetadataFormat
{
    // Mandatory items
    public static final COURT_NAME = 'court_name';
    public static final REGISTRY_MARK = 'registry_mark';
    public static final DECISION_DATE = 'decision_date';
    public static final WEB_PATH = 'web_path';
    public static final LOCAL_PATH = 'local_path';

    // Extra items relevant for supreme court documents
    public static final ECLI = 'ecli';
    public static final DECISION_TYPE = 'decision_type';
}
