# Czech Republic Supreme Court Crawler

Command line utility for downloading decision documents from Czech Republic Supreme Court between given two dates.
Supports crawling of certain registry marks and using two types of dates: decision and publication.

## Usage

```
java -jar ./build/libs/cz-supreme-court-crawler-all-1.0.jar --from 2001-01-01 --to 2001-01-31 --directory ../downloaded/ --registry-marks ODO,TDO --decision-date
```

## Notes

- Decisions before 2010-04-01 do not carry complete set of search data (it seems that this especially applies to publication date!)
- Some decisions are published at the 2008-12-31 (more than 1000)
- More than 1000 result per day are not downloadable (accessible via API), when this happen you have to access the decisions via different query schema.
