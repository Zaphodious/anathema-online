(ns anathema-online.components.ui
  (:require [com.stuartsierra.component :as component]
            [anathema-online.past-core :refer [render]]))

(defrecord UIComponent [disk]
  component/Lifecycle
  (start [component]
    (render)
    component)
  (stop [component]
    component))

(defn new-ui-component []
  (->UIComponent nil))
