(ns anathema-online.components.reactive-ui
  (:require [com.stuartsierra.component :as component]
            [anathema-online.past-core :refer [render]]
            [anathema-online.ui :as ui]
            [rum.core :as rum]))



(defrecord ReactiveUIComponent [init-component dom-root change-fn view-atom disk url-path]
  component/Lifecycle
  (start [component]
    (let [{:keys [init-component dom-root change-fn view-atom disk url-path] :as initialized-this}
          (-> component
              (assoc :dom-root (. js/document (getElementById "app")))
              (assoc :url-path (-> js/window .-location))
              (ui/init-with-update-fn))]
      (rum/mount (init-component initialized-this) dom-root)
      initialized-this))

  (stop [component]
    component))

(defn new-ui-component []
  (map->ReactiveUIComponent {:view-atom (atom {}), :init-component ui/fallback-initial-component}))

