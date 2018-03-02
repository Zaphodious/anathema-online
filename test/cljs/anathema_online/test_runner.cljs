(ns anathema-online.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [anathema-online.core-test]
   [anathema-online.common-test]))

(enable-console-print!)

(doo-tests 'anathema-online.core-test
           'anathema-online.common-test)
