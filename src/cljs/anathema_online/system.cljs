(ns anathema-online.system
  (:require [com.stuartsierra.component :as component]
            [anathema-online.components.reactive-ui :refer [new-ui-component]]
            [anathema-online.components.atom-disk :refer [new-atom-disk]]
            [anathema-online.components.service-worker :refer [new-service-worker]]))

(declare system)

(defn new-system []
  (component/system-map
    :service-worker (new-service-worker)
    :disk (new-atom-disk)
    :ui (-> (new-ui-component)
            (component/using [:disk]))))

(defn init []
  (set! system (new-system)))

(defn start []
  (set! system (component/start system)))

(defn stop []
  (set! system (component/stop system)))

(defn ^:export go []
  (init)
  (start))

(defn reset []
  (stop)
  (go))
