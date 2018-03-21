(ns anathema-online.components.service-worker
  (:require [com.stuartsierra.component :as component]))

(def worker-path (if js/goog.DEBUG
                   "jshelper/bootstrap_worker.js"
                   "js/compiled/worker.js"))

(defn is-service-worker-supported?
  []
  (exists? js/navigator.serviceWorker))

(defn register-service-worker
  [path-to-sw]
  (if (is-service-worker-supported?)
    (-> js/navigator
        .-serviceWorker
        (.register path-to-sw)
        (.then #(js/console.log (str "Service Worker Registered[" path-to-sw "]"))))))

(defrecord Service-Worker-Component []
  component/Lifecycle
  (start [this]
    (register-service-worker worker-path)
    this)
  (stop [this] this))

(defn new-service-worker []
  (map->Service-Worker-Component {}))