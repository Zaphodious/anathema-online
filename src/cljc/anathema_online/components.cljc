(ns anathema-online.components
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(defprotocol Tuner
  (inst-chan! [t chan-key]
    "Instantiates and adds a chan to the record under the provided chan-key. Returns the Tuner.")
  (close-chan! [t chan-key]
    "Closes a core-async chan that exists in the record under the provided chan-key.
     DOES NOT remove the chan, to avoid null pointer exceptions. Returns the Tuner."))

(defrecord disk [chan-keys buffer-size]

  Tuner
  (inst-chan! [d chan-key]
    (assoc d chan-key (async/chan buffer-size)))
  (close-chan! [d chan-key]
    (async/close! (get d chan-key))
    d)

  component/Lifecycle
  (start [d]
    (->> chan-keys
         (map #(inst-chan! d %))
         (reduce merge)
         (doall)))
  (stop [d]
    (->> chan-keys
         (map #(close-chan! d %))
         (reduce merge)
         (doall))))

(defn new-disk
  [extra-chan-keys] (->disk (into [:read :write :request] extra-chan-keys) 5))
