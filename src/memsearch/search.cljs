(ns memsearch.search
  (:require [memsearch.stopwords :as sw]
            [memsearch.index :as in]
            [clj-fuzzy.stemmers :as st]
            [clj-fuzzy.metrics :as fm]
            [clj-fuzzy.phonetics :as ph]
            [clojure.math.combinatorics :as combo]))

(def test-index
  {"W643" [{:id 1, :actuals #{"world"}, :frequency 2}
           {:id 2, :actuals #{"worlds" "world"}, :frequency 2}]
   "W600" [{:id 1, :actuals #{"war"}, :frequency 1}
           {:id 2, :actuals #{"war"}, :frequency 1}]
   "I531" [{:id 2, :actuals #{"independence" "indepandance"}, :frequency 2}
           {:id 3, :actuals #{"independence"}, :frequency 1}]})


(defn fetch-docs-for-a-word
  [w index]
  (index (ph/soundex w)))

;(fetch-docs-for-a-word "war" test-index)

(defn highest-similarity
  [w str-set]
  (let [st (first (sort-by #(fm/jaro-winkler w %) > str-set))]
    (fm/jaro-winkler w st)))
;(highest-similarity "independence" #{"independenc" "independ" "indepandance" "india"})

(defn scored-doc-for-a-word
  [w doc]
  (if (:actuals doc)
    {(:id doc) {:score (* (highest-similarity w (:actuals doc)) (:frequency doc))}}
    {(:id doc) {:score (:frequency doc)}}))

(defn scored-docs-for-word
  [w docs]
  (loop [ds docs
         res {}]
    (if (first ds)
      (recur (rest ds) (into res (scored-doc-for-a-word w (first ds))))
      res)))

;(scored-docs-for-word "world" (test-index "W643"))

(defn merge-scored-docs
  "Example inputs: {1 {:score 10 :data {:age 20}}} and {1 {:score 12 :data {:age 20}} 2 {:score 5 :data {:age 30}}}
   Return: {1 {:score 22 :data {:age 20}} 2 {:score 5 :data {:age 30}}}
   If there are duplicate keys (values are equal):
    - The values of :score gets added.
    - The values of :data are expected to be equal in both the inputs, and will be returned as is.
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

(defn sorted-scored-docs
  [s-coll index]
  (let [res (scored-docs-for-str-coll s-coll index)]
    (into (sorted-map-by (fn [key1 key2]
                           (compare [(get-in res [key2 :score]) key2]
                                    [(get-in res [key1 :score]) key1]))) 
          res)))

(defn scored-docs
  [s-coll index & sorted?]
  (if (first sorted?)
    (sorted-scored-docs s-coll index)
    (scored-docs-for-str-coll s-coll index)))

(defn default-fetch-fn
  "The db is expected to be a map with doc-ids as keys and maps as values.
  Each value map may contain the data that needs to come out of the db."
  [db doc-ids]
  (transduce (map db) (into {}) doc-ids))

(defn scored-docs-with-data
  "The db is expected to be a map with doc-ids as keys and maps as values.
   The return value of fetch-fn is expected to be a map or a coll similar to:
   {1 {:data {}} 2 {:data {}}} or [{1 {:data {}}} {2 {:data {}}}]"
  [s-coll index db & opts-map]
  (let [docs (if (:sorted? (first opts-map))
               (scored-docs s-coll index true)
               (scored-docs s-coll index))
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

; (index-keys-from-string s valid-word-fn)
(defn search
  [query-string index & opts-map]
  (let [opts (first opts-map)
        db (:db opts)
        sorted? (:sorted? opts)
        valid-word-fn (:valid-word-fn opts)]
    "WIP - I am here"))


;(scored-docs-for-str-coll ["war" "independence"] test-index)
;(merge-with into (sorted-scored-docs ["war" "independence"] test-index)
;            {1 {:data {}}})

;; TODOs:
;; 1. The sorting of result can be based on another function that the user could provide.
;; 2. Filter searched results based on a minimum score
;; 3. Add more words to stop-words list
