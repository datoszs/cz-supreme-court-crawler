import groovy.time.TimeCategory
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class Downloader
{

    private int timeout = 10000 // in miliseconds
    private int windowSize = 7   // in days
    private String directory    // into which the decisions should be downloaded

    Downloader(String directory)
    {
        this.directory = directory
    }

    void execute(Date from, Date to, String signature)
    {
        println ">>> Downloading items list for ${Helpers.formatDateInterval(from, to)}"

        def allItems = [:]

        int offset = SupremeCourt.DEFAULT_OFFSET
        int itemsCount = -1;

        while (itemsCount != 0) {
            println ">>> Offset ${offset} (limit ${SupremeCourt.PER_PAGE} items)..."
            def document = fetchUrl(SupremeCourt.BASE_URL, SupremeCourt.getParameters(from, to, signature, offset));
            def pageItems = parseItems(document)
            allItems.putAll(pageItems)
            // Check limits
            if (allItems.size() >= SupremeCourt.RESULTSET_LIMIT) {
                throw new RuntimeException('Limit of maximal items exceeded! This range is incomplete.')
            }
            // Prepare next iteration
            itemsCount = pageItems.size()
            offset += SupremeCourt.PER_PAGE
        }

        println ">>> Downloading items content..."
        allItems.each {id, item ->
            println ">>> Item ${item['signature']}..."
            def file = new File(directory + '/' + Helpers.getSafeName(item['signature']) + '.html')
            def document = fetchUrl(item['url']);
            document.select('body').attr('onload', '') // remove the print popup dialog
            file << document.html()
        }

    }

    private Document fetchUrl(String url)
    {
        return Jsoup.connect(url).timeout(timeout).get()
    }

    private Document fetchUrl(String url, parameters)
    {
        return Jsoup.connect(url).data(parameters).timeout(timeout).get();
    }

    private parseItems(Document document)
    {
        def items = [:]
        Elements links = document.select('a[href~=/(WebPrint|WebSearch)/]') // Select all links containing these segments
        links.each { link ->
            def href = link.attr('href');
            def match = href =~ /\/(WebPrint|WebSearch)\/(.*)\?/
            if (!match) {
                throw new RuntimeException("Invalid link")
            }
            def type = match.group(1)
            def id = match.group(2) // TODO: sanitation, nektere veci maji vice cisel!
            if (!items.containsKey(id)) {
                items[id] = [:]
            }
            if (type == 'WebPrint') {
                items[id]['url'] = link.attr('abs:href')
            } else {
                items[id]['signature'] = link.text()
            }

        }
        return items
    }

    def fetchPeriod(from, to)
    {
        def signatures = [SupremeCourt.SIGNATURE_CDO, SupremeCourt.SIGNATURE_ODO, SupremeCourt.SIGNATURE_ODON, SupremeCourt.SIGNATURE_TDO]
        println "Fetching interval ${Helpers.formatDateInterval(from, to)} in windows: "
        use (TimeCategory) {
            def windowStart = from
            def windowEnd = ((windowStart + windowSize.days) < to) ? windowStart + windowSize.days : to
            while (windowStart <= windowEnd) {
                windowEnd = ((windowStart + windowSize.days) < to) ? windowStart + windowSize.days : to
                println "> ${Helpers.formatDateInterval(windowStart, windowEnd)}:"
                signatures.each {signature ->
                    println ">> Signature ${signature}"
                    execute(windowStart, windowEnd, signature)
                }
                windowStart = windowStart + windowSize.days
            }
        }
    }
}

