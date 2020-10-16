/**
 * Important constants, hard-coded limits and parameter builder.
 */
class SupremeCourt
{
    static final int INTERVAL_TYPE_DECISION_DATE = 1
    static final int INTERVAL_TYPE_PUBLICATION_DATE = 2

    static final String SUPREME_COURT = 'Nejvyšší soud'

    static final String COURT = 'Soud'
    static final String DECISION_DATE = 'Datum rozhodnutí'
    static final String REGISTRY_MARK = 'Spisová značka'
    static final String ECLI = 'ECLI'
    static final String DECISION_TYPE = 'Typ rozhodnutí'
    static final String KEYWORD = 'Heslo'
    static final String DECISION_CATEGORY = 'Kategorie rozhodnutí'

    static final String BASE_URL = "http://nsoud.cz/Judikatura/judikatura_ns.nsf/\$\$WebSearch1?SearchView"

    static final int LEGAL_SENTENCE_SHORT = 1;   // The shortest view as possible

    static final int DEFAULT_OFFSET = 1;         // Start is from 1, using 0 leads to unexpected sorting and unstable results!
    static final int PER_PAGE = 50;              // Theoretically the API allows up to 1000 per page, but the delay and unreliablity makes it risky.
    static final int RESULTSET_LIMIT = 1000;     // The hard-coded limit of the API for number of results. When this limit is exceeded by the query then the result is inherently incomplete.

    static Map<String, String> getParameters(int intervalType, Date from, Date to, String registry_mark, int offset)
    {
        String intervalColumn
        if (intervalType == INTERVAL_TYPE_DECISION_DATE) {
            intervalColumn = 'datum_rozhodnuti'
        } else {
            intervalColumn = 'datum_predani_na_web'
        }
        def parameters = [
            'Query': '[spzn2]=' + registry_mark + ' AND [' + intervalColumn + ']>=' + from.format('dd/MM/yyyy') + ' AND [' + intervalColumn + ']<=' + to.format('dd/MM/yyyy') + ' AND [SoudCreate]="'+ SUPREME_COURT + '"',
            'SearchMax': Integer.toString(RESULTSET_LIMIT),
            'pohled': Integer.toString(LEGAL_SENTENCE_SHORT),
            'start': Integer.toString(offset),
            'count': Integer.toString(PER_PAGE),
            'searchOrder': Integer.toString(4),
        ];
        return parameters
    }
}
