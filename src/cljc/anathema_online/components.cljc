(ns anathema-online.components
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(defrecord disk []
  component/Lifecycle
  (start [d]
    (let [read-chan (async/chan 5)
          write-chan (async/chan 5)
          write-mult (async/mult write-chan)]
      (-> d
          (assoc :read-chan read-chan)
          (assoc :write-chan write-chan)
          (assoc :write-mult write-mult))))
  (stop [d]
    (async/close! (:read-chan d))
    (async/close! (:write-chan d))
    d))

(defn disk-read
  "Fetches the data under the path, returns as a core.async channel."
  [d path]
  (let [c (async/chan)]
    (async/go (async/>! (:read-chan d) [path c])
              (async/<! c))))

(defn disk-write!
  "Requests that the app state be placed onto the disk under the provided path. Returns the disk."
  [d path object]
  (async/go (async/>! (:write-chan d) [path object]))
  d)

(defn disk-change!
  "Changes the data under the path using the provided function. Returns the disk."
  [d path fun]
  (disk-write! d path (fun (disk-read d path)))
  d)