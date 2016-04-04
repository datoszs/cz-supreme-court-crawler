import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Utility for crawling Supreme Court of Czech Republic to obtain a list of court decision in given period
 * For now it only fetch only chosen subset of results!
 */

class Crawler
{
    static void main(String[] args)
    {
        // Configure CLI argument parser
        def cli = new CliBuilder(usage:'crawler [options] <directory>', header:'Parameters:');
        cli.h(longOpt:'help', 'print this help text')
        cli.f(longOpt:'from', args:1, argName:'date', required: true, 'Start of the interval (included) in format YYYY-MM-DD')
        cli.t(longOpt:'to', args:1, argName:'date', required: true, 'Start of the interval (included) in format YYYY-MM-DD')
        cli.d(longOpt:'directory', args:1, argName:'directory', required: true, 'Directory into which the results will be parsed')

        // Process the options
        def options = cli.parse(args)
        if (!options) {
            return
        }
        if(options.h) {
            cli.usage()
        }
        // Processing params
        Date from
        Date to
        // Start serving the request
        DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd');

        try {
            from = dateFormat.parse(options.f)
        } catch (all) {
            println "Date format of date from is invalid."
            return
        }
        try {
            to = dateFormat.parse(options.t)
        } catch (all) {
            println "Date format of date to is invalid."
            return
        }
        if (from > to) {
            println "Date interval is invalid (possibly the start is later then the end)."
        }
        def folder = new File(options.directory)
        if (!folder.exists() || !folder.isDirectory()) {
            println "The destination directory is not a folder or doesn't exist (or permissions are wrong)."
        }

        Downloader downloader = new Downloader(options.directory);
        downloader.fetchPeriod(from, to)
    }
}

