(ns anathema-online.data-test
  (:require [clojure.test :as t]
            [anathema-online.data :as adata]
            [anathema-online.disk :as adisk]
            [clojure.spec.test.alpha :as stest]))

(doall (map stest/instrument
            '[adata/change-game-entity
              adisk/read-object
              adisk/write-object!
              adisk/clear-category!
              adisk/change-object!]))

