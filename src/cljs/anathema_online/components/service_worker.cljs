(ns anathema-online.components.service-worker
  (:require [com.stuartsierra.component :as component]
            [hireling.core :as hireling]))

(defrecord Service-Worker-Component []
  component/Lifecycle
  (start [this]
    (hireling/register-service-worker "/service_worker.js")
    this)
  (stop [this] this))

(defn new-service-worker []
  (map->Service-Worker-Component {}))