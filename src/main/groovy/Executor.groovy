import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Executor of crawling Supreme Court of Czech Republic for obtaining a list of court decision in given period.
 */

class Executor
{

    final static int WINDOW_LENGTH = 14

    static void main(String[] args)
    {
        // Configure CLI argument parser
        def cli = new CliBuilder(usage: 'crawler [options]', header: 'Parameters:');
        cli.h(longOpt: 'help', 'print this help text')
        cli._(longOpt: 'from', args: 1, argName: 'DATE', required: true, 'Start date of the interval (inclusive) in format YYYY-MM-DD.')
        cli._(longOpt: 'to', args: 1, argName: 'DATE', required: true, 'End date of the interval (inclusive) in format YYYY-MM-DD,')
        cli._(longOpt: 'directory', args: 1, argName: 'DIRECTORY', required: true, 'Empty directory into which the results will be downloaded.')
        cli._(longOpt: 'publication-date', required: false, 'Obtain results based on publication date (default). Not reliable for past.')
        cli._(longOpt: 'decision-date', required: false, 'Obtain results based on decision date (default).')
        cli._(longOpt: 'registry-marks', args: 1, argName: 'MARKS', required: true, 'Comma separated list of registry marks to crawl.')
        cli._(longOpt: 'window', args: 1, argName: 'DAYS', required: false, 'Length of crawling window to workaround maximal number of results. Default value is 14 days.')
        cli._(longOpt: 'fetch-attempts', args: 1, argName: 'COUNT', required: false, 'Number of attempts to fetch remote documents.')
        cli._(longOpt: 'wait-on-fail', required: false, 'Whether to wait on explicit user feedback on failed fetch attempts.')

        // Process the options
        def options = cli.parse(args)
        if (!options) {
            return
        }
        if (options.h) {
            cli.usage()
            return
        }
        // Processing params
        Date from
        Date to
        // Start serving the request
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd');

        try {
            from = dateFormat.parse(options.from)
        } catch (all) {
            println "Date format of date from is invalid."
            System.exit(1)
        }
        try {
            to = dateFormat.parse(options.to)
        } catch (all) {
            println "Date format of date to is invalid."
            System.exit(1)
        }
        if (from > to) {
            println "Date interval is invalid (possibly the start is later then the end)."
            System.exit(1)
        }
        def folder = new File(options.directory)
        if (!folder.exists() || !folder.isDirectory()) {
            println "The destination directory is not a folder or doesn't exist (or permissions are wrong)."
            System.exit(1)
        }
        if (folder.list().contains("documents") || folder.list().contains("metadata.csv")) {
            println "The destination directory is not empty. Please remove content before proceeding."
            System.exit(1)
        }
        Integer fetchAttempts = 1
        if (options.'fetch-attempts') {
            try {
                fetchAttempts = (options.'fetch-attempts').toInteger()
                if (fetchAttempts < 0) {
                    throw new Exception()
                }
                if (fetchAttempts == 0) { // transform 0 into null as that means unlimited
                    fetchAttempts = null
                }
            } catch (all) {
                println "The number of fetch attempts is invalid. Please provide non negative number."
                System.exit(1)
            }
        }
        boolean waitOnFail = options.'wait-on-fail'
        if (fetchAttempts == null && !waitOnFail) {
            println "Unlimited number of fetch attempts without --wait-on-fail option is invalid."
            System.exit(1)
        }
        boolean usePublicationDate = options.'publication-date'
        boolean useDecisionDate = options.'decision-date'
        if (!usePublicationDate && !useDecisionDate) {
            usePublicationDate = true
        }
        if (usePublicationDate && useDecisionDate) {
            println "Cannot use publication date together with decision date.";
            System.exit(1)
        }
        int intervalType = (useDecisionDate) ? SupremeCourt.INTERVAL_TYPE_DECISION_DATE : SupremeCourt.INTERVAL_TYPE_PUBLICATION_DATE

        int window = WINDOW_LENGTH
        if (options.window) {
            try {
                window = (options.window).toInteger()
                if (window < 1) {
                    throw new Exception()
                }
            } catch (all) {
                println "The window length is invalid. Please provide positive number."
                System.exit(1)
            }
        }

        String[] registryMarks = (options.'registry-marks').split(',')

        Crawler crawler = new Crawler(folder.getAbsolutePath(), window, registryMarks, intervalType, fetchAttempts, waitOnFail);
        try {
            crawler.fetchPeriod(from, to)
        } catch (TooManyItemsException exception) {
            Helpers.printErr('Query error: ' + exception.getMessage())
            System.exit(2)
        } catch (Exception exception) {
            Helpers.printErr('General error: ' + exception.getMessage())
            System.exit(3)
        }
    }
}

