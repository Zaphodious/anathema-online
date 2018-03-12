(ns anathema-online.data
  (:require [clojure.spec.alpha :as s]
            [com.rpl.specter :as sp]
            [clojure.spec.gen.alpha :as sg]
            [hashids.core :as h])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def ^:private hash-ops {:salt "Exalted Is Best Game!"})
(defn new-id []
  (h/encode hash-ops (rand-int 99999) (rand-int 99999) (rand-int 99999)))



