(ns anathema-online.components.atom-disk
  (:require [anathema-online.disk :as disk]
            [com.stuartsierra.component :as component]
            [com.rpl.specter :as sp]
            [clojure.core.async :as async]))

(defrecord AtomDiskComponent []
  component/Lifecycle
  disk/Disk
  (start [this]
    (assoc this :state-atom (atom {})))
  (stop [this]
    this)
  (read-object [{:keys [state-atom] :as this} category key]
    (sp/select-first
      [sp/ATOM (sp/keypath category key)]
      state-atom))
  (write-object! [{:keys [state-atom] :as this} {:keys [category key] :as object}]
    (async/go
      (sp/transform
        [sp/ATOM (sp/keypath category key)]
        (fn [a] object)
        state-atom)
      this)))

(defn new-atom-disk []
  (->AtomDiskComponent))

