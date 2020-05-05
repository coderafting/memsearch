(ns memsearch.core
  (:require [memsearch.text.index :as ti]
            [memsearch.text.search :as ts]))

(def text-index ti/text-index)
(def text-search ts/text-search)
