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
             {:maintain-actual? true})

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
 [{:id 1, :frequency 2}
  {:id 2, :frequency 2}]
 "W600"
 [{:id 1, :frequency 1}
  {:id 2, :frequency 1}]
 "I531" [{:id 2, :frequency 2}]}

#_(build-index [{:id 1 :content "world world war"}
              {:id 2 :content "world's world war of independence indepandance"}
              {:id 3 :content "My name is india\nokay world war"}]
             {:maintain-actual? true})

#_{"W643"
 [{:id 1, :actuals #{"world"}, :frequency 2}
  {:id 2, :actuals #{"worlds" "world"}, :frequency 2}
  {:id 3, :actuals #{"world"}, :frequency 1}]
 "W600"
 [{:id 1, :actuals #{"war"}, :frequency 1}
  {:id 2, :actuals #{"war"}, :frequency 1}
  {:id 3, :actuals #{"war"}, :frequency 1}]
 "I531"
 [{:id 2, :actuals #{"independence" "indepandance"}, :frequency 2}]
 "N500" [{:id 3, :actuals #{"name"}, :frequency 1}]
 "I530" [{:id 3, :actuals #{"india"}, :frequency 1}]
 "O200" [{:id 3, :actuals #{"okay"}, :frequency 1}]}

;(prep-string-coll (clojure.string/split "My name is india\nokay world war" #" "))

#_(def test-index
  {"W643" [{:id 1, :actuals #{"world"}, :frequency 2}
           {:id 2, :actuals #{"worlds" "world"}, :frequency 2}]
   "W600" [{:id 1, :actuals #{"war"}, :frequency 1}
           {:id 2, :actuals #{"war"}, :frequency 1}]
   "I531" [{:id 2, :actuals #{"independence" "indepandance"}, :frequency 2}
           {:id 3, :actuals #{"independence"}, :frequency 1}]})

#_(sorted-scored-docs ["war" "independence"] test-index true)
#_(scored-docs ["war" "independence"] test-index {:sorted? true :increasing? true})
#_(scored-docs ["war" "independence"] test-index nil)
#_(scored-docs-with-data ["war" "independence"] test-index {1 {:data "my-data"}})
#_(scored-docs-with-data ["war" "independence"] test-index {1 {:data "my-data"}}
                       {:sorted? true :increasing? true})

#_(defn my-valid-fn [w] (not= "war" w))

#_(search "war of independence" test-index
        {:db {1 {:data "my-data"}}
         :sorted? true
         :increasing? true
         :valid-word-fn my-valid-fn})
