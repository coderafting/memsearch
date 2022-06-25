(ns memsearch.test-core
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [memsearch.core :as m]
   [memsearch.text.search :as ms]))

(deftest sample-test
  (testing "Sample"
    (is (= 1 1))))

(def sample-data
  [{:id 1 :content "I've been programming in functional and procedural programming languages. The functional language is Clojure, and the procedural language is Go."}
   {:id 2 :content "Music pleases soul and mind. We can generate music using Clojure"}])

;; Key checks to cover:
;; - Create index, with maintaining original too
;; - Check if stop words and unexpected characters are not included in the index
;; - Check if the frequency matches in the index as expected
;; - Check is the search is working as expected, with spelling mistakes

(deftest text-index
  (let [index (m/text-index sample-data {:maintain-actual? true})]
    (testing "Indexing and Search"
      (is (= 12 (count index)))

      (let [indexed-docs (ms/fetch-docs-for-a-word "clojure" index)]
        (is (= 2 (count indexed-docs))))

      (let [indexed-docs (ms/fetch-docs-for-a-word "closure" index)]
        (is (= 2 (count indexed-docs))))

      (let [indexed-docs (ms/fetch-docs-for-a-word "clojur" index)]
        (is (= 2 (count indexed-docs))))

      (let [indexed-docs (ms/fetch-docs-for-a-word "language" index)
            {:keys [id actuals frequency]} (first indexed-docs)]
        (is (= 1 (count indexed-docs)))
        (is (and (= 1 id) (= 3 frequency) (= #{"languages" "language"} actuals)))))))

(deftest text-search
  (let [index (m/text-index sample-data)]
    (testing "Simple search"
      (let [result (m/text-search "language" index)]
        (is (= 1 (count result)))
        (is (get result 1))))
    
    (testing "Search with spelling variations"
      (let [result (m/text-search "languaze" index)]
        (is (= 1 (count result)))
        (is (get result 1)))
      
      (let [result (m/text-search "languazes" index)]
        (is (= 1 (count result)))
        (is (get result 1))))))
