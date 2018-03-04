(ns anathema-online.data
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [gzip-util.core :as gz]
            [clojure.java.io :as io])
  (:import org.bson.types.ObjectId))

(def db-url (env :mongodb-uri ""))
(def _prelim-con (mg/connect-via-uri db-url))
(def conn (:conn _prelim-con))
(def db (:db _prelim-con))

(defn put-in-db! [category id thingy]
  ())

(defn get-from-db [category id]
  (let [{:keys [data]} (mc/find-map-by-id db (name category) id)]
    (read-string
      (gz/gzipped-input-stream->str
        (io/input-stream data)
        "utf-8"))))