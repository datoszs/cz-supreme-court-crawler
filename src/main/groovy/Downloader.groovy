import groovy.time.TimeCategory

class Downloader
{
    private windowSize = 7   // in days
    private DocumentProcessor documentProcessor
    private MetadataWriter metadataWriter
    private metadata = []



    Downloader(String directory)
    {
        documentProcessor = new DocumentProcessor(directory)
        metadataWriter = new MetadataWriter(directory)
    }

    void execute(Date from, Date to, String signature)
    {
        println ">>> Downloading items list for ${Helpers.formatDateInterval(from, to)}"

        def allItems = [:]

        int offset = SupremeCourt.DEFAULT_OFFSET
        int itemsCount = -1;

        while (itemsCount != 0) {
            println ">>> Offset ${offset} (limit ${SupremeCourt.PER_PAGE} items)..."
            def pageItems = ResultsProcessor.process(Fetcher.fetchUrl(SupremeCourt.BASE_URL, SupremeCourt.getParameters(from, to, signature, offset)))
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
            metadata.addAll(documentProcessor.process(item['url'], Fetcher.fetchUrl(item['url'])))
        }
    }

    def fetchPeriod(from, to)
    {
        // Process whole range [from, to] via fixed sized windows
        def signatures = [SupremeCourt.REGISTRY_MASK_CDO, SupremeCourt.REGISTRY_MASK_ODO, SupremeCourt.REGISTRY_MASK_ODON]
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

        // Dump metadata
        metadataWriter.write(metadata, "metadata.csv")
    }
}

