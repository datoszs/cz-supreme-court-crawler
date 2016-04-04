import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class DocumentProcessor
{

    public static final String PATH = 'path'

    private String directory    // Directory to download decisions into

    public DocumentProcessor(String directory)
    {
        this.directory = directory
    }

    public process(String signature, Document document)
    {
        document.select('body').attr('onload', '') // Remove the print popup dialog
        document.select('div.tlacitko').remove(); // Remove the back button

        // Parse metadata
        def itemMetadata = [:]
        Elements table = document.select('table#box-table-a')
        if (table.size() == 0) {
            return;
        }
        Elements rows = table.get(0).select("tr");
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (cols.size() >= 2) {
                itemMetadata[sanitizeKey(cols.get(0).text())] = cols.get(1).text()
            }
        }

        // Store file
        def path = directory + '/' + Helpers.getSafeName(signature) + '.html'
        itemMetadata['path'] = path
        def file = new File(path)
        file.setText(document.html())

        return itemMetadata
    }

    private static sanitizeKey(String value)
    {
        if (value.endsWith(':')) {
            return value.substring(0, value.length() - 1)
        }
        return value
    }
}
