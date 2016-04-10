import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Fetcher {

    private static final TIMEOUT = 10000 // in miliseconds

    public static Document fetchUrl(String url)
    {
        return Jsoup.connect(url).timeout(TIMEOUT).get()
    }

    public static Document fetchUrl(String url, parameters)
    {
        return Jsoup.connect(url).data(parameters).timeout(TIMEOUT).get();
    }
}
