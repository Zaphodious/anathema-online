(ns anathema-online.components.ui
  (:require [com.stuartsierra.component :as component]
            [anathema-online.core :refer [render]]))

(defrecord UIComponent []
  component/Lifecycle
  (start [component]
    (render)
    component)
  (stop [component]
    component))

(defn new-ui-component []
  (map->UIComponent {}))
