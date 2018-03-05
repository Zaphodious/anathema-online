(ns anathema-online.data
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [gzip-util.core :as gz]
            [clojure.java.io :as io]
            [com.rpl.specter :as sp]
            [clojure.data :as cd]
            [byte-transforms :as bt]
            [byte-streams :as bs]
            [cemerick.url :as url :refer [url-decode url-encode]])
  (:import org.bson.types.ObjectId))

(def db-url (url-decode (env :mongodb-uri "")))
(def _prelim-con (mg/connect-via-uri db-url))
(def conn (:conn _prelim-con))
(def db (:db _prelim-con))
(def entity-collection "entities")
(def player-collection "players")

(defn put-entity-in-db! [thingy]
  (mc/update db entity-collection
             {:_id (or (:key thingy) "nill")}
             {:name     (:name thingy)
              :category (:category thingy)
              :type     (:type thingy)
              :subtype  (:subtype thingy)
              :player    (:player thingy)
              :data     (gz/str->gzipped-bytes (url-encode (pr-str thingy)))}
             {:upsert true}))

(defn extract-data [entry-map]
  (url-decode (gz/gzipped-input-stream->str (io/input-stream (:data entry-map)) "UTF-8")))
(defn get-entity-from-db [key]
  (read-string (extract-data (mc/find-map-by-id db entity-collection key))))

(defn all-entities-to-file [filename]
  (spit (str filename ".edn")
        (->> (mc/find-maps db entity-collection)
             (map extract-data)
             (map read-string)
             (vec))))

(defn all-players-to-file [filename]
  (spit (str filename ".edn")
        (vec (mc/find-maps db player-collection))))

(defn put-player-in-db! [player-thing]
  (mc/update db player-collection
             {:_id (or (:key player-thing "nill"))}
             player-thing
             {:upsert true}))

(defn get-player-from-db [player-id]
  (dissoc (mc/find-map-by-id db player-collection player-id) :_id))

(defn backup-dev-db! []
  (do
    (all-entities-to-file "dev-entity-backup")
    (all-players-to-file "dev-player-backup")))

(defn restore-dev-db! []
  (let [entity-insert-result (map
                               put-entity-in-db!
                               (read-string (slurp "dev-entity-backup.edn")))
        player-insert-result (map
                               put-player-in-db!
                               (read-string (slurp "dev-player-backup.edn")))]
       {:players player-insert-result
        :entity entity-insert-result}))


