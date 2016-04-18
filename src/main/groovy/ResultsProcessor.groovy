import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Parses given result overview page and extract list of document IDs together with link leading to full document.
 */
class ResultsProcessor
{

    def static process(Document document)
    {
        def items = [:]
        Elements links = document.select('a[href~=/(WebPrint|WebSearch)/]') // Select all links containing these segments
        links.each { link ->
            def href = link.attr('href');
            def match = href =~ /\/(WebPrint|WebSearch)\/(.*)\?/
            if (!match) {
                throw new RuntimeException("Invalid link")
            }
            def type = match.group(1)
            def id = match.group(2)
            if (!items.containsKey(id)) {
                items[id] = [:]
            }
            if (type == 'WebPrint') {
                items[id]['url'] = link.attr('abs:href')
            } else {
                items[id]['signature'] = link.text()
            }
        }
        return items
    }
}
