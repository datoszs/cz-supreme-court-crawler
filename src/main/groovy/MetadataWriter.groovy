import au.com.bytecode.opencsv.CSVWriter

class MetadataWriter
{
    private columns = [
            MetadataFormat.COURT_NAME,
            MetadataFormat.REGISTRY_MARK,
            MetadataFormat.DECISION_DATE,
            MetadataFormat.WEB_PATH,
            MetadataFormat.LOCAL_PATH,
            MetadataFormat.ECLI,
            MetadataFormat.DECISION_TYPE
    ]
    private mapping = [
            SupremeCourt.COURT,
            SupremeCourt.REGISTRY_MARK,
            SupremeCourt.DECISION_DATE,
            DocumentProcessor.WEB_PATH,
            DocumentProcessor.LOCAL_PATH,
            SupremeCourt.ECLI,
            SupremeCourt.DECISION_TYPE
    ]
    private String directory

    public MetadataWriter(String directory)
    {
        this.directory = directory
    }

    public write(List<Map<String, String>> metadata, String indexFilename)
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

    private static safeExtract(Map<String, String> item, String key)
    {
        if (item.containsKey(key)) {
            return item[key];
        }
        return '';
    }
}
