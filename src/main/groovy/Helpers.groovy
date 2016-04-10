import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class Helpers
{
    def static formatDateInterval(Date from, Date to)
    {
        return "[${from.format('yyyy-MM-dd')}, ${to.format('yyyy-MM-dd')}]"
    }

    def static getSafeName(name)
    {
        return name.replaceAll("\\W+", "_")
    }

    def static getNormalizedDate(input)
    {
        DateFormat dateFormat = new SimpleDateFormat('MM/dd/yyyy');
        try {
            Date date = dateFormat.parse(input)
            return date.format('yyyy-MM-dd')
        } catch (ParseException exception) {
            printErr('Cannot parse date [' + input + '].')
        }
    }

    def static printErr(message)
    {
        System.err.println(message)
    }

    def static extractRegistryMarkFromECLI(ecli)
    {
        // E.g. ECLI:CZ:NS:2015:22.CDO.4013.2013.1
        def match = ecli =~ /^ECLI:CZ:NS:(\d{4}):(\d{1,}).(\w{1,10}).(\d{1,}).(\d{1,}).(\d{1,})$/
        if (match && match.groupCount() == 6) {
            return match.group(2) + ' ' + match.group(3) + ' ' + match.group(4) + '/' + match.group(5)
        } else {
            printErr('Cannot match ECLI ' + ecli + ' to register mark.')
        }
        return null;

    }
}
