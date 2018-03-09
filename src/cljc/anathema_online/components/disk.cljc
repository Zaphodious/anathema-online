(ns anathema-online.components.disk
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(defrecord DiskComponent []
  component/Lifecycle
  (start [d]
    (let [read-chan (async/chan)
          write-chan (async/chan)
          write-mult (async/mult write-chan)]
      (-> d
          (assoc :read-chan read-chan)
          (assoc :write-chan write-chan)
          (assoc :write-mult write-mult))))
  (stop [d]
    (async/close! (:read-chan d))
    (async/close! (:write-chan d))
    d))

(defn new-disk []
  (->DiskComponent))

(defn disk-read
  "Fetches the data under the category and id, returns as a core.async channel."
  [d category id]
  (let [c (async/chan)]
    (async/go (async/>! (:read-chan d) {:category category :id id :channel c :request-type :disk-read})
              (async/<! c))))

(defn disk-write!
  "Requests that the replacement object be put under the category and id. Returns a channel onto which the success of the put will be put."
  [d category id object]
  (async/go (async/>! (:write-chan d) object)))
