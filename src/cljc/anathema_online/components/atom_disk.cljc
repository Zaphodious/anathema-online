(ns anathema-online.components.atom-disk
  (:require [anathema-online.disk :as disk]
            [com.stuartsierra.component :as component]
            [com.rpl.specter :as sp]
            [clojure.core.async :as async]))

(defn read-thing [{:keys [state-atom] :as this} category key]
    (sp/select-first
      [sp/ATOM (sp/keypath category key)]
      state-atom))

(defn write-thing! [{:keys [state-atom] :as this} {:keys [category key] :as object}]
    (sp/transform
      [sp/ATOM (sp/keypath category key)]
      (fn [a] object)
      state-atom))

(defn clear-cats! [{:keys [state-atom] :as this} category]
    (swap! state-atom (fn [a] (dissoc a category))))

(defrecord AtomDiskComponent []
  component/Lifecycle
  (start [this]
    (-> this
      (assoc :state-atom (atom {}))
      (into {:write-object! write-thing!
             :read-object read-thing
             :clear-category! clear-cats!})))
  (stop [this]
    this))

  ;disk/Disk
  ;(-read-object- [{:keys [state-atom] :as this} category key]
  ;  (sp/select-first
  ;    [sp/ATOM (sp/keypath category key)]
  ;    state-atom))
  ;(-write-object!- [{:keys [state-atom] :as this} {:keys [category key] :as object}]
  ;  (sp/transform
  ;    [sp/ATOM (sp/keypath category key)]
  ;    (fn [a] object)
  ;    state-atom))
  ;(-clear-category!- [{:keys [state-atom] :as this} category]
  ;  (swap! state-atom (fn [a] (dissoc a category)))))

(defn new-atom-disk []
  (map->AtomDiskComponent {}))

