(ns memsearch.test-core
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [memsearch.core :as m]))

(deftest sample-test
  (testing "Sample"
    (is ( = 1 1))))

(def sample-data
  [{:id 1 :content "I’ve been programming in functional and procedural programming languages. The functional language is Clojure, and the procedural language is Go."}
   {:id 2 :content "Music pleases soul and mind."}])

;; Key checks to cover:
;; - Create index, with maintaining original too
;; - Check if the frequency matches in the index as expected
;; - 

;; TODO
;; Complete test and benchmarks

;;(def test-data "/testdata/data.edn")
;(/ (count (vec (str (mapv #(:content %) test-data)))) 3)

#_(def test-db
  (into {}
        (map #(hash-map (:id %) {:content (:content %)}) test-data)))

#_(def sample-index (m/text-index test-data {:maintain-actual? true}))

#_(time (m/text-index test-data {:maintain-actual? true}))
#_(count (m/text-index test-data {:maintain-actual? true}))
; about 45ms to index by parsing about 1500 words to generate 180 unique index keys with their values
; about 90ms to index by parsing about 3800 words to generate about 454 unique index keys with their values
; will have to test with a different same sizes to get benchmarks for different scale.

#_(time (m/text-search "object oriented programming" sample-index {:db test-db :sorted? true}))
;; 17ms, 10 results, includes data fetch from the db, with 25 docs (with about 60 words each)

#_(time (m/text-search "knuth on programming" sample-index {:db test-db :sorted? true}))
;; 23ms, 16 results, includes data fetch from the db, with 50 docs (with about 75 words each)

#_(time (m/text-search "knuth on programming" sample-index))
;; 18ms, without data merge from db and without sorting

;; Benchmark with :maintain-actual? false
