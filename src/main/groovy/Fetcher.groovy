import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Fetcher {

    static final int TIMEOUT = 10000 // in miliseconds
    static final int ATTEMPT_PAUSE = 3000 // in miliseconds

    Integer attempts = 1
    boolean waitOnFail = false

    Fetcher(Integer attempts, boolean waitOnFail)
    {
        this.attempts = attempts
        this.waitOnFail = waitOnFail
    }

    Document fetchUrl(String url, attempts = 3, waitOnFeedback = false)
    {
        return attemptize({
            return Jsoup.connect(url).timeout(TIMEOUT).get()
        });
    }

    Document fetchUrl(String url, Map<String, String> parameters)
    {
        return attemptize({
            return Jsoup.connect(url).data(parameters).timeout(TIMEOUT).get()
        });
    }

    Document attemptize(closure) {
        while (attempts == null || attempts > 0) {
            try {
                return closure()
            } catch (Exception ex) {
                Helpers.printErr('Error: ' + ex.getClass().getName() + ': ' + ex.getMessage())
                if (waitOnFail) {
                    System.console().readLine('Press ENTER to continue...')
                } else {
                    try {
                        Thread.sleep(ATTEMPT_PAUSE);
                    } catch(InterruptedException ex2) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (attempts != null) {
                    attempts -= 1
                }
            }
        }
        throw new Exception('Number of fetch attempts have been exceeded. Failing now.')
    }
}
