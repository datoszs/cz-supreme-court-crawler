class SupremeCourt
{
    public static final String SUPREME_COURT = 'Nejvyšší soud'

    public static final String COURT = 'Soud'
    public static final String DECISION_DATE = 'Datum rozhodnutí'
    public static final String SIGNATURE = 'Spisová značka'
    public static final String ECLI = 'ECLI'
    public static final String DECISION_TYPE = 'Typ rozhodnutí'
    public static final String KEYWORD = 'Heslo'
    public static final String DECISION_CATEGORY = 'Kategorie rozhodnutí'

    public static final String BASE_URL = "http://nsoud.cz/Judikatura/judikatura_ns.nsf/\$\$WebSearch1?SearchView"

    public static final int LEGAL_SENTENCE_SHORT = 1;   // The shortest view as possible

    public static final int DEFAULT_OFFSET = 0;
    public static final int PER_PAGE = 50;              // Theoretically the API allows up to 1000 per page, but the delay and unreliablity makes it risky.
    public static final int RESULTSET_LIMIT = 1000;     // The hardcoded limit of the API for number of results.

    public static final String SIGNATURE_CDO = 'CDO';
    public static final String SIGNATURE_TDO = 'TDO';
    public static final String SIGNATURE_ODO = 'ODO';
    public static final String SIGNATURE_ODON = 'ODON';

    def static getParameters(Date from, Date to, String signature, int offset)
    {
        def parameters = [
            'Query': '[spzn2]=' + signature + ' AND [datum_predani_na_web]>=' + from.format('dd/MM/yyyy') + ' AND [datum_predani_na_web]<=' + to.format('dd/MM/yyyy') + ' AND [SoudCreate]="'+ SUPREME_COURT + '"',
            'SearchMax': Integer.toString(RESULTSET_LIMIT),
            'pohled': Integer.toString(LEGAL_SENTENCE_SHORT),
            'start': Integer.toString(offset),
            'count': Integer.toString(PER_PAGE)
        ];
        return parameters
    }
}
