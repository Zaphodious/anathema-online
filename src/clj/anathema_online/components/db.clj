(ns anathema-online.components.db
  (:require [com.stuartsierra.component :as component]
            [anathema-online.data :as anath-data]))

(defrecord DBComponent []
  component/Lifecycle
  (start [dbc]
    (let [{:keys [db-uri]} (:environ dbc)
          {:keys [db conn]} (anath-data/connect-to-db! db-uri)]
      (println "--- Starting the DB thing!")
      (assoc dbc :db db)))
  (stop [dbc]
    dbc))

(defn new-db []
  (->DBComponent))