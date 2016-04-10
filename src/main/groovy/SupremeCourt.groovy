class SupremeCourt
{
    public static final SUPREME_COURT = 'Nejvyšší soud'

    public static final COURT = 'Soud'
    public static final DECISION_DATE = 'Datum rozhodnutí'
    public static final REGISTRY_MARK = 'Spisová značka'
    public static final ECLI = 'ECLI'
    public static final DECISION_TYPE = 'Typ rozhodnutí'
    public static final KEYWORD = 'Heslo'
    public static final DECISION_CATEGORY = 'Kategorie rozhodnutí'

    public static final BASE_URL = "http://nsoud.cz/Judikatura/judikatura_ns.nsf/\$\$WebSearch1?SearchView"

    public static final int LEGAL_SENTENCE_SHORT = 1;   // The shortest view as possible

    public static final int DEFAULT_OFFSET = 0;
    public static final int PER_PAGE = 50;              // Theoretically the API allows up to 1000 per page, but the delay and unreliablity makes it risky.
    public static final int RESULTSET_LIMIT = 1000;     // The hardcoded limit of the API for number of results.

    public static final REGISTRY_MASK_CDO = 'CDO';
    public static final REGISTRY_MASK_TDO = 'TDO';
    public static final REGISTRY_MASK_ODO = 'ODO';
    public static final REGISTRY_MASK_ODON = 'ODON';

    def static getParameters(Date from, Date to, String registry_mark, int offset)
    {
        def parameters = [
            'Query': '[spzn2]=' + registry_mark + ' AND [datum_predani_na_web]>=' + from.format('dd/MM/yyyy') + ' AND [datum_predani_na_web]<=' + to.format('dd/MM/yyyy') + ' AND [SoudCreate]="'+ SUPREME_COURT + '"',
            'SearchMax': Integer.toString(RESULTSET_LIMIT),
            'pohled': Integer.toString(LEGAL_SENTENCE_SHORT),
            'start': Integer.toString(offset),
            'count': Integer.toString(PER_PAGE)
        ];
        return parameters
    }
}
