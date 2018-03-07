(ns anathema-online.components.environ
  (:require [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [cemerick.url :as url]))

(defrecord EnvironmentComponent []
  component/Lifecycle
  (start [ec]
    (-> ec
        (assoc :db-uri (url/url-decode (env :mongodb-uri "")))))
  (stop [ec]
    ec))

(defn new-environ []
  (->EnvironmentComponent))