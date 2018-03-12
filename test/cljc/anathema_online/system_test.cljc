(ns anathema-online.system-test
  (:require [clojure.test :as test]
            [com.stuartsierra.component :as component]
            [anathema-online.components.atom-disk :as atom-disk]
            [anathema-online.async-test :as ast]
            [clojure.core.async :as async]
            [anathema-online.disk :as disk]))

(def test-data-1 {:category :test-things
                  :id "12345"
                  :name "Happiness"})

(def system-atom
  (atom nil))

(defn refresh-system-atom []
  (reset! system-atom
    (component/start-system
      (component/system-map
        :disk (atom-disk/new-atom-disk)))))

(test/deftest testing-system-works
  (ast/async-test
    (async/go
      (test/is (= 2 2)))))

(defn ensure-system-running-fixture [fun]
  (if @system-atom
    (fun)
    (do (refresh-system-atom)
        fun)))

(defn clear-test-category-fixture [fun]
  (do (disk/-clear-category!- (:disk @system-atom) (:category test-data-1))
      (fun)))

(test/use-fixtures
  :once ensure-system-running-fixture
  :each clear-test-category-fixture)




