(ns anathema-online.data
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [gzip-util.core :as gz]
            [clojure.java.io :as io]
            [cemerick.url :as url :refer [url-decode]])
  (:import org.bson.types.ObjectId))

(def db-url (url-decode (env :mongodb-uri "")))
(def _prelim-con (mg/connect-via-uri db-url))
(def conn (:conn _prelim-con))
(def db (:db _prelim-con))
(def collection "entities")

(defn put-in-db! [thingy]
  (mc/update db collection
             {:_id (:key thingy)}
             {:name (:name thingy)
              :category (:category thingy)
              :type (:type thingy)
              :subtype (:subtype thingy)
              :data (gz/str->gzipped-bytes (pr-str thingy))}
             {:upsert true}))

(defn extract-data [entry-map]
  (gz/gzipped-input-stream->str (io/input-stream (:data entry-map)) "UTF-8"))
(defn get-from-db [key]
  (read-string (extract-data (mc/find-map-by-id db collection key))))

(defn all-entities-to-file [filename]
  (spit (str filename ".edn")
        (->> (mc/find-maps db collection)
             (map extract-data)
             (map read-string)
             (vec))))

(defn backup-dev-db! []
  (all-entities-to-file "dev-backup"))

(defn restore-dev-db! []
  (map
    put-in-db!
    (read-string (slurp "dev-backup.edn"))))