(ns memsearch.runner
  (:require
    [doo.runner :refer-macros [doo-tests]]
    [memsearch.test-core]))

(doo-tests 'memsearch.test-core)
