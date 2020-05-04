# memsearch

WIP

## TODOs
- Save index to disk [P1]
- Load index in app memory [P1]
- Update stop words list [P1]
- Add deps.edn [P1]
- Take into account the relative distance between any two words (of the search string) in the results scores. In order to achieve this, the index will have to maintain the position(s) of the word in the document. This becomes important when the content of the document is large (ex: a blog)
- Usage of eager evaluation model paradigm to gain performance with some custom transducers. This will be helpful for large data-set (either content of the doc is large or large number of docs or both)