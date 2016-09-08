import au.com.bytecode.opencsv.CSVWriter

/**
 * Maps and writes extracted information into metadata according to its format.
 */
class MetadataWriter
{
    def columns = [
            MetadataFormat.COURT_NAME,
            MetadataFormat.RECORD_ID,
            MetadataFormat.REGISTRY_MARK,
            MetadataFormat.DECISION_DATE,
            MetadataFormat.WEB_PATH,
            MetadataFormat.LOCAL_PATH,
            MetadataFormat.ECLI,
            MetadataFormat.DECISION_TYPE
    ]
    def mapping = [
            SupremeCourt.COURT,
            SupremeCourt.ECLI,
            SupremeCourt.REGISTRY_MARK,
            SupremeCourt.DECISION_DATE,
            DocumentProcessor.WEB_PATH,
            DocumentProcessor.LOCAL_PATH,
            SupremeCourt.ECLI,
            SupremeCourt.DECISION_TYPE
    ]
    String directory

    MetadataWriter(String directory)
    {
        this.directory = directory
    }

    void write(List<Map<String, String>> metadata, String indexFilename)
    {
        Writer temp = new FileWriter(directory + '/' + indexFilename);
        CSVWriter writer = new CSVWriter(temp);
        writer.writeNext(columns.toArray(new String[0]));
        metadata.each {item ->
            def List<String> line = []
            mapping.each {key ->
                line.add(safeExtract(item, key))
            }
            writer.writeNext(line.toArray(new String[0]))
        }
        writer.close();
    }

    static String safeExtract(Map<String, String> item, String key)
    {
        if (item.containsKey(key)) {
            return item[key];
        }
        return '';
    }
}
