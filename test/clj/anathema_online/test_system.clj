(ns anathema-online.test-system
  (:require [clojure.test :as test :refer [deftest use-fixtures is]]
            [reloaded.repl :as rr :refer [system]]
            [com.stuartsierra.component :as component :refer [using start-system stop-system]]
            [anathema-online.async :as ao-async]
            [anathema-online.components.disk :as c-disk]
            [anathema-online.components.mongo :as c-db]
            [clojure.core.async :as async]))

(def test-data {:name (str (rand))
                :key "12345"
                :category :test-thing
                :funny-thing "Unusual"})

(defn ensure-system-running-fixture [fun]
  (if reloaded.repl/system
    (fun)
    (throw (RuntimeException. "Please spin-up development system before beginning test."))))

(defn ensure-db-cleared-fixture [fun]
  (do
    (println "running clear fixture.")
    (c-db/remove-all-in-category (:db (:db system))
                                 (:category test-data))
    (fun)))

(deftest disk-reads-and-writes
  (let [write-result-chan (c-disk/disk-write! (:disk system)
                                              :test-thing
                                              (:key test-data)
                                              test-data)
        read-result-atom (c-disk/disk-read (:disk system)
                                           :test-thing
                                           (:key test-data))]
    (loop [atom-result nil]
      (if atom-result
        (is (= test-data atom-result))
        (recur @read-result-atom)))))

(use-fixtures
  :each ensure-db-cleared-fixture
  :once ensure-system-running-fixture)








