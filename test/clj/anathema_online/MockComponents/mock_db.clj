(ns anathema-online.MockComponents.mock-db
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [com.rpl.specter :as sp]))

(defn spin-up-writer [{state :state {:keys [write-mult]} :disk :as mockDB}]
  (async/go-loop []
    (let [write-chan (async/tap write-mult (async/chan))
          {:keys [category id view] :as write-request} (async/<! write-chan)]
      (println "writing " id " in category " category " as " view)
      (sp/transform [sp/ATOM (sp/keypath category id)] (constantly view) state))
    (recur))
  mockDB)

(defn spin-up-reader [{state :state {:keys [read-chan]} :disk :as mockDB}]
  (async/go-loop []
    (let [{:keys [category id channel] :as read-request} (async/<! read-chan)]
      (println "request is " read-request)
      (println "mockDB is " mockDB)
      (async/>! channel (-> @state (get category) (get id))))
    (recur))
  mockDB)

(defrecord MockDBComponent []
  component/Lifecycle
  (start [mockDB]
    (-> mockDB
        (assoc :state (atom {}))
        (spin-up-reader)
        (spin-up-writer)))
  (stop [mockDB]
    mockDB))

(defn new-mockDB [] (->MockDBComponent))