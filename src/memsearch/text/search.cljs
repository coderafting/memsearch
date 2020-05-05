(ns memsearch.text.search
  (:require [memsearch.text.index :as in]
            [clj-fuzzy.metrics :as fm]
            [clj-fuzzy.phonetics :as ph]))

(defn fetch-docs-for-a-word
  "Fetches all the indexed docs for a word."
  [w index]
  (index (ph/soundex w)))

(defn highest-similarity
  "Returns the similarilty value (between 0 and 1, both inclusive) for a word 
  against the best similar word from a set of words."
  [w str-set]
  (let [st (first (sort-by #(fm/jaro-winkler w %) > str-set))]
    (fm/jaro-winkler w st)))

(defn scored-doc-for-a-word
  "Returns a map with doc-id as key and a map (with `:score`) as value.
   The supplied doc is expected to have `:id` and `:frequency` of the supplied word."
  [w doc]
  (if (:actuals doc)
    {(:id doc) {:score (* (highest-similarity w (:actuals doc)) (:frequency doc))}}
    {(:id doc) {:score (:frequency doc)}}))

(defn scored-docs-for-word
  "Returns a map with doc-ids as keys and a maps (with `:score`) as values.
   The supplied docs are expected to have `:id` and `:frequency` of the supplied word."
  [w docs]
  (loop [ds docs
         res {}]
    (if (first ds)
      (recur (rest ds) (into res (scored-doc-for-a-word w (first ds))))
      res)))

(defn merge-scored-docs
  "Example inputs: `{1 {:score 10 :data {:age 20}}}` and `{1 {:score 12 :data {:age 20}} 2 {:score 5 :data {:age 30}}}`
   Return: `{1 {:score 22 :data {:age 20}} 2 {:score 5 :data {:age 30}}}`
   If there are duplicate keys (values are equal):
    - The values of `:score` gets added.
    - The values of `:data` are expected to be equal in both the inputs, and will be returned as is.
   If the keys are not duplicate, then simply append as a key val pair."
  [m1 m2]
  (let [skeys (keys m2)]
    (loop [ks skeys
           res m1]
      (if (first ks)
        (recur (rest ks) (if (res (first ks))
                           (assoc-in res [(first ks) :score] (+ (:score (res (first ks)))
                                                                (:score (m2 (first ks)))))
                           (assoc res (first ks) (m2 (first ks)))))
        res))))

(defn scored-docs-for-str-coll
  "Fetches the docs for all the valid words of the supplied collection.
   The score for each fetched doc is combined (added) for each word of the words collection, 
   to compute the final socre for the doc."
  [s-coll index]
  (loop [strs s-coll
         res {}]
    (if (first strs)
      (recur (rest strs) 
             (merge-scored-docs res 
                                (scored-docs-for-word 
                                 (first strs) 
                                 (fetch-docs-for-a-word (first strs) index))))
      res)))

(defn score-comparator
  "Sorting comparator based on the score, either in increasing or in decreasing order.
   Defaults to decreasing order."
  [m & increasing?]
  (fn [key1 key2]
    (let [v1 [(get-in m [key1 :score]) key1]
          v2 [(get-in m [key2 :score]) key2]]
      (if (first increasing?)
        (compare v1 v2)
        (compare v2 v1)))))

(defn sorted-scored-docs
  "Results sorted by score, either in increasing or in decreasing order.
   Defaults to decreasing order."
  [s-coll index & increasing?]
  (let [res (scored-docs-for-str-coll s-coll index)]
    (into (sorted-map-by (if (first increasing?) (score-comparator res true) (score-comparator res))) 
          res)))

(defn scored-docs
  "Results from the index map based on the supplied words collection.
   Return value may or may not be sorted based on provided options.
   Defaults to non-sorted result."
  [s-coll index & opts-map]
  (let [opts (first opts-map)
        sorted? (:sorted? opts)
        increasing? (:increasing? opts)]
    (cond 
      (and sorted? increasing?) (sorted-scored-docs s-coll index true)
      (and sorted? (not increasing?)) (sorted-scored-docs s-coll index)
      :else (scored-docs-for-str-coll s-coll index))))

(defn default-fetch-fn
  "The `db` is expected to be a map with doc-ids as keys and maps as values.
   Each value map may contain the data that needs to come out of the `db`."
  [db doc-ids]
  (into {} (map #(hash-map % (db %))) doc-ids))

(defn scored-docs-with-data
  "If a custom fetch fn is not provided in the opts-map, then the `db` is expected to be a map with doc-ids as keys and maps as values.
   The return value of fetch-fn is expected to be a map or a collection similar to:
   `{1 {:data {}} 2 {:data {}}}` or `[{1 {:data {}}} {2 {:data {}}}]`."
  [s-coll index db & opts-map]
  (let [docs (scored-docs s-coll index (first opts-map))
        ids (keys docs)
        custom-fetch-fn (:fetch-fn (first opts-map))
        docs-data (if custom-fetch-fn (custom-fetch-fn db ids)
                   (default-fetch-fn db ids))]
    (cond
      (map? docs-data) (merge-with into docs docs-data)
      (or (vector? docs-data)
          (list? docs-data)) (loop [data docs-data
                                    res docs]
                               (if (first data)
                                 (recur (rest data) (merge-with into res (first data)))
                                 res))
      :else docs)))

(defn text-search
  "Search the index with a string of one or more words.
   Various options can be provided:
    `:db` - If provided, the return value will contain additional data from the db based on the doc-ids returned by the index.
    `:fetch-fn` - A function with args signature `[db doc-ids]`. Exists only with `:db` key.
                  It is expected to return results in the form similar to `{1 {:data {}} 2 {:data {}}}` or `[{1 {:data {}}} {2 {:data {}}}]`.
                  The key `:data `and its value could be any key and value.
    `:sorted?` - If `true`, the result should be sorted. Defaults to decreasing order of sorting.
    `:increasing?` - Exists only with `:sorted?` key, a `true` value indicates the sorting to be in the increasing order.
    `:valid-word-fn` - A single arity fn that takes one word (string) and returns boolean."
  [query-string index & opts-map]
  (let [opts (first opts-map)
        db (:db opts)
        str-coll (in/index-keys-from-string query-string (:valid-word-fn opts))]
    (if db
      (scored-docs-with-data str-coll index db opts)
      (scored-docs str-coll index opts))))
