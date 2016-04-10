import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class DocumentProcessor
{

    public static final WEB_PATH = 'web_path'
    public static final LOCAL_PATH = 'local_path'

    private String directory    // Directory to download decisions into

    public DocumentProcessor(String directory)
    {
        this.directory = directory
    }

    public process(String url, Document document)
    {
        document.select('body').attr('onload', '') // Remove the print popup dialog
        document.select('div.tlacitko').remove(); // Remove the back button

        // Parse metadata
        def item = [:]
        item[WEB_PATH] = url
        String[] eclis = []
        Elements table = document.select('table#box-table-a')
        if (table.size() == 0) {
            return;
        }
        Elements rows = table.get(0).select("tr");
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (cols.size() >= 2) {
                def key = sanitizeKey(cols.get(0).text())
                if (key == SupremeCourt.REGISTRY_MARK) {
                    // ignored explicitly, we extract this information from ECLI
                } else if (key == SupremeCourt.DECISION_DATE) {
                    item[key] = Helpers.getNormalizedDate(cols.get(1).text())
                } else if (key == SupremeCourt.ECLI) {
                    eclis = cols.get(1).text().split('; ')
                } else {
                    item[key] = cols.get(1).text()
                }
            }
        }
        // Prepare output directory
        def baseDirectory = directory + '/documents/'
        new File(baseDirectory).mkdirs()

        // Expand to multiple documents according to ECLI
        // and store file under ECLI (which is unique) in documents folder
        def items = []
        eclis.each {ecli ->
            def temp = item.clone()
            temp[SupremeCourt.ECLI] = ecli
            temp[SupremeCourt.REGISTRY_MARK] = Helpers.extractRegistryMarkFromECLI(ecli)
            temp['local_path'] = Helpers.getSafeName(ecli) + '.html'

            def file = new File(baseDirectory + Helpers.getSafeName(ecli) + '.html')
            file.setText(document.html())

            items.add(temp)
        }
        return items
    }

    private static sanitizeKey(String value)
    {
        if (value.endsWith(':')) {
            return value.substring(0, value.length() - 1)
        }
        return value
    }
}
