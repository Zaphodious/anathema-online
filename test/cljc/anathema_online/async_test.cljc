(ns anathema-online.async-test
  (:require [clojure.test :as test :refer [deftest]]
            [clojure.core.async :as async]))


(defn async-test [chan]
  #?(:clj (async/<!! chan)
     :cljs (test/async done
             (async/take! chan (fn [_] (done))))))