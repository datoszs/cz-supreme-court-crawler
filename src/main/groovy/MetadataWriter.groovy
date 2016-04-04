import au.com.bytecode.opencsv.CSVWriter

class MetadataWriter
{
    private String directory

    public MetadataWriter(String directory)
    {
        this.directory = directory
    }

    public write(List<Map<String, String>> metadata, String indexFilename)
    {
        Writer temp = new FileWriter(directory + '/' + indexFilename);
        CSVWriter writer = new CSVWriter(temp);
        def String[] firstLine = [SupremeCourt.COURT, SupremeCourt.SIGNATURE, SupremeCourt.ECLI, SupremeCourt.DECISION_DATE, SupremeCourt.DECISION_CATEGORY, SupremeCourt.DECISION_TYPE, DocumentProcessor.PATH];
        writer.writeNext(firstLine);
        metadata.each {item ->
            def List<String> line = []
            firstLine.each {key ->
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
