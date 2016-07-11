import groovy.time.TimeCategory

/**
 * Crawl search court search to download decision document in given period using windowing to prevent exceeding API limits.
 * registry ma
 */
class Crawler
{
    final static String METADATA_FILE = 'metadata.csv'
    final static String DOCUMENTS_DIRECTORY = 'documents'

    int windowLength
    String[] registryMarks
    int intervalType

    DocumentProcessor documentProcessor
    MetadataWriter metadataWriter
    Fetcher fetcher

    def metadata = []

    Crawler(String directory, int windowLength, String[] registryMarks, int intervalType, Integer fetchAttempts, boolean waitOnFail)
    {
        this.windowLength = windowLength
        this.registryMarks = registryMarks
        this.intervalType = intervalType

        this.fetcher = new Fetcher(fetchAttempts, waitOnFail)

        documentProcessor = new DocumentProcessor(directory)
        metadataWriter = new MetadataWriter(directory)
    }

    void execute(Date from, Date to, String mark)
    {
        println ">>> Downloading items list for ${Helpers.formatDateInterval(from, to)}"

        def allItems = [:]
        int offset = SupremeCourt.DEFAULT_OFFSET
        int itemsCount = -1;
        def beforeAdd = 0;

        while (itemsCount != 0) {
            println ">>> Offset ${offset} (limit ${SupremeCourt.PER_PAGE} items)..."
            def pageItems = ResultsProcessor.process(fetcher.fetchUrl(SupremeCourt.BASE_URL, SupremeCourt.getParameters(intervalType, from, to, mark, offset)))
            beforeAdd = allItems.size()
            allItems.putAll(pageItems)
            // Check limits
            if (allItems.size() >= SupremeCourt.RESULTSET_LIMIT) {
                throw new TooManyItemsException('Query returned more than maximal limit of results. Quitting as the result wouldn\'t be reliable complete.')
            }
            // Prepare next iteration
            if (beforeAdd + pageItems.size() != allItems.size()) {
                println "Warning: " + ((beforeAdd + pageItems.size()) - allItems.size()) + " items already present in the result list!"
            }
            println "Page items " + pageItems.size()
            println "Total items " + allItems.size()
            itemsCount = pageItems.size()
            offset += SupremeCourt.PER_PAGE
        }

        println ">>> Downloading items content..."
        documentProcessor.prepareDocumentsDirectory()
        allItems.each {id, item ->
            println ">>> Item ${item['signature']}..."
            metadata.addAll(documentProcessor.process(item['url'], fetcher.fetchUrl(item['url'])))
        }
    }

    void fetchPeriod(from, to)
    {
        // Process whole range [from, to] via fixed sized windows
        println "Fetching interval ${Helpers.formatDateInterval(from, to)} in windows: "
        use (TimeCategory) {
            Date windowStart = from
            Date windowEnd = ((windowStart + (windowLength - 1).days) < to) ? windowStart + (windowLength - 1).days : to
            while (windowStart <= to) {
                windowEnd = ((windowStart + (windowLength - 1).days) < to) ? windowStart + (windowLength - 1).days : to
                registryMarks.each {mark ->
                    println ">> Registry mark ${mark}"
                    execute(windowStart, windowEnd, mark)
                }
                windowStart = windowStart + windowLength.days
            }
        }

        // At the end dump the metadata into CSV
        metadataWriter.write(metadata, METADATA_FILE)
    }
}

