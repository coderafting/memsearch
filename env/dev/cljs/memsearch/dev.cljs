(ns ^:figwheel-no-load memsearch.dev
  (:require
    [memsearch.test-page :as test-page]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(test-page/init!)
