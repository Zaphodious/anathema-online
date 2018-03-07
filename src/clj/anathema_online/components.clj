(ns anathema-online.components
  (:require [com.stuartsierra.component :as component]
            [environ.core :as env]
            [cemerick.url :as url]))

(defrecord EnvironmentComponent
  component/Lifecycle
  (start [ec]
    (-> ec
        (assoc :db-uri (url/url-decode (env :mongodb-uri "")))))
  (stop [ec]
    ec))

