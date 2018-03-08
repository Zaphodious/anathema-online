(ns anathema-online.MockComponents.mock-db
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [com.rpl.specter :as sp]))

(defn spin-up-writer [{state :state {:keys [write-mult]} :disk :as mockDB}]
  (async/go-loop []
    (let [write-chan (async/tap write-mult (async/chan 5))
          {:keys [category id object] :as write-request} (async/<! write-chan)]
     (sp/transform [sp/ATOM (sp/keypath category id)] (constantly object) state))
    (recur))
  mockDB)

(defn spin-up-reader [{state :state {:keys [read-chan]} :disk :as mockDB}]
  (async/go-loop []
    (let [{:keys [category id return-chan] :as read-request} (async/<! read-chan)]
      (async/>! return-chan (sp/select-first [sp/ATOM (sp/keypath category id)] state)))
    (recur))
  mockDB)

(defrecord MockDBComponent
  component/Lifecycle
  (start [mockDB]
    (-> mockDB
        (assoc :state (atom {}))
        (spin-up-writer)
        (spin-up-reader)))
  (stop [mockDB]
    mockDB))