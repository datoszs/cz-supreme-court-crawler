import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Fetcher {

    static final int TIMEOUT = 10000 // in miliseconds

    static Document fetchUrl(String url)
    {
        return Jsoup.connect(url).timeout(TIMEOUT).get()
    }

    static Document fetchUrl(String url, Map<String, String> parameters)
    {
        return Jsoup.connect(url).data(parameters).timeout(TIMEOUT).get();
    }
}
