(ns 
 ^{:todo {1 "Save index to disk"
          2 "Load index in app memory"
          3 "Append, Update, and Delete docs to/from index"
          4 "Sync with the saved index on every dynamic mutation of the index"}}
  memsearch.text.index
  (:require [memsearch.text.stopwords :as sw]
            [clj-fuzzy.phonetics :as ph]))

(defn 
  ^{:doc "This is the default string sanitization function."
    :todo "An option should be provided to users to allow for custom prep functions."}
  prep-string
  [s]
  (-> s
      (clojure.string/replace "\n" " ")
      (clojure.string/replace #"[^A-Za-z0-9 ]" "")
      (clojure.string/lower-case)))

(defn 
  ^{:doc "This is the default word validation function."
    :todo "An option should be provided to users to allow for custom validator functions."}
  valid-word?
  [s]
  (let [st (clojure.string/trim (clojure.string/replace s "\n" " "))]
    (and
     (not (sw/stop-words (clojure.string/lower-case st)))
     (> (count st) 1))))

(declare prep-string-coll)

(defn string-vec-helper
  [s]
  (let [str-coll (clojure.string/split s #" ")]
    (if (> (count str-coll) 1)
      (prep-string-coll str-coll)
      s)))

(defn prep-string-coll
  "Creates a collection of prepped and valid words. Input is a collection of strings.
   Users may provide their own `valid-word-fn`."
  [str-coll & valid-word-fn]
  (flatten (remove nil? (map #(if (if (first valid-word-fn) ((first valid-word-fn) %) (valid-word? %))
                                (string-vec-helper (prep-string %)))
                             str-coll))))

(defn index-keys-from-string
  "Creates a collection of prepped and valid words from a string.
   Users may provide their own `valid-word-fn`."
  ([s] (prep-string-coll (clojure.string/split s #" ")))
  ([s valid-word-fn] (prep-string-coll (clojure.string/split s #" ") valid-word-fn)))

(defn index-map-from-doc
  "Builds an index map from a document. A document is a map with two keys - `:id` and `:content`.
   The `:id` is the unique identifier for the document that the users can use during search to get the actual document.
   The `:content` key is the string whose words will be indexed.
   Users may provide an opts-map with keys `:maintain-actual?` and `:valid-word-fn`.
    - When `:maintain-actual?` is `true`, the actual indexed words along with the encoded form of the words.
    - The `:valid-word-fn` is a custom word validator that users may provide.
   Note that maintaining actual words will consume additional space.
   Sample input: 
   ```
   (index-map-from-doc {:id 1 :content \"World War 1\"} {:maintain-actual? true})
   ```
   Sample output: 
   ``` 
   {\"W643\" [{:id 1, :actuals #{\"world\"}, :frequency 1}]
    \"W600\" [{:id 1, :actuals #{\"war\"}, :frequency 1}]}
   ```
   The `:id` is the same as supplied by the user. 
   The value of `:frequency` is the frequency of the word in the `:content` string."
  [{:keys [id content]} & opts-map]
  (let [valid-word-fn (:valid-word-fn (first opts-map))
        keys-coll (if valid-word-fn 
                    (index-keys-from-string content valid-word-fn) 
                    (index-keys-from-string content))]
    (loop [ks keys-coll
           res {}]
      (if (first ks)
        (recur (rest ks) 
               (let [soundex-code (ph/soundex (first ks))]
                 (assoc res soundex-code
                        [(if (:maintain-actual? (first opts-map))
                           {:id id
                            :actuals (set (conj (:actuals (first (res soundex-code))) (first ks)))
                            :frequency (inc (:frequency (first (res soundex-code))))}
                           {:id id
                            :frequency (inc (:frequency (first (res soundex-code))))})])))
        res))))

(defn text-index
  "Builds the final index map from a collection of documents. A document is a map with two keys - `:id` and `:content`.
   The `:id` is the unique identifier for the document that the users can use during search to get the actual document.
   The `:content` key is the string whose words will be indexed.
   Users may provide an opts-map with keys `:maintain-actual?` and `:valid-word-fn`.
    - When `:maintain-actual?` is `true`, the actual indexed words are saved along with the encoded form of the words.
    - The value of `:valid-word-fn` is a custom word validator that users may provide.
   The value of `:valid-word-fn` is a single arity fn that takes one word (string) and returns boolean.
   Note that maintaining actual words will consume additional space.
   Sample input: 
   ```
   (text-index [{:id 1 :content \"World war 1\"}
                {:id 2 :content \"Independence for the world\"}]
               {:maintain-actual? true})
   ```
   Sample output: 
   ```
   {\"W643\" [{:id 1, :actuals #{\"world\"}, :frequency 1}
              {:id 2, :actuals #{\"world\"}, :frequency 1}]
    \"W600\" [{:id 1, :actuals #{\"war\"}, :frequency 1}]
    \"I531\" [{:id 2, :actuals #{\"independence\"}, :frequency 1}]}
   ```
   The `:id` is the same as supplied by the user. 
   The value of `:frequency` is the frequency of the word in the `:content` string."
  [doc-coll & opts-map]
  (loop [docs doc-coll
         res {}]
    (if (first docs)
      (recur (rest docs) (let [i (if (first opts-map)
                                   (index-map-from-doc (first docs) (first opts-map))
                                   (index-map-from-doc (first docs)))]
                           (merge-with into res i)))
      res)))
