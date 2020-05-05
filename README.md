# memsearch
[![Clojars Project](https://img.shields.io/clojars/v/coderafting/memsearch.svg)](https://clojars.org/coderafting/memsearch)
[![cljdoc badge](https://cljdoc.org/badge/coderafting/memsearch)](https://cljdoc.org/d/coderafting/memsearch/CURRENT)

A ClojureScript library to provide in-memory full-text indexing and search facilities.

## Offerings
Currently, the library offers two facilities - **full-text indexing** and **full-text search**.

The indexing facility is offerd via `memsearch.core/text-index` function, and the search facility is offered via `memsearch.core/text-search` function. Their capabilities and limitations are described in the **Usage** section.

## Usage

### Setup
Add this library as a dependency to your project as:
```clojure
[coderafting/memsearch "0.1.0"]
```
Require `memsearch.core` in your working namespace.
```clojure
(ns your-project-namespace
  (:require [memsearch.core :as ms]))
```

### Available functions
Currently, there are 2 functions available for consumption in the `memsearch.core` namespace:
```clojure
text-index
text-search
```
We will go through each of them below.

#### text-index
The function's args signature is defined as:
```clojure
[doc-coll & opts-map]
```
The function accepts a collection of documents (`doc-coll`) and some options via `opts-map`, and returns an index map that can be used for efficient search. The library uses [soundex](https://en.wikipedia.org/wiki/Soundex) algorithm to generate index keys for each valid word.

A document is a map with two keys - `:id` and `:content`.

- The `:id` is the unique identifier for the document that the users can use during search to get the actual document from the database.
- The `:content` key is the string whose words will be indexed.

Users may provide an `opts-map` with keys `:maintain-actual?` and `:valid-word-fn`.

- When `:maintain-actual?` is `true`, the actual indexed words are saved along with the encoded form of the words. Ofcourse, maintaining actual words will consume additional space.
- The value of `:valid-word-fn` is a custom word validator that users may provide. The library uses its own validator as the default validator. The value of `:valid-word-fn` is a single arity function that takes one word (string) and returns boolean.

Examples below:

```clojure
(ms/text-index [{:id 1 :content "World war 1"}
                {:id 2 :content "Independence for the world"}]
               {:maintain-actual? true})
=>
    {"W643" [{:id 1, :actuals #{"world"}, :frequency 1}
             {:id 2, :actuals #{"world"}, :frequency 1}]
     "W600" [{:id 1, :actuals #{"war"}, :frequency 1}]
     "I531" [{:id 2, :actuals #{"independence"}, :frequency 1}]}

(ms/text-index [{:id 1 :content "World war 1"}
                {:id 2 :content "Independence for the world"}])
=>
    {"W643" [{:id 1, :frequency 1}
             {:id 2, :frequency 1}]
     "W600" [{:id 1, :frequency 1}]
     "I531" [{:id 2, :frequency 1}]}
```
The `:id`s are same as the ones supplied by the user in the input docs. The value of `:frequency` is the frequency of the word in the `:content` string.

Please see the **TODOs** section for future plans to improvise indexing.

#### text-search
The function's args signature is defined as:
```clojure
[query-string index & opts-map]
```
The function takes a query string, an index map, and some options to return the result in the form of a map.

And example of `query-string` is `"knuth on programming"`. The `index` is expected to be built from the `text-index` function described above. If there is no `opts-map` provided, then the function returns a map with document ids (`:id`) as keys and map (with `:score` key) as the values.

The `opts-map` can have the following keys:

- `:db` - If provided, the return value will contain additional data from the db based on the doc-ids returned by the index.
- `:fetch-fn` - A function with args signature `[db doc-ids]`. It exists only with `:db` key. It is expected to return results in the form similar to `{1 {:data {}} 2 {:data {}}}` or `[{1 {:data {}}} {2 {:data {}}}]`. The key `:data` and its value could be any key and value.
- `:sorted?` - If `true`, the result will be sorted. Defaults to decreasing order of sorting.
- `:increasing?` - Exists only with `:sorted?` key, a `true` value indicates the sorting to be in the increasing order.
- `:valid-word-fn` - A single arity function that takes one word (string) and returns boolean.

If a custom `fetch-fn` is not provided in the `opts-map`, then the `:db` is expected to be a map with doc-ids as keys and maps as values.

Examples below:

```clojure

(def sample-index (ms/text-index [...data...]))
(def sample-db {...data...})

(m/text-search "knuth on programming" sample-index)
=>
    {1 {:score 3}
     35 {:score 0.9022727272727272}
     4 {:score 0.9714285714285714}
     36 {:score 2.7818181818181817}}

(m/text-search "knuth on programming" sample-index {:db sample-db :sorted? true})
=>
    {1 {:score 3 :data {}}
     36 {:score 2.7818181818181817 :data {}}
     4 {:score 0.9714285714285714 :data {}}
     35 {:score 0.9022727272727272 :data {}}}

The :data key is coming from the db, it can be any key and value.
```

Please see the **TODOs** section for future plans to improvise search.

## Feedback/Discussions
Github issues are a good way to discuss library related topics. I am also reachable via [CodeRafting](https://www.coderafting.com/).

## Some high-level TODOs (not in the order of priority)

#### General
- `Clj` compatibility
- Input validators
- Benchmarks at different scales

#### Search and Index Improvization
- While computing score, take into account the `relative distance` between any two words (of the search string) in the results' content. In order to achieve this, the index will have to maintain the position(s) of the word in the documents' content. This becomes important when the content of the document is large (ex: a blog).
- Consideration of the `weights of words` based on what content they exist in. In order to achieve this, the index will have to take different types of contents into account. Ex: `{:content {:title "" :blog ""}}`.
- Adopt `eager evaluation paradigm` to gain performance with some custom transducers. This will be helpful for large data-set (either content of the doc is large or large number of docs or both).

#### Index persistence
- Save index to disk
- Load index in app memory
- Dynamic update of the index as docs gets added, updated, and deleted.
- Sync with saved index on every mutation of the index.

## License
Distributed under the MIT License. Copyright (c) 2020 [Amarjeet Yadav](https://www.coderafting.com/).
