(ns anathema-online.components.server-ui
  "UI component for rendering on the server."
  (:require [rum.core :as rum]
            [anathema-online.ui :as ui]
            [com.stuartsierra.component :as component]))

(defrecord ServerUI [init-component dom-root change-fn view-atom disk]
  component/Lifecycle
  (start [this]
    (assoc this :change-fn (ui/make-update-fn this)))
  (stop [this]
    this))

(defn new-ui-component []
  (map->ServerUI {:view-atom (atom {}), :init-component ui/fallback-initial-component}))