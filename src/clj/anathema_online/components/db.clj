(ns anathema-online.components.db
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [gzip-util.core :as gz]
            [clojure.java.io :as io]
            [com.rpl.specter :as sp]
            [clojure.data :as cd]
            [byte-transforms :as bt]
            [byte-streams :as bs]
            [cemerick.url :as url :refer [url-decode url-encode]]
            [clojure.core.async :as async]))


(def entity-collection "entities")
(def player-collection "players")

(defn- connect-to-db! [db-uri]
  (mg/connect-via-uri db-uri))

(defn- put-entity-in-db! [db thingy]
  (mc/update db entity-collection
             {:_id (or (:key thingy) "nill")}
             {:name     (:name thingy)
              :category (:category thingy)
              :type     (:type thingy)
              :subtype  (:subtype thingy)
              :player    (:player thingy)
              :data     (gz/str->gzipped-bytes (url-encode (pr-str thingy)))}
             {:upsert true}))

(defn- extract-data [entry-map]
  (url-decode (gz/gzipped-input-stream->str (io/input-stream (:data entry-map)) "UTF-8")))
(defn- get-entity-from-db [db key]
  (read-string (extract-data (mc/find-map-by-id db entity-collection key))))

(defn- all-entities-to-file [db filename]
  (spit (str filename ".edn")
        (->> (mc/find-maps db entity-collection)
             (map extract-data)
             (map read-string)
             (vec))))

(defn- all-players-to-file [db filename]
  (spit (str filename ".edn")
        (vec (mc/find-maps db player-collection))))

(defn- put-player-in-db! [db player-thing]
  (mc/update db player-collection
             {:_id (or (:key player-thing "nill"))}
             player-thing
             {:upsert true}))

(defn- get-player-from-db [db player-id]
  (assoc (dissoc (mc/find-map-by-id db player-collection player-id) :_id) :category :player))

(defn- put-thing-in-db! [db thingy]
  (let [isPlayer? (= :player (:category thingy))
        write-fn (if isPlayer? put-player-in-db! put-entity-in-db!)]
    (write-fn db thingy)))

(defn- get-path-parent-from-db [db path]
  (let [category (first path)
        id (second path)
        get-fn (if (= :player category)
                 get-player-from-db
                 get-entity-from-db)]
    (get-fn db id)))

(defn backup-dev-db! [db]
  (do
    (all-entities-to-file db "dev-entity-backup")
    (all-players-to-file db "dev-player-backup")))

(defn restore-dev-db! [db]
  (let [entity-insert-result (map
                               #(put-thing-in-db! db %)
                               (read-string (slurp "dev-entity-backup.edn")))
        player-insert-result (map
                               #(put-thing-in-db! db %)
                               (read-string (slurp "dev-player-backup.edn")))]
    {:players player-insert-result
     :entity entity-insert-result}))

(defn spinup-db-writer [db write-mult]
  (let [sub-chan (async/chan 5)]
       (async/tap write-mult sub-chan)
       (async/go-loop
         []
         (when-let [{:keys [path view]} (async/<! sub-chan)]
           (println "Writing " view " to " path)
           (let [parent-view (get-path-parent-from-db db path)
                 mod-view (sp/transform [(apply sp/keypath (rest (rest path)))]
                                        (fn [a] view)
                                        parent-view)]
             (println "Further, this is written- " mod-view)))
         (recur))))

(defrecord DBComponent []
  component/Lifecycle
  (start [dbc]
    (let [{:keys [db-uri]} (:environ dbc)
          {:keys [write-mult]} (:disk dbc)
          {:keys [db conn] :as connection-map} (connect-to-db! db-uri)]
      (println "--- Starting the DB thing!")
      (spinup-db-writer db write-mult)
      (into dbc connection-map)))
  (stop [dbc]
    (mg/disconnect (:conn dbc))
    dbc))

(defn new-db []
  (->DBComponent))


