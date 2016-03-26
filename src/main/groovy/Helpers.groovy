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
}
