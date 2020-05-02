(ns memsearch.test-core
  (:require
    [cljs.test :refer-macros [is are deftest testing use-fixtures]]))

(deftest example
  (is (= 0 1)))

;(prep-string-coll (clojure.string/split "World war\nof 2 he's independence" #" "))
;(index-keys-from-string "World war\nof 2 he's independence")
; => ("world" "war" "independence")

;(index-map-from-doc {:id 1 :content "India india independence"})
;(index-map-from-doc {:id 1 :content "India india's independence indepandance"} true)

#_(build-index [{:id 1 :content "world world war"}
              {:id 2 :content "world's world war of independence indepandance"}]
             true)

#_{"W643"
 [{:id 1, :actuals #{"world"}, :frequency 2}
  {:id 2, :actuals #{"worlds" "world"}, :frequency 2}]
 "W600"
 [{:id 1, :actuals #{"war"}, :frequency 1}
  {:id 2, :actuals #{"war"}, :frequency 1}]
 "I531"
 [{:id 2, :actuals #{"independence" "indepandance"}, :frequency 2}]}

#_(build-index [{:id 1 :content "world world war"}
              {:id 2 :content "world's world war of independence indepandance"}])

#_{"W643"
 [{:id 1, :actuals nil, :frequency 2}
  {:id 2, :actuals nil, :frequency 2}]
 "W600"
 [{:id 1, :actuals nil, :frequency 1}
  {:id 2, :actuals nil, :frequency 1}]
 "I531" [{:id 2, :actuals nil, :frequency 2}]}